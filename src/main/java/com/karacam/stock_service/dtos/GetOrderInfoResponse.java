package com.karacam.stock_service.dtos;

import com.karacam.stock_service.models.OrderInfo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetOrderInfoResponse {
    private OrderInfo orderInfo;
}
