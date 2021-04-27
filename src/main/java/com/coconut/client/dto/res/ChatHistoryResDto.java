package com.coconut.client.dto.res;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ChatHistoryResDto {
    /**
     *     @SerializedName("userInfo") var userInfo : UserDataResponse,
     *     @SerializedName("chatRoomId") var chatRoomId : String,
     *     @SerializedName("chatUserId") var chatUserId : String,
     *     @SerializedName("readMembers") var readMembers : String,
     *     @SerializedName("time") var time : String,
     *     @SerializedName("history") var history : String,
     *     @SerializedName("isFile") var isFile : Boolean?,
     */

    private UserDataResDto userInfo;
    private String chatRoomId;
    private String chatUserId;
    private String readMembers;
    private String time;
    private String history;
    private boolean isFile;

    @Builder
    public ChatHistoryResDto(UserDataResDto userInfo, String chatRoomId, String chatUserId, String readMembers, String time, String history, boolean isFile) {
        this.userInfo = userInfo;
        this.chatRoomId = chatRoomId;
        this.chatUserId = chatUserId;
        this.readMembers = readMembers;
        this.time = time;
        this.history = history;
        this.isFile = isFile;
    }
}
