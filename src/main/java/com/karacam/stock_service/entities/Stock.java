package com.karacam.stock_service.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "stocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock {
    @Id
    @Column(name = "symbol")
    private String symbol;

    @Column(name = "stock_name", nullable = false)
    private String stockName;

    @Column(name = "latest_trading_price", nullable = false, precision = 10, scale = 2)
    private double latestTradingPrice;

    @Column(name = "latest_trading_price_time_stamp", nullable = false)
    private Instant latestTradingPriceTimeStamp;
}