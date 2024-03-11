package com.example.demo.grpc;

public record StockPriceChangedEvent(String symbol, double price) {
}
