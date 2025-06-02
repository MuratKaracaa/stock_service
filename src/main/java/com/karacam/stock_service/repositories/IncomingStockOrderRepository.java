package com.karacam.stock_service.repositories;

import com.karacam.stock_service.entities.IncomingStockOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomingStockOrderRepository extends JpaRepository<IncomingStockOrder, String> {
}
