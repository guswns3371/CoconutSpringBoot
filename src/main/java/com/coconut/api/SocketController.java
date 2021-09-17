package com.coconut.api;

import com.coconut.api.dto.ChatMessageSocketDto;
import com.coconut.api.dto.ChatRoomSocketDto;
import com.coconut.service.socket.SocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SocketController {

    private final SocketService socketService;

    @MessageMapping("/users/online")
    @SendTo("/sub/users/service")
    public List<String> onlineUsers(String userIndex) { return socketService.onlineUsers(userIndex); }

    @MessageMapping("/users/offline")
    @SendTo("/sub/users/service")
    public List<String> offlineUsers(String userIndex) { return socketService.offlineUsers(userIndex); }

    @MessageMapping("/chat/enter")
    public void enterUsers(ChatRoomSocketDto dto) {
        socketService.enterUsers(dto);
    }

    @MessageMapping("/chat/exit")
    public void exitUsers(ChatRoomSocketDto dto) {
        socketService.exitUsers(dto);
    }

    @MessageMapping("/chat/message")
    public void sendMessage(ChatMessageSocketDto dto) { socketService.sendMessage(dto); }
}
