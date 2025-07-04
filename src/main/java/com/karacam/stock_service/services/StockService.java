package com.karacam.stock_service.services;

import com.karacam.stock_service.entities.Stock;
import com.karacam.stock_service.enums.TimeSeriesPeriods;
import com.karacam.stock_service.models.OHLCInfo;
import com.karacam.stock_service.models.StockInfo;
import com.karacam.stock_service.models.TimeSeriesPoint;
import com.karacam.stock_service.repositories.StockRepository;
import com.karacam.stock_service.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StockService {
    private final StockRepository stockRepository;
    private final RedisService redisService;

    @Autowired
    public StockService(StockRepository stockRepository_, RedisService redisService_) {
        this.stockRepository = stockRepository_;
        this.redisService = redisService_;
    }

    public StockInfo getOneStock(String symbol) {
        Optional<Stock> optionalStock = this.stockRepository.findById(symbol);

        if (optionalStock.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Stock stock = optionalStock.get();

        return StockInfo.builder()
                .symbol(stock.getSymbol())
                .stockName(stock.getStockName())
                .latestTradingPrice(stock.getLatestTradingPrice())
                .build();
    }

    public List<StockInfo> getMultipleStocks(List<String> symbolList) {
        List<Stock> stockList = this.stockRepository.findAllById(symbolList);

        return stockList.stream().map((iteratedStock) -> StockInfo.builder()
                .symbol(iteratedStock.getSymbol())
                .stockName(iteratedStock.getStockName())
                .latestTradingPrice(iteratedStock.getLatestTradingPrice())
                .build()).collect(Collectors.toList());
    }

    public List<OHLCInfo> getOHLCInfo(String symbol, TimeSeriesPeriods period) {
        List<List<List<Number>>> queryResult = this.redisService.getTimeSeriesDataForStock(symbol, period);
        int dataLength = queryResult.getFirst().size();
        List<OHLCInfo> ohlcData = new ArrayList<>();
        for (int i = 0; i < dataLength; i++) {
            OHLCInfo ohlc = new OHLCInfo();
            for (int j = 0; j < 4; j++) {
                List<Number> pair = queryResult.get(j).get(i);
                if (pair != null) {
                    Double latestTradingPrice = pair.getLast().doubleValue();
                    String timeStampFormat = period == TimeSeriesPeriods.DAILY ? "dd/MM/yyyy HH:mm:ss" : "dd/MM/yyyy";
                    String formattedTimeStamp = TimeUtil.formatUnixTimestamp((Long) pair.getFirst(), timeStampFormat);
                    TimeSeriesPoint timeSeriesPoint = TimeSeriesPoint.builder()
                            .timestamp(formattedTimeStamp)
                            .latestTradingPrice(latestTradingPrice)
                            .build();
                    switch (j) {
                        case 0 -> ohlc.setOpen(timeSeriesPoint);
                        case 1 -> ohlc.setHigh(timeSeriesPoint);
                        case 2 -> ohlc.setLow(timeSeriesPoint);
                        case 3 -> ohlc.setClose(timeSeriesPoint);
                    }
                }
            }
            ohlcData.add(ohlc);
        }
        return ohlcData;
    }
}
