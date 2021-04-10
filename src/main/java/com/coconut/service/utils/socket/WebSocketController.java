package com.coconut.service.utils.socket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class WebSocketController {

    @MessageMapping("/hello-msg-mapping")
    @SendTo("/topic/greetings")
    public String echoMessageMapping(String message) {
        log.warn("React to hello-msg-mapping : "+message);
        return "소켓 통신 개통 축하한다 새꺄";
    }
}
