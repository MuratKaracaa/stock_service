package com.karacam.stock_service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimeSeriesPoint {
    private String timestamp;
    private double latestTradingPrice;
}