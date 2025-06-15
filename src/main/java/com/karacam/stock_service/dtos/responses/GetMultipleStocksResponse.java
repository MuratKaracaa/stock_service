package com.karacam.stock_service.dtos.responses;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetMultipleStocksResponse {
    private List<StockDTO> stocks;
}
