package com.example.demo.dummy;

import java.util.*;

public enum StockRepository {
    INSTANCE;

    private Map<String, Stock> stocks = new HashMap<>();
    StockRepository() {
        addStock(new Stock("ABC", 50));
        addStock(new Stock("DEF", 50));
    }

    public void addStock(Stock stock) {
        stocks.put(stock.getSymbol(), stock);
    }
    

    public Optional<Stock> getStock(String symbol) {
        return Optional.ofNullable(stocks.get(symbol));
    }

    public Collection<Stock> getStocks() {
        return Collections.unmodifiableCollection(stocks.values());
    }
}
