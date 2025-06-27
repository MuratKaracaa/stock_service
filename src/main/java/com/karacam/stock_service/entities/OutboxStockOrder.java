package com.karacam.stock_service.entities;

import com.karacam.stock_service.enums.OrderSide;
import com.karacam.stock_service.enums.OrderType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "incoming_orders_outbox")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OutboxStockOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "side")
    private OrderSide side;

    @Column(name = "type")
    private OrderType type;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "price")
    private double price;

    @Column(name = "timestamp")
    private Instant timestamp;
}
