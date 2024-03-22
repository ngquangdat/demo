package com.example.demo.socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;

import java.util.Map;

@Configuration
public class WebSocketConfig {

    @Autowired
    private TutorialHandler tutorialHandler;

    @Bean
    public HandlerMapping handlerMapping(){
        Map<String, TutorialHandler> handlerMap = Map.of(
                "/tutorial", tutorialHandler
        );
        return new SimpleUrlHandlerMapping(handlerMap, 1);
    }
}
