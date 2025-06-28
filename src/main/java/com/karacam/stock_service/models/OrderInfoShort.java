package com.karacam.stock_service.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderInfoShort {
    private String orderId;
    private String symbol;
    private Double total;
}
