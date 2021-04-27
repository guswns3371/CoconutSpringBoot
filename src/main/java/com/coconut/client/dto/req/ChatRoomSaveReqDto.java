package com.coconut.client.dto.req;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
@Getter
public class ChatRoomSaveReqDto {

    /**
     *     @SerializedName("chatUserId") var chatUserId : String?,
     *     @SerializedName("chatRoomMembers") var chatRoomMembers : ArrayList<String>
     */

    private String chatUserId;
    private ArrayList<String> chatRoomMembers;

    @Builder
    public ChatRoomSaveReqDto(String chatUserId, ArrayList<String> chatRoomMembers) {
        this.chatUserId = chatUserId;
        this.chatRoomMembers = chatRoomMembers;
    }

}
