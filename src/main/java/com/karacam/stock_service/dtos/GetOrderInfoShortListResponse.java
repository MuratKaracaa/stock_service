package com.karacam.stock_service.dtos;

import com.karacam.stock_service.models.OrderInfoShort;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetOrderInfoShortListResponse {
    private List<OrderInfoShort> orderInfoShortList;
}
