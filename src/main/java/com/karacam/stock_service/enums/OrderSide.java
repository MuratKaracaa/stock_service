package com.karacam.stock_service.enums;

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
}
