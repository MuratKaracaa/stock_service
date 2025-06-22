package com.karacam.stock_service.dtos.responses;

import com.karacam.stock_service.models.OHLC;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetTimeSeriesResponse {
    List<OHLC> ohlcData;
}
