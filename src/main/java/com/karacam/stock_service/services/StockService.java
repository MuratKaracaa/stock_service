package com.karacam.stock_service.services;

import com.karacam.stock_service.dtos.responses.GetMultipleStocksResponse;
import com.karacam.stock_service.dtos.responses.GetOneStockResponse;
import com.karacam.stock_service.dtos.responses.StockDTO;
import com.karacam.stock_service.entities.Stock;
import com.karacam.stock_service.repositories.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StockService {
    private final StockRepository stockRepository;

    @Autowired
    public StockService(StockRepository stockRepository_) {
        this.stockRepository = stockRepository_;
    }

    public GetOneStockResponse getOneStock(String symbol) {
        Optional<Stock> optionalStock = this.stockRepository.findById(symbol);

        if (optionalStock.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Stock stock = optionalStock.get();

        return GetOneStockResponse.builder()
                .symbol(stock.getSymbol())
                .stockName(stock.getStockName())
                .latestTradingPrice(stock.getLatestTradingPrice())
                .build();
    }

    public GetMultipleStocksResponse getMultipleStocks(List<String> symbolList) {
        List<Stock> stockList = this.stockRepository.findAllById(symbolList);

        List<StockDTO> stockDTOS = stockList.stream().map((iteratedStock) -> StockDTO.builder()
                .symbol(iteratedStock.getSymbol())
                .stockName(iteratedStock.getStockName())
                .latestTradingPrice(iteratedStock.getLatestTradingPrice())
                .build()).collect(Collectors.toList());

        return GetMultipleStocksResponse.builder()
                .stocks(stockDTOS)
                .build();
    }
}
