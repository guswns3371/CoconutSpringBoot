package com.coconut.chat.presentation.dto;

import java.util.ArrayList;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ChatRoomDataReqDto {
  /**
   *     @SerializedName("chatUserId") var chatUserId : String?,
   *     @SerializedName("chatRoomId") var chatRoomId : String?,
   *     @SerializedName("chatRoomMembers") var chatRoomMembers : ArrayList<String>
   */

  private Long chatUserId;
  private Long chatRoomId;
  private ArrayList<String> chatRoomMembers;

  @Builder
  public ChatRoomDataReqDto(Long chatUserId, Long chatRoomId, ArrayList<String> chatRoomMembers) {
    this.chatUserId = chatUserId;
    this.chatRoomId = chatRoomId;
    this.chatRoomMembers = chatRoomMembers;
  }
}
