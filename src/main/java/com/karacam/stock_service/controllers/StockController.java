package com.karacam.stock_service.controllers;

import com.karacam.stock_service.core.ValidationMessages;
import com.karacam.stock_service.core.ValidationPatterns;
import com.karacam.stock_service.dtos.responses.GetMultipleStocksResponse;
import com.karacam.stock_service.dtos.responses.GetOneStockResponse;
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
        return this.stockService.getOneStock(symbol);
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
        return this.stockService.getMultipleStocks(symbols);
    }

}
