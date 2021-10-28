package com.coconut.api.dto.req;

import com.coconut.domain.chat.ChatRoom;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
