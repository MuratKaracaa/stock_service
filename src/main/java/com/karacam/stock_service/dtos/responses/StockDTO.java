package com.karacam.stock_service.dtos.responses;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class StockDTO {
    private String symbol;
    private String stockName;
    private double latestTradingPrice;
}
