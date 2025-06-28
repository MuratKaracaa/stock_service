package com.karacam.stock_service.repositories;

import com.karacam.stock_service.entities.IncomingStockOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IncomingStockOrderRepository extends JpaRepository<IncomingStockOrder, String> {
    List<IncomingStockOrder> findByUserId(int userId);

    Optional<IncomingStockOrder> findByOrderId(String orderId);
}
