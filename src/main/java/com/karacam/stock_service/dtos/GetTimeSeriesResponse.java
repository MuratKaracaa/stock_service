package com.karacam.stock_service.dtos;

import com.karacam.stock_service.models.OHLCInfo;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetTimeSeriesResponse {
    private List<OHLCInfo> ohlcData;
}
