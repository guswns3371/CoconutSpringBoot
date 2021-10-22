package com.coconut.api;

import com.coconut.api.dto.req.*;
import com.coconut.api.dto.res.ChatHistoryResDto;
import com.coconut.api.dto.res.ChatRoomDataResDto;
import com.coconut.service.ChatHistoryService;
import com.coconut.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;
    private final ChatHistoryService chatHistoryService;

    @PostMapping()
    public ChatRoomDataResDto makeChatRoom(@RequestBody ChatRoomSaveReqDto reqDto) {
        return chatService.makeChatRoom(reqDto);
    }

    @GetMapping("/users/{userId}")
    public ArrayList<ChatRoomListReqDto> getChatRoomLists(@PathVariable Long userId) {
        return chatService.getChatRoomLists(userId);
    }

    @GetMapping("/{id}")
    public ChatRoomDataResDto getChatRoomData(@PathVariable Long id, @RequestParam("userId") Long userId) {
        return chatService.getChatRoomData(id, userId);
    }

    @GetMapping("/history/{id}")
    public ArrayList<ChatHistoryResDto> getChatHistory(@PathVariable Long id) {
        return chatService.getChatHistory(id);
    }

    @PostMapping(
            value = "/image",
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
        ChatUploadImageReqDto reqDto = ChatUploadImageReqDto.builder()
                .userId(userId)
                .chatRoomId(chatRoomId)
                .images(images)
                .build();
        return chatService.uploadChatImages(reqDto);
    }

    @PostMapping("/name")
    public boolean changeChatRoomName(@RequestBody ChatRoomNameChangeReqDto reqDto) {
        return chatService.changeChatRoomName(reqDto);
    }

    @PostMapping("/invite")
    public ChatRoomDataResDto inviteUser(@RequestBody ChatRoomDataReqDto chatRoomDataReqDto) {
        return chatService.inviteUser(chatRoomDataReqDto);
    }

    @PostMapping("/exit")
    public boolean exitChatRoom(@RequestBody ChatRoomExitReqDto reqDto) {
        return chatService.exitChatRoom(reqDto);
    }
}
