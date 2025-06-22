package com.karacam.stock_service.models;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
public class StockModel {
    private String symbol;
    private String stockName;
    private BigDecimal latestTradingPrice;
}
