package com.example.demo.socket;

import com.example.demo.redis.MessagePublisher;
import com.example.demo.ws.proto.HelloRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.*;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class TutorialHandler implements WebSocketHandler {

    @Autowired
    private MessagePublisher messagePublisher;

    private final List<WebSocketSession> sessions = new ArrayList<>();

//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) {
//        log.info("Connection established on session: {}", session.getId());
//        sessions.add(session);
//        session.getAttributes().put("REQUEST_TIME", System.currentTimeMillis());
//    }
//
//    @Override
//    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
//        String data = (String) message.getPayload();
//        log.info("Message: {}", data);
//        session.getAttributes().put("REQUEST_TIME", System.currentTimeMillis());
//
//        messagePublisher.publish(data);
//    }
//
//    @Override
//    public void handleTransportError(WebSocketSession session, Throwable exception) {
//        log.info("Exception occured: {} on session: {}", exception.getMessage(), session.getId());
//
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
//        log.info("Connection closed on session: {} with status: {}", session.getId(), closeStatus.getCode());
//        sessions.remove(session);
//    }
//
//    @Override
//    public boolean supportsPartialMessages() {
//        return false;
//    }


//    @Scheduled(fixedRate = 1500)
//    public void sendMessage() throws IOException {
////        log.info("Scheduled sendMessage {}", sessions.size());
//        for (WebSocketSession session : sessions) {
//            var random = ThreadLocalRandom.current();
//            HelloRequest helloRequest = HelloRequest.newBuilder()
//                    .setTime(random.nextInt())
//                    .setMessage("Hello from " + session.getId())
//                    .build();
//            String asBase64 = Base64.getEncoder().encodeToString(helloRequest.toByteArray());
//            TextMessage msg = new TextMessage("message|" + asBase64);
////            sendMessage(session, msg);
//            BinaryMessage binaryMessage = new BinaryMessage(helloRequest.toByteArray());
//            session.sendMessage(binaryMessage);
//        }
//        closeSession();
//    }
//
//    public void boardCastMessage(String message) throws IOException {
//        for (WebSocketSession session : sessions) {
//            var random = ThreadLocalRandom.current();
//            HelloRequest helloRequest = HelloRequest.newBuilder()
//                    .setTime(random.nextInt())
//                    .setMessage("Hello from " + message)
//                    .build();
//            BinaryMessage binaryMessage = new BinaryMessage(helloRequest.toByteArray());
//            session.sendMessage(binaryMessage);
//        }
//    }
//
//    private void sendMessage(WebSocketSession session, TextMessage msg) throws IOException {
//        String requestTimeStr = String.valueOf(session.getAttributes().get("REQUEST_TIME"));
//        long requestTime = StringUtils.isBlank(requestTimeStr) ? 0L : Long.parseLong(requestTimeStr);
//        log.info("Session {} request time {}", session.getId(), requestTime);
//        if (System.currentTimeMillis() - requestTime > 10000) {
//            session.getAttributes().put("EXPIRED", 1);
//        } else {
//            session.sendMessage(msg);
//        }
//    }
//
//    private void closeSession() {
//        List<WebSocketSession> expiredSessions = sessions.stream()
//                .filter(s -> s.getAttributes().get("EXPIRED") != null)
//                .toList();
//        sessions.removeAll(expiredSessions);
//        expiredSessions.forEach(s -> {
//            try {
//                s.close();
//            } catch (IOException e) {
//                log.error("Close session {} error", s.getId(), e);
//            }
//        });
//    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.receive()
                .doOnNext(message -> {
                    log.info("doOnNext {}", message);
                })
                .doOnSubscribe(subscription -> {
                    log.info("doOnSubscribe {}", subscription);
                    sessions.add(session);
                })
                .doFinally(signalType -> {
                    log.info("doFinally {}", signalType);
                })
                .map(value -> session.textMessage("Echo " + value.getPayloadAsText()))
                .zipWith(session.send(chatHistory.asFlux().map(session::textMessage))).then();
    }

    private final Sinks.Many<String> chatHistory = Sinks.many().replay().limit(1);
    private final AtomicInteger counter = new AtomicInteger();
    @Scheduled(fixedRate = 1500)
    public void sendMessage() {
        chatHistory.tryEmitNext("Hello" + counter.getAndIncrement());
    }

}
