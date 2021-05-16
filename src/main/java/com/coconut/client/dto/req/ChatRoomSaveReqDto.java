package com.coconut.client.dto.req;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
public class ChatRoomSaveReqDto {

    /**
     * @SerializedName("chatUserId") var chatUserId : String?,
     * @SerializedName("chatRoomMembers") var chatRoomMembers : ArrayList<String>
     */

    private String chatUserId;
    private ArrayList<String> chatRoomMembers;

    @Builder
    public ChatRoomSaveReqDto(String chatUserId, ArrayList<String> chatRoomMembers) {
        this.chatUserId = chatUserId;
        this.chatRoomMembers = chatRoomMembers;
    }

    public ArrayList<String> getDistinctChatRoomMembers() {
        return this.chatRoomMembers.stream().distinct().sorted().collect(Collectors.toCollection(ArrayList::new));
    }
}
