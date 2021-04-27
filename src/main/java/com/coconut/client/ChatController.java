package com.coconut.client;

import com.coconut.client.dto.req.ChatRoomSaveReqDto;
import com.coconut.client.dto.res.ChatHistoryResDto;
import com.coconut.client.dto.res.ChatRoomSaveResDto;
import com.coconut.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/api/chat/room/make")
    public ChatRoomSaveResDto makeChatRoom(@RequestBody ChatRoomSaveReqDto chatRoomSaveReqDto) {
        return chatService.makeChatRoom(chatRoomSaveReqDto);
    }

    @GetMapping("/api/chat/{id}")
    public ArrayList<ChatHistoryResDto> getChatHistory(@PathVariable String id) {
        return chatService.getChatHistory(id);
    }
}
