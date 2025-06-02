package com.karacam.stock_service.repositories;

import com.karacam.stock_service.entities.OutboxStockOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxStockOrderRepository extends JpaRepository<OutboxStockOrder, Long> {
}
