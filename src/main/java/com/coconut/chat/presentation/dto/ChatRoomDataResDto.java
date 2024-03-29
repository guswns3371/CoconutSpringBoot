package com.coconut.chat.presentation.dto;

import com.coconut.auth.presentation.dto.UserDataResDto;
import java.util.ArrayList;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ChatRoomDataResDto {

  /**
   *     @SerializedName("chatRoomId") var chatRoomId : String,
   *     @SerializedName("chatRoomName") var chatRoomName : String,
   *     @SerializedName("chatRoomMembers") var chatRoomMembers : String,
   *     @SerializedName("chatRoomMembersInfo") var chatRoomMembersInfo : ArrayList<UserDataResponse>
   */

  private String chatRoomId;
  private String chatRoomName;
  private ArrayList<String> chatRoomMembers;
  private ArrayList<UserDataResDto> chatRoomMembersInfo;

  @Builder
  public ChatRoomDataResDto(String chatRoomId, String chatRoomName, ArrayList<String> chatRoomMembers, ArrayList<UserDataResDto> chatRoomMembersInfo) {
    this.chatRoomId = chatRoomId;
    this.chatRoomName = chatRoomName;
    this.chatRoomMembers = chatRoomMembers;
    this.chatRoomMembersInfo = chatRoomMembersInfo;
  }

}
