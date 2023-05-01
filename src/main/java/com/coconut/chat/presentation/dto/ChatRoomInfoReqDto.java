package com.coconut.chat.presentation.dto;

import com.coconut.chat.domain.entity.ChatRoom;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ChatRoomInfoReqDto {

  /**
   * @SerializedName("id") var id : String?,
   * @SerializedName("members") var members : String?,
   * @SerializedName("roomType") var roomType : String?,
   * @SerializedName("lastMessage") var lastMessage : String?,
   * @SerializedName("lastTime") var lastTime : String?
   */

  private String id;
  private String members;
  private String roomType;
  private String lastMessage;
  private String lastTime;

  @Builder
  public ChatRoomInfoReqDto(ChatRoom entity) {
    this.id = entity.getId().toString();
    this.members = entity.getMembers();
    this.roomType = entity.getRoomType().getKey();
    this.lastMessage = entity.getLastMessage();
    this.lastTime = entity.getModifiedDate().format(DateTimeFormatter.ofPattern("a h: mm", Locale.KOREA));
  }

}
