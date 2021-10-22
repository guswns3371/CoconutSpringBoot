package com.coconut.api.dto.res;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
@Getter
public class ChatHistorySaveResDto {
    /**
     * @SerializedName("userInfo") var userInfo : UserDataResponse,
     * @SerializedName("chatRoomId") var chatRoomId : String,
     * @SerializedName("chatUserId") var chatUserId : String,
     * @SerializedName("readMembers") var readMembers : String,
     * @SerializedName("time") var time : String,
     * @SerializedName("history") var history : String,
     * @SerializedName("chatImages") var chatImages : String,
     * @SerializedName("messageType") var messageType : Boolean?,
     */

    private UserDataResDto userInfo;
    private String chatRoomId;
    private String chatUserId;
    private String readMembers;
    private String time;
    private String history;
    private ArrayList<String> chatImages;
    private String messageType;

    @Builder
    public ChatHistorySaveResDto(UserDataResDto userInfo, String chatRoomId, String chatUserId, String readMembers, String time, String history, ArrayList<String> chatImages, String messageType) {
        this.userInfo = userInfo;
        this.chatRoomId = chatRoomId;
        this.chatUserId = chatUserId;
        this.readMembers = readMembers;
        this.time = time;
        this.history = history;
        this.chatImages = chatImages;
        this.messageType = messageType;
    }

    @Override
    public String toString() {
        return "ChatHistorySaveResDto{" +
                "userInfo=" + userInfo +
                ", chatRoomId='" + chatRoomId + '\'' +
                ", chatUserId='" + chatUserId + '\'' +
                ", readMembers='" + readMembers + '\'' +
                ", time='" + time + '\'' +
                ", history='" + history + '\'' +
                ", chatImages='" + chatImages + '\'' +
                ", messageType='" + messageType + '\'' +
                '}';
    }
}
