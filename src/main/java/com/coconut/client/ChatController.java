package com.coconut.client;

import com.coconut.client.dto.req.*;
import com.coconut.client.dto.res.BaseResDto;
import com.coconut.client.dto.res.ChatHistoryResDto;
import com.coconut.client.dto.res.ChatRoomDataResDto;
import com.coconut.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(
            value = "/api/chat/upload/image" ,
            consumes = {
                    MediaType.MULTIPART_FORM_DATA_VALUE,
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public ArrayList<String> uploadChatImages(
            @RequestPart(value = "userId", required = false) String userId,
            @RequestPart(value = "chatRoomId", required = false) String chatRoomId,
            @RequestPart(required = false) MultipartFile[] images
    ) {
        return chatService.uploadChatImages(
                ChatUploadImageReqDto.builder()
                        .userId(userId)
                        .chatRoomId(chatRoomId)
                        .images(images)
                        .build());
    }
}
