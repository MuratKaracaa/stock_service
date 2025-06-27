package com.karacam.stock_service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OHLCInfo {
    TimeSeriesPoint open;
    TimeSeriesPoint high;
    TimeSeriesPoint low;
    TimeSeriesPoint close;
}
