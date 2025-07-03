package com.karacam.stock_service.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

public enum OrderSide {
    BUY('1'),
    SELL('2');

    private final char value;

    OrderSide(char c) {
        this.value = c;
    }

    public char getValue() {
        return this.value;
    }

    public static OrderSide fromValue(char value) {
        return Arrays.stream(OrderSide.values())
                .filter(side -> side.getValue() == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown OrderSide value: " + value));
    }

    @Converter(autoApply = true)
    public static class OrderSideConverter implements AttributeConverter<OrderSide, Character> {
        @Override
        public Character convertToDatabaseColumn(OrderSide orderSide) {
            return orderSide != null ? orderSide.getValue() : null;
        }

        @Override
        public OrderSide convertToEntityAttribute(Character character) {
            return character != null ? OrderSide.fromValue(character) : null;
        }
    }
}
