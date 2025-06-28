package com.karacam.stock_service.services;

import com.karacam.stock_service.entities.IncomingStockOrder;
import com.karacam.stock_service.entities.OutboxStockOrder;
import com.karacam.stock_service.entities.types.OrderExecution;
import com.karacam.stock_service.enums.OrderSide;
import com.karacam.stock_service.enums.OrderStatus;
import com.karacam.stock_service.enums.OrderType;
import com.karacam.stock_service.gen.AppExecutionReportOuterClass;
import com.karacam.stock_service.gen.IncomingOrder;
import com.karacam.stock_service.models.OrderInfo;
import com.karacam.stock_service.models.OrderInfoShort;
import com.karacam.stock_service.repositories.IncomingStockOrderRepository;
import com.karacam.stock_service.repositories.OutboxStockOrderRepository;
import com.karacam.stock_service.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final RedisService redisService;
    private final IncomingStockOrderRepository incomingStockOrderRepository;
    private final OutboxStockOrderRepository outboxStockOrderRepository;

    private final String orderSetKeyPrefix = "order_set_key_prefix:";
    private String orderSetKey;

    @Autowired
    public OrderService(RedisService redisService_, IncomingStockOrderRepository incomingStockOrderRepository_, OutboxStockOrderRepository outboxStockOrderRepository_) {
        this.redisService = redisService_;
        this.incomingStockOrderRepository = incomingStockOrderRepository_;
        this.outboxStockOrderRepository = outboxStockOrderRepository_;

        orderSetKey = orderSetKeyPrefix.concat(TimeUtil.getToday());
    }

    @KafkaListener(topics = "incoming_orders", groupId = "stock_service_incoming_order_consumer")
    @Transactional
    public void listenToIncomingOrders(IncomingOrder.OrderMessage order) {
        String orderId = generateHashForIncomingOrder(order);
        Instant timeStamp = TimeUtil.protoTimestampToInstant(order.getTimestamp());

        if (this.redisService.checkAndSet(orderSetKey, orderId)) {
            IncomingStockOrder incomingStockOrder = IncomingStockOrder.builder()
                    .orderId(orderId)
                    .userId(order.getUserId())
                    .symbol(order.getSymbol())
                    .price(order.getPrice())
                    .quantity(order.getQuantity())
                    .type(order.getType() == IncomingOrder.OrderType.MARKET ? OrderType.MARKET : OrderType.LIMIT)
                    .side(order.getSide() == IncomingOrder.OrderSide.BUY ? OrderSide.BUY : OrderSide.SELL)
                    .status(OrderStatus.NEW)
                    .fulfilledQuantity(0)
                    .timestamp(timeStamp)
                    .build();

            OutboxStockOrder outboxStockOrder = OutboxStockOrder.builder()
                    .orderId(orderId)
                    .symbol(order.getSymbol())
                    .price(order.getPrice())
                    .quantity(order.getQuantity())
                    .type(order.getType() == IncomingOrder.OrderType.MARKET ? OrderType.MARKET : OrderType.LIMIT)
                    .side(order.getSide() == IncomingOrder.OrderSide.BUY ? OrderSide.BUY : OrderSide.SELL)
                    .timestamp(timeStamp)
                    .build();

            incomingStockOrderRepository.save(incomingStockOrder);
            outboxStockOrderRepository.save(outboxStockOrder);
        }
    }

    @KafkaListener(topics = "execution_reports", groupId = "stock_service_execution_report_consumer")
    @Transactional
    public void listenToExecutionReports(AppExecutionReportOuterClass.AppExecutionReport appExecutionReport) {
        Optional<IncomingStockOrder> incomingStockOrderOptional = this.incomingStockOrderRepository.findByOrderId(appExecutionReport.getOrderId());
        if (incomingStockOrderOptional.isEmpty()) {
            // implement dead letter queue
        }

        OrderExecution orderExecution = OrderExecution.builder()
                .timeStamp(appExecutionReport.getTimestamp())
                .executionPrice(appExecutionReport.getExecutionPrice())
                .volume(appExecutionReport.getVolume())
                .build();

        IncomingStockOrder incomingOrder = incomingStockOrderOptional.get();
        incomingOrder.getExecutionList().add(orderExecution);
        int newFulfilledQuantity = incomingOrder.getFulfilledQuantity() + appExecutionReport.getVolume();
        incomingOrder.setFulfilledQuantity(newFulfilledQuantity);
        if (newFulfilledQuantity < incomingOrder.getQuantity()) {
            incomingOrder.setStatus(OrderStatus.PARTIALLY_FULFILLED);
        } else {
            incomingOrder.setStatus(OrderStatus.FULFILLED);
        }
        this.incomingStockOrderRepository.save(incomingOrder);
    }

    public List<OrderInfoShort> getOrderInfoShortList(int userId) {
        List<IncomingStockOrder> stockOrders = this.incomingStockOrderRepository.findByUserId(userId);

        return stockOrders.stream().map(iteratedStockOrder -> {
            double total = getExecutedOrderTotal(iteratedStockOrder);
            return OrderInfoShort.builder()
                    .symbol(iteratedStockOrder.getSymbol())
                    .orderId(iteratedStockOrder.getOrderId())
                    .total(total)
                    .build();
        }).toList();
    }

    public OrderInfo getOrderInfo(int userId, String orderId) {
        Optional<IncomingStockOrder> incomingStockOrderOptional = this.incomingStockOrderRepository.findByOrderId(orderId);
        if (incomingStockOrderOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        IncomingStockOrder incomingStockOrder = incomingStockOrderOptional.get();

        if (incomingStockOrder.getUserId() != userId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owners can access order information");
        }

        return OrderInfo.builder()
                .orderId(incomingStockOrder.getOrderId())
                .symbol(incomingStockOrder.getSymbol())
                .price(incomingStockOrder.getPrice())
                .quantity(incomingStockOrder.getQuantity())
                .side(incomingStockOrder.getSide())
                .type(incomingStockOrder.getType())
                .status(incomingStockOrder.getStatus())
                .executionList(incomingStockOrder.getExecutionList())
                .total(getExecutedOrderTotal(incomingStockOrder))
                .build();
    }


    private String generateHashForIncomingOrder(IncomingOrder.OrderMessage order) {
        try {
            byte[] serializedOrder = order.toByteArray();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(serializedOrder);
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate hash for message", e);
        }
    }

    private double getExecutedOrderTotal(IncomingStockOrder order) {
        return order.getExecutionList().stream().mapToDouble(OrderExecution::getExecutionPrice).sum();

    }

    @Scheduled(cron = "0 0 3 * * *")
    protected void updateOrderSetKey() {
        orderSetKey = orderSetKeyPrefix.concat(TimeUtil.getToday());
    }
}
