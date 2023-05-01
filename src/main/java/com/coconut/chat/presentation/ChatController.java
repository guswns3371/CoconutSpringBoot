package com.coconut.chat.presentation;

import com.coconut.chat.application.ChatService;
import com.coconut.chat.presentation.dto.ChatHistoryResDto;
import com.coconut.chat.presentation.dto.ChatRoomDataReqDto;
import com.coconut.chat.presentation.dto.ChatRoomDataResDto;
import com.coconut.chat.presentation.dto.ChatRoomExitReqDto;
import com.coconut.chat.presentation.dto.ChatRoomListReqDto;
import com.coconut.chat.presentation.dto.ChatRoomNameChangeReqDto;
import com.coconut.chat.presentation.dto.ChatRoomSaveReqDto;
import com.coconut.chat.presentation.dto.ChatUploadImageReqDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chats")
public class ChatController {

  private final ChatService chatService;

  @PostMapping()
  public ResponseEntity<ChatRoomDataResDto> makeChatRoom(@RequestBody ChatRoomSaveReqDto reqDto) {
    return ResponseEntity.ok(chatService.makeChatRoom(reqDto));
  }

  @GetMapping("/users/{userId}")
  public ResponseEntity<List<ChatRoomListReqDto>> getChatRoomLists(@PathVariable Long userId) {
    return ResponseEntity.ok(chatService.getChatRoomLists(userId));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ChatRoomDataResDto> getChatRoomData(@PathVariable Long id, @RequestParam("userId") Long userId) {
    return ResponseEntity.ok(chatService.getChatRoomData(id, userId));
  }

  @GetMapping("/history/{id}")
  public ResponseEntity<List<ChatHistoryResDto>> getChatHistory(@PathVariable Long id) {
    return ResponseEntity.ok(chatService.getChatHistory(id));
  }

  @PostMapping(
      value = "/image",
      consumes = {
          MediaType.MULTIPART_FORM_DATA_VALUE,
          MediaType.APPLICATION_JSON_VALUE
      }
  )
  public ResponseEntity<List<String>> uploadChatImages(
      @RequestPart(value = "userId", required = false) String userId,
      @RequestPart(value = "chatRoomId", required = false) String chatRoomId,
      @RequestPart(required = false) MultipartFile[] images
  ) {
    ChatUploadImageReqDto reqDto = ChatUploadImageReqDto.builder()
                                                        .userId(userId)
                                                        .chatRoomId(chatRoomId)
                                                        .images(images)
                                                        .build();
    return ResponseEntity.ok(chatService.uploadChatImages(reqDto));
  }

  @PostMapping("/name")
  public ResponseEntity<Boolean> changeChatRoomName(@RequestBody ChatRoomNameChangeReqDto reqDto) {
    return ResponseEntity.ok(chatService.changeChatRoomName(reqDto));
  }

  @PostMapping("/invite")
  public ResponseEntity<ChatRoomDataResDto> inviteUser(@RequestBody ChatRoomDataReqDto chatRoomDataReqDto) {
    return ResponseEntity.ok(chatService.inviteUser(chatRoomDataReqDto));
  }

  @PostMapping("/exit")
  public ResponseEntity<Boolean> exitChatRoom(@RequestBody ChatRoomExitReqDto reqDto) {
    return ResponseEntity.ok(chatService.exitChatRoom(reqDto));
  }
}
