package com.coconut.api.dto.req;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
@Getter
public class ChatRoomDataReqDto {
    /**
     *     @SerializedName("chatUserId") var chatUserId : String?,
     *     @SerializedName("chatRoomId") var chatRoomId : String?,
     *     @SerializedName("chatRoomMembers") var chatRoomMembers : ArrayList<String>
     */

    private String chatUserId;
    private String chatRoomId;
    private ArrayList<String> chatRoomMembers;

    @Builder
    public ChatRoomDataReqDto(String chatUserId, String chatRoomId, ArrayList<String> chatRoomMembers) {
        this.chatUserId = chatUserId;
        this.chatRoomId = chatRoomId;
        this.chatRoomMembers = chatRoomMembers;
    }

    @Override
    public String toString() {
        return "ChatRoomDataReqDto{" +
                "chatUserId='" + chatUserId + '\'' +
                ", chatRoomId='" + chatRoomId + '\'' +
                ", chatRoomMembers=" + chatRoomMembers +
                '}';
    }
}
