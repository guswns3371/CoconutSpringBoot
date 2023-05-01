package com.coconut.chat.presentation.dto;

import java.util.Arrays;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@Getter
public class ChatUploadImageReqDto {

  private String userId;
  private String chatRoomId;
  private MultipartFile[] images;

  @Builder
  public ChatUploadImageReqDto(String userId, String chatRoomId, MultipartFile[] images) {
    this.userId = userId;
    this.chatRoomId = chatRoomId;
    this.images = images;
  }

  @Override
  public String toString() {
    return "ChatUploadImageReqDto{" +
           "userId='" + userId + '\'' +
           ", chatRoomId='" + chatRoomId + '\'' +
           ", images=" + Arrays.toString(images) +
           '}';
  }
}
