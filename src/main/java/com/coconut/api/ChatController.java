package com.coconut.api;

import com.coconut.api.dto.req.*;
import com.coconut.api.dto.res.ChatHistoryResDto;
import com.coconut.api.dto.res.ChatRoomDataResDto;
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
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/room/make")
    public ChatRoomDataResDto makeChatRoom(@RequestBody ChatRoomSaveReqDto chatRoomSaveReqDto) {
        return chatService.makeChatRoom(chatRoomSaveReqDto);
    }

    @PostMapping("/room/info")
    public ChatRoomDataResDto getChatRoomData(@RequestBody ChatRoomDataReqDto chatRoomDataReqDto) {
        return chatService.getChatRoomData(chatRoomDataReqDto);
    }

    @PostMapping("/room/invite")
    public ChatRoomDataResDto inviteUser(@RequestBody ChatRoomDataReqDto chatRoomDataReqDto) {
        return chatService.inviteUser(chatRoomDataReqDto);
    }

    @GetMapping("/{chatRoomId}")
    public ArrayList<ChatHistoryResDto> getChatHistory(@PathVariable String chatRoomId) {
        return chatService.getChatHistory(chatRoomId);
    }

    @GetMapping("/room/list/{userId}")
    public ArrayList<ChatRoomListReqDto> getChatRoomLists(@PathVariable String userId) {
        return chatService.getChatRoomLists(userId);
    }

    @PostMapping("/room/name")
    public boolean changeChatRoomName(@RequestBody ChatRoomNameChangeReqDto reqDto) {
        return chatService.changeChatRoomName(reqDto);
    }

    @PostMapping("/room/exit")
    public boolean exitChatRoom(@RequestBody ChatRoomExitReqDto reqDto) {
        return chatService.exitChatRoom(reqDto);
    }

    @PostMapping(
            value = "/upload/image" ,
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
