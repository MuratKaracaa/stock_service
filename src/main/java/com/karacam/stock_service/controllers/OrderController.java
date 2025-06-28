package com.karacam.stock_service.controllers;

import com.karacam.stock_service.dtos.GetOrderInfoResponse;
import com.karacam.stock_service.dtos.GetOrderInfoShortListResponse;
import com.karacam.stock_service.models.OrderInfo;
import com.karacam.stock_service.models.OrderInfoShort;
import com.karacam.stock_service.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
@Validated
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService_) {
        this.orderService = orderService_;
    }

    @GetMapping("/get-order-info-short-list")
    public GetOrderInfoShortListResponse getOrderInfoShortList(
            @RequestParam
            int userId
    ) {
        List<OrderInfoShort> orderInfoShortList = this.orderService.getOrderInfoShortList(userId);
        return GetOrderInfoShortListResponse.builder()
                .orderInfoShortList(orderInfoShortList)
                .build();
    }

    @GetMapping("/get-order-info")
    public GetOrderInfoResponse getOrderInfo(
            @RequestParam
            int userId,
            @RequestParam
            String orderId
    ) {
        OrderInfo orderInfo = this.orderService.getOrderInfo(userId, orderId);
        return GetOrderInfoResponse.builder()
                .orderInfo(orderInfo)
                .build();
    }
}
