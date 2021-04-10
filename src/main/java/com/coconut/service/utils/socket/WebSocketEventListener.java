package com.coconut.service.utils.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

@Component
public class WebSocketEventListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.warn("Received a new web socket connection : 소켓 연결 성공!");;
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("username");
        logger.warn("Received a new web socket disconnection : 소켓 연결 끊음!");;
    }
}
