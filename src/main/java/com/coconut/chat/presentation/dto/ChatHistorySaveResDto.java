package com.coconut.chat.presentation.dto;

import com.coconut.auth.presentation.dto.UserDataResDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
@Data
public class ChatHistorySaveResDto {
    /**
     * @SerializedName("userInfo") var userInfo : UserDataResponse,
     * @SerializedName("chatRoomId") var chatRoomId : String,
     * @SerializedName("chatUserId") var chatUserId : String,
     * @SerializedName("readCount") var readMembers : String,
     * @SerializedName("time") var time : String,
     * @SerializedName("history") var history : String,
     * @SerializedName("chatImages") var chatImages : String,
     * @SerializedName("messageType") var messageType : Boolean?,
     */

    private UserDataResDto userInfo;
    private String chatRoomId;
    private String chatUserId;
    private int readCount;
    private String time;
    private String history;
    private ArrayList<String> chatImages;
    private String messageType;

    @Builder
    public ChatHistorySaveResDto(UserDataResDto userInfo, String chatRoomId, String chatUserId, int readCount, String time, String history, ArrayList<String> chatImages, String messageType) {
        this.userInfo = userInfo;
        this.chatRoomId = chatRoomId;
        this.chatUserId = chatUserId;
        this.readCount = readCount;
        this.time = time;
        this.history = history;
        this.chatImages = chatImages;
        this.messageType = messageType;
    }


}
