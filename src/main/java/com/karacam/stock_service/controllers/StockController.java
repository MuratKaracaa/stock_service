package com.karacam.stock_service.controllers;

import com.karacam.stock_service.constants.ValidationMessages;
import com.karacam.stock_service.constants.ValidationPatterns;
import com.karacam.stock_service.dtos.GetMultipleStocksResponse;
import com.karacam.stock_service.dtos.GetOneStockResponse;
import com.karacam.stock_service.dtos.GetTimeSeriesResponse;
import com.karacam.stock_service.enums.TimeSeriesPeriods;
import com.karacam.stock_service.models.OHLCInfo;
import com.karacam.stock_service.models.StockInfo;
import com.karacam.stock_service.services.StockService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stock")
@Validated
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService_) {
        this.stockService = stockService_;
    }

    @GetMapping("/get-one-stock")
    public GetOneStockResponse getOneStock(
            @RequestParam
            @NotNull(message = ValidationMessages.SYMBOL_REQUIRED)
            @NotBlank(message = ValidationMessages.PARAMETER_NOT_EMPTY)
            String symbol
    ) {
        StockInfo stockInfo = this.stockService.getOneStock(symbol);
        return GetOneStockResponse.builder()
                .stockInfo(stockInfo)
                .build();
    }

    @GetMapping("/get-multiple-stocks")
    public GetMultipleStocksResponse getMultipleStocks(
            @RequestParam
            @NotNull(message = ValidationMessages.SYMBOLS_REQUIRED)
            @NotEmpty(message = ValidationMessages.PARAMETER_LIST_NOT_EMPTY)
            @Size(min = 1, max = 20, message = ValidationMessages.SYMBOLS_MAX_TWENTY)
            @Valid
            List<@NotBlank @Pattern(regexp = ValidationPatterns.SYMBOL_REGEX, message = ValidationMessages.SYMBOL_INVALID_FORMAT) String> symbols
    ) {
        List<StockInfo> stockInfoList = this.stockService.getMultipleStocks(symbols);
        return GetMultipleStocksResponse.builder()
                .stocks(stockInfoList)
                .build();
    }

    @GetMapping("/get-time-series")
    public GetTimeSeriesResponse getTimeSeries(
            @RequestParam
            @NotNull(message = ValidationMessages.SYMBOL_REQUIRED)
            @NotBlank(message = ValidationMessages.PARAMETER_NOT_EMPTY)
            String symbol,
            @RequestParam
            @NotNull(message = ValidationMessages.SYMBOL_REQUIRED)
            TimeSeriesPeriods period
    ) {
        List<OHLCInfo> ohlcInfoList = this.stockService.getOHLCInfo(symbol, period);
        return GetTimeSeriesResponse.builder()
                .ohlcData(ohlcInfoList)
                .build();
    }
}
