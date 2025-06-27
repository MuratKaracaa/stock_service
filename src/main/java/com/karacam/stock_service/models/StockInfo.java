package com.karacam.stock_service.models;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class StockInfo {
    private String symbol;
    private String stockName;
    private BigDecimal latestTradingPrice;
}
