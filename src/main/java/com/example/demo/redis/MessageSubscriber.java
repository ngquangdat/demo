package com.example.demo.redis;

import com.example.demo.socket.TutorialHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class MessageSubscriber implements MessageListener {

    @Autowired
    private TutorialHandler tutorialHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("MessageSubscriber received: {}", message);
        try {
            String msg = objectMapper.readValue(message.getBody(), String.class);
            tutorialHandler.boardCastMessage(msg);
        } catch (IOException e) {
            log.error("MessageSubscriber onMessage error", e);
        }
    }
}
