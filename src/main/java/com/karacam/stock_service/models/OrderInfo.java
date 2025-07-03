package com.karacam.stock_service.models;

import com.karacam.stock_service.entities.types.OrderExecution;
import com.karacam.stock_service.enums.OrderSide;
import com.karacam.stock_service.enums.OrderStatus;
import com.karacam.stock_service.enums.OrderType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderInfo {
    private String orderId;
    private String symbol;
    private OrderSide side;
    private OrderType type;
    private int quantity;
    private double price;
    private double total;
    private OrderStatus status;
    private int fulfilledQuantity;
    private List<OrderExecution> executionList;
}
