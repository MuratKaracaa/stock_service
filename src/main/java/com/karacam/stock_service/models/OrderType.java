package com.karacam.stock_service.models;

public enum OrderType {
    LIMIT('1'),
    MARKET('2');

    private final char value;

    OrderType(char c) {
        this.value = c;
    }

    public char getValue(){
        return this.value;
    }
}
