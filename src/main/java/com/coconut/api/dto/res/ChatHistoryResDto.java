package com.coconut.api.dto.res;

import com.coconut.domain.chat.ChatHistory;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

@NoArgsConstructor
@Getter
public class ChatHistoryResDto {
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
    private int readCount;
    private String time;
    private String history;
    private ArrayList<String> chatImages;
    private String messageType;

    @Builder
    public ChatHistoryResDto(UserDataResDto userInfo, String chatRoomId, String chatUserId, int readCount, String time, String history, ArrayList<String> chatImages, String messageType) {
        this.userInfo = userInfo;
        this.chatRoomId = chatRoomId;
        this.chatUserId = chatUserId;
        this.readCount = readCount;
        this.time = time;
        this.history = history;
        this.chatImages = chatImages;
        this.messageType = messageType;
    }

    public ChatHistoryResDto(ChatHistory entity) {
        this.userInfo = new UserDataResDto(entity.getUser());
        this.chatRoomId = entity.getId().toString();
        this.chatUserId = entity.getUser().getId().toString();
        this.readCount = entity.getReadCount();
        this.time = entity.getCreatedDate().format(
                DateTimeFormatter.ofPattern("a h:mm", Locale.KOREA));
//        this.time = entity.getCreatedDate().toString();
        this.history = entity.getHistory();
        String images = entity.getChatImages();
        this.chatImages = (images == null) ? null : new ArrayList<>(Arrays.asList(images.substring(1, images.length() - 1).split(", ")));
        this.messageType = entity.getMessageTypeKey();
    }
}
