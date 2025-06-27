package com.karacam.stock_service.enums;

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
}
