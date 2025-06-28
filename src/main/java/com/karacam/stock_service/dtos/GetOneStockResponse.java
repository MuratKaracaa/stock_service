package com.karacam.stock_service.dtos;

import com.karacam.stock_service.models.StockInfo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetOneStockResponse {
    private StockInfo stockInfo;
}
