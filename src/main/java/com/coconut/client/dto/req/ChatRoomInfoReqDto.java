package com.coconut.client.dto.req;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
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
    public ChatRoomInfoReqDto(String id, String members, String roomType, String lastMessage, String lastTime) {
        this.id = id;
        this.members = members;
        this.roomType = roomType;
        this.lastMessage = lastMessage;
        this.lastTime = lastTime;
    }

    @Override
    public String toString() {
        return "ChatRoomInfoReqDto{" +
                "id='" + id + '\'' +
                ", members='" + members + '\'' +
                ", roomType='" + roomType + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", lastTime='" + lastTime + '\'' +
                '}';
    }
}
