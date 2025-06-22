package com.karacam.stock_service.dtos.responses;

import com.karacam.stock_service.models.StockModel;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class GetOneStockResponse extends StockModel {

}
