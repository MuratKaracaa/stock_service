package com.karacam.stock_service.entities.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderExecution {
    private String timeStamp;
    private Double executionPrice;
    private int volume;
}
