package com.coconut.client.dto.req;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ChatRoomInfoReqDto {

    /**
     *     @SerializedName("id") var id : String?,
     *     @SerializedName("members") var members : String?,
     *     @SerializedName("lastMessage") var lastMessage : String?,
     *     @SerializedName("lastTime") var lastTime : String?
     */

    private String id;
    private String members;
    private String lastMessage;
    private String lastTime;

    @Builder
    public ChatRoomInfoReqDto(String id, String members, String lastMessage, String lastTime) {
        this.id = id;
        this.members = members;
        this.lastMessage = lastMessage;
        this.lastTime = lastTime;
    }

    @Override
    public String toString() {
        return "ChatRoomInfoReqDto{" +
                "id='" + id + '\'' +
                ", members='" + members + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", lastTime='" + lastTime + '\'' +
                '}';
    }
}