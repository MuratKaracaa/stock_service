package com.karacam.stock_service.entities;

import com.karacam.stock_service.entities.types.OrderExecution;
import com.karacam.stock_service.enums.OrderSide;
import com.karacam.stock_service.enums.OrderStatus;
import com.karacam.stock_service.enums.OrderType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "incoming_orders", indexes = {
        @Index(name = "idx_order_id", columnList = "order_id"),
        @Index(name = "idx_user_id", columnList = "user_id"),
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncomingStockOrder {
    @Id
    @Column(name = "order_id")
    private String orderId;

    @Column(name = "user_id")
    private int userId;

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

    @Column(name = "status")
    private OrderStatus status;

    @Column(name = "fulfilled_quantity")
    private int fulfilledQuantity;

    @Column(name = "timestamp")
    private Instant timestamp;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "execution_list", columnDefinition = "jsonb")
    private List<OrderExecution> executionList;

    @PostLoad
    private void ensureExecutionListIsNotNull() {
        if (this.executionList == null) {
            this.executionList = new ArrayList<>();
        }
    }
}
