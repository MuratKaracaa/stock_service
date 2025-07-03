package com.karacam.stock_service.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

public enum OrderStatus {
    NEW('1'),
    PARTIALLY_FULFILLED('2'),
    FULFILLED('3');

    private final char value;

    OrderStatus(char c) {
        value = c;
    }

    public char getValue() {
        return value;
    }

    public static OrderStatus fromValue(char value) {
        return Arrays.stream(OrderStatus.values())
                .filter(status -> status.getValue() == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown OrderStatus value: " + value));
    }

    @Converter(autoApply = true)
    public static class OrderStatusConverter implements AttributeConverter<OrderStatus, Character> {
        @Override
        public Character convertToDatabaseColumn(OrderStatus orderStatus) {
            return orderStatus != null ? orderStatus.getValue() : null;
        }

        @Override
        public OrderStatus convertToEntityAttribute(Character character) {
            return character != null ? OrderStatus.fromValue(character) : null;
        }
    }
}
