package com.karacam.stock_service.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

public enum OrderType {
    LIMIT('1'),
    MARKET('2');

    private final char value;

    OrderType(char c) {
        this.value = c;
    }

    public char getValue() {
        return this.value;
    }

    public static OrderType fromValue(char value) {
        return Arrays.stream(OrderType.values())
                .filter(type -> type.getValue() == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown OrderType value: " + value));
    }

    @Converter(autoApply = true)
    public static class OrderTypeConverter implements AttributeConverter<OrderType, Character> {
        @Override
        public Character convertToDatabaseColumn(OrderType orderType) {
            return orderType != null ? orderType.getValue() : null;
        }

        @Override
        public OrderType convertToEntityAttribute(Character character) {
            return character != null ? OrderType.fromValue(character) : null;
        }
    }
}
