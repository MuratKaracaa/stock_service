package com.karacam.stock_service.core;

public class ValidationMessages {
    private ValidationMessages() {
    }

    public static final String PARAMETER_NOT_EMPTY = "Provided parameter cannot be empty";
    public static final String PARAMETER_LIST_NOT_EMPTY = "Provided parameter must contain at least one element";

    public static final String SYMBOL_REQUIRED = "symbol parameter must be provided";
    public static final String SYMBOLS_REQUIRED = "symbols parameter must be provided";
    public static final String SYMBOLS_MAX_TWENTY = "Up to 20 symbols are allowed";
    public static final String SYMBOL_INVALID_FORMAT = "Invalid format. Symbols must be alphabetic and maximum 5 letters long.";
}
