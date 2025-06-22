package com.karacam.stock_service.services;

import com.karacam.stock_service.entities.IncomingStockOrder;
import com.karacam.stock_service.entities.OutboxStockOrder;
import com.karacam.stock_service.gen.IncomingOrder;
import com.karacam.stock_service.models.OrderSide;
import com.karacam.stock_service.models.OrderStatus;
import com.karacam.stock_service.models.OrderType;
import com.karacam.stock_service.repositories.IncomingStockOrderRepository;
import com.karacam.stock_service.repositories.OutboxStockOrderRepository;
import com.karacam.stock_service.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

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

    @KafkaListener(topics = "incoming_orders", groupId = "incoming_order_consumer")
    @Transactional
    public void listenToIncomingOrders(IncomingOrder.OrderMessage order) {
        String orderId = generateHashForIncomingOrder(order);
        Instant timeStamp = TimeUtil.protoTimestampToInstant(order.getTimestamp());

        if (this.redisService.checkAndSet(orderSetKey, orderId)) {
            IncomingStockOrder incomingStockOrder = IncomingStockOrder.builder()
                    .orderId(orderId)
                    .userId(order.getUserId())
                    .orderSymbol(order.getSymbol())
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
    
    @Scheduled(cron = "0 0 3 * * *")
    protected void updateOrderSetKey() {
        orderSetKey = orderSetKeyPrefix.concat(TimeUtil.getToday());
    }
}
