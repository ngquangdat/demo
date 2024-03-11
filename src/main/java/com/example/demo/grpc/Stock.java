package com.example.demo.grpc;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Stock {
    private final String symbol;
    private double price;
    public void updatePrice(double price) {
        this.price = price;
        DomainEvents.publish(new StockPriceChangedEvent(this.symbol, this.price));
    }
}
