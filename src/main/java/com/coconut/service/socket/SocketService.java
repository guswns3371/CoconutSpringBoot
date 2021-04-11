package com.coconut.service.socket;

import com.coconut.client.model.EchoModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocketService {

    private final SimpMessagingTemplate simpTemplate;

    // simpTemplate.convertAndSend 으로 "/topic/greetings" 방의 모든 사용자에게 메시지를 보낼 수 있다.
    public void echoMessage(String message) {
        log.warn("Start convertAndSend");
        simpTemplate.convertAndSend("/topic/greetings",
                EchoModel.builder()
                        .echo(message)
                        .build());
        log.warn("End convertAndSend");
    }
}
