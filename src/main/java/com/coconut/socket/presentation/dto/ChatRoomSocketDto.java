package com.coconut.socket.presentation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomSocketDto {

    /**
     *     var chatRoomId : String,
     *     var chatUserId : String
     */

    private String chatUserId;
    private String chatRoomId;

    @Builder
    public ChatRoomSocketDto(String chatUserId, String chatRoomId) {
        this.chatUserId = chatUserId;
        this.chatRoomId = chatRoomId;
    }

    @Override
    public String toString() {
        return "ChatRoomSocketDto{" +
                "chatUserId='" + chatUserId + '\'' +
                ", chatRoomId='" + chatRoomId + '\'' +
                '}';
    }
}
