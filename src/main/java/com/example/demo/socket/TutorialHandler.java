package com.example.demo.socket;

import com.example.demo.ws.proto.HelloRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TutorialHandler implements WebSocketHandler {

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		log.info("Connection established on session: {}", session.getId());
		addSession(session);
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		String tutorial = (String) message.getPayload();
		log.info("Message: {}", tutorial);
        session.sendMessage(new TextMessage("Started processing tutorial: " + session + " - " + tutorial));
        Thread.sleep(1000);
        session.sendMessage(new TextMessage("Completed processing tutorial: " + tutorial));
		
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		log.info("Exception occured: {} on session: {}", exception.getMessage(), session.getId());
		
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		log.info("Connection closed on session: {} with status: {}", session.getId(), closeStatus.getCode());
		
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}


	private List<WebSocketSession> sessions = new ArrayList<>();

	private void addSession(WebSocketSession sess) {
		this.sessions.add(sess);
	}

	@Scheduled(fixedRate = 3000)
	public void sendMessage() throws IOException {
		log.info("Scheduled sendMessage {}", this.sessions.size());
		for (WebSocketSession sess : sessions) {
//			TextMessage msg = new TextMessage("Hello from " + sess.getId());
//			sess.sendMessage(msg);

			HelloRequest helloRequest = HelloRequest.newBuilder()
					.setTime(123456789)
					.setMessage("Hello from " + sess.getId())
					.build();
			BinaryMessage binaryMessage = new BinaryMessage(helloRequest.toByteArray());
			sess.sendMessage(binaryMessage);
		}
	}
}
