package com.karacam.stock_service.dtos;

import com.karacam.stock_service.models.StockInfo;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetMultipleStocksResponse {
    private List<StockInfo> stocks;
}
