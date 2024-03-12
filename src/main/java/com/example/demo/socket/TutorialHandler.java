package com.example.demo.socket;

import com.example.demo.ws.proto.HelloRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class TutorialHandler implements WebSocketHandler {

    private final List<WebSocketSession> sessions = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Connection established on session: {}", session.getId());
        sessions.add(session);
        session.getAttributes().put("REQUEST_TIME", System.currentTimeMillis());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String data = (String) message.getPayload();
        log.info("Message: {}", data);
        session.getAttributes().put("REQUEST_TIME", System.currentTimeMillis());
//        session.sendMessage(new TextMessage("Started processing tutorial: " + session + " - " + data));
//        Thread.sleep(1000);
//        session.sendMessage(new TextMessage("Completed processing tutorial: " + data));

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.info("Exception occured: {} on session: {}", exception.getMessage(), session.getId());

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        log.info("Connection closed on session: {} with status: {}", session.getId(), closeStatus.getCode());
        sessions.remove(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }


    @Scheduled(fixedRate = 1500)
    public void sendMessage() throws IOException {
//        log.info("Scheduled sendMessage {}", sessions.size());
        for (WebSocketSession session : sessions) {
            var random = ThreadLocalRandom.current();
            HelloRequest helloRequest = HelloRequest.newBuilder()
                    .setTime(random.nextInt())
                    .setMessage("Hello from " + session.getId())
                    .build();
            String asBase64 = Base64.getEncoder().encodeToString(helloRequest.toByteArray());
            TextMessage msg = new TextMessage("message|" + asBase64);
//            sendMessage(session, msg);
            BinaryMessage binaryMessage = new BinaryMessage(helloRequest.toByteArray());
            session.sendMessage(binaryMessage);
        }
        closeSession();
    }

    private void sendMessage(WebSocketSession session, TextMessage msg) throws IOException {
        String requestTimeStr = String.valueOf(session.getAttributes().get("REQUEST_TIME"));
        long requestTime = StringUtils.isBlank(requestTimeStr) ? 0L : Long.parseLong(requestTimeStr);
        log.info("Session {} request time {}", session.getId(), requestTime);
        if (System.currentTimeMillis() - requestTime > 10000) {
            session.getAttributes().put("EXPIRED", 1);
        } else {
            session.sendMessage(msg);
        }
    }

    private void closeSession() {
        List<WebSocketSession> expiredSessions = sessions.stream()
                .filter(s -> s.getAttributes().get("EXPIRED") != null)
                .toList();
        sessions.removeAll(expiredSessions);
        expiredSessions.forEach(s -> {
            try {
                s.close();
            } catch (IOException e) {
                log.error("Close session {} error", s.getId(), e);
            }
        });
    }
}
