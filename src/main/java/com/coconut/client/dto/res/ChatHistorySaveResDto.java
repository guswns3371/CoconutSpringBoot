package com.coconut.client.dto.res;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ChatHistorySaveResDto {
    /**
     *     @SerializedName("userInfo") var userInfo : UserDataResponse,
     *     @SerializedName("chatRoomId") var chatRoomId : String,
     *     @SerializedName("chatUserId") var chatUserId : String,
     *     @SerializedName("readMembers") var readMembers : String,
     *     @SerializedName("time") var time : String,
     *     @SerializedName("history") var history : String,
     *     @SerializedName("messageType") var messageType : Boolean?
     */

    private UserDataResDto userInfo;
    private String chatRoomId;
    private String chatUserId;
    private String readMembers;
    private String time;
    private String history;
    private String messageType;

    @Builder
    public ChatHistorySaveResDto(UserDataResDto userInfo, String chatRoomId, String chatUserId, String readMembers, String time, String history, String messageType) {
        this.userInfo = userInfo;
        this.chatRoomId = chatRoomId;
        this.chatUserId = chatUserId;
        this.readMembers = readMembers;
        this.time = time;
        this.history = history;
        this.messageType = messageType;
    }

    @Override
    public String toString() {
        return "ChatHistorySaveResDto{" +
                "userInfo=" + userInfo.toString() +
                ", chatRoomId='" + chatRoomId + '\'' +
                ", chatUserId='" + chatUserId + '\'' +
                ", readMembers='" + readMembers + '\'' +
                ", time='" + time + '\'' +
                ", history='" + history + '\'' +
                ", messageType=" + messageType +
                '}';
    }
}
