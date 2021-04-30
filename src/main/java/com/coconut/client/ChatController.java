package com.coconut.client;

import com.coconut.client.dto.req.ChatRoomDataReqDto;
import com.coconut.client.dto.req.ChatRoomListReqDto;
import com.coconut.client.dto.req.ChatRoomSaveReqDto;
import com.coconut.client.dto.res.ChatHistoryResDto;
import com.coconut.client.dto.res.ChatRoomDataResDto;
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
    public ChatRoomDataResDto makeChatRoom(@RequestBody ChatRoomSaveReqDto chatRoomSaveReqDto) {
        return chatService.makeChatRoom(chatRoomSaveReqDto);
    }

    @PostMapping("/api/chat/room/info")
    public ChatRoomDataResDto getChatRoomData(@RequestBody ChatRoomDataReqDto chatRoomDataReqDto) {
        return chatService.getChatRoomData(chatRoomDataReqDto);
    }

    @GetMapping("/api/chat/{chatRoomId}")
    public ArrayList<ChatHistoryResDto> getChatHistory(@PathVariable String chatRoomId) {
        return chatService.getChatHistory(chatRoomId);
    }

    @GetMapping("/api/chat/room/list/{userId}")
    public ArrayList<ChatRoomListReqDto> getChatRoomLists(@PathVariable String userId) {
        return chatService.getChatRoomLists(userId);
    }
}
