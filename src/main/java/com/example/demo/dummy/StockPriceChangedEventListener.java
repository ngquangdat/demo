package com.example.demo.dummy;

import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StockPriceChangedEventListener {
    @Subscribe
    public void handleEvent(StockPriceChangedEvent event) {
        log.info(event.toString());
    }
}
