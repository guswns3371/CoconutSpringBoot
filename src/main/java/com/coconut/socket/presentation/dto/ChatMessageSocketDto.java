package com.coconut.socket.presentation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Getter
@NoArgsConstructor
public class ChatMessageSocketDto {
    /**
     * var chatRoomId : String,
     * var chatUserId : String,
     * var chatMessage : String?,
     * var chatRoomMembers : ArrayList<String>?,
     * var readMembers : ArrayList<String>?,
     * var chatImages : ArrayList<String?>?,
     * var messageType : String
     */

    private String chatRoomId;
    private String chatUserId;
    private String chatMessage;
    private ArrayList<String> chatRoomMembers;
    private ArrayList<String> readMembers;
    private ArrayList<String> chatImages;
    private String messageType;

    @Builder
    public ChatMessageSocketDto(String chatRoomId, String chatUserId, String chatMessage, ArrayList<String> chatRoomMembers, ArrayList<String> readMembers, ArrayList<String> chatImages, String messageType) {
        this.chatRoomId = chatRoomId;
        this.chatUserId = chatUserId;
        this.chatMessage = chatMessage;
        this.chatRoomMembers = chatRoomMembers;
        this.readMembers = readMembers;
        this.chatImages = chatImages;
        this.messageType = messageType;
    }


}
