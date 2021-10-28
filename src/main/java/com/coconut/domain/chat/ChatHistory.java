package com.coconut.domain.chat;

import com.coconut.api.dto.res.ChatHistorySaveResDto;
import com.coconut.api.dto.res.UserDataResDto;
import com.coconut.domain.BaseTimeEntity;
import com.coconut.domain.user.User;
import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Getter
@NoArgsConstructor
@Entity
public class ChatHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_history_id")
    private Long id;

    @Column
    private String history;

    @Column
    private int readCount;

    @Column(length = 800)
    private String chatImages;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType = MessageType.TEXT;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @OneToMany(mappedBy = "chatHistory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserChatHistory> userChatHistoryList = new ArrayList<>();

    @Builder
    public ChatHistory(String history, int readCount, String chatImages, MessageType messageType, User user, ChatRoom chatRoom) {
        this.history = history;
        this.readCount = readCount;
        this.chatImages = chatImages;
        this.messageType = messageType;
        setUser(user);
        setChatRoom(chatRoom);
    }

    private void setUser(User user) {
        this.user = user;
        if (!user.getChatHistoryList().contains(this)) {
            user.getChatHistoryList().add(this);
        }
    }

    private void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
        if (!chatRoom.getChatHistoryList().contains(this)) {
            chatRoom.getChatHistoryList().add(this);
        }
    }

    public String getMessageTypeKey() {
        return this.messageType.getKey();
    }

    public void updateReadCount() {
        readCount = userChatHistoryList.size();
    }

    public ChatHistorySaveResDto toChatHistorySaveResDto() {

        ArrayList<String> chatImagesString = null;
        if (chatImages != null) {
            chatImagesString =
                    new GsonBuilder().create().fromJson(chatImages, new TypeToken<ArrayList<String>>() {
                    }.getType());
        }

        return ChatHistorySaveResDto.builder()
                .userInfo(new UserDataResDto(user))
                .chatRoomId(chatRoom.getId().toString())
                .chatUserId(user.getId().toString())
                .readCount(readCount)
                .time(getCreatedDate().format(DateTimeFormatter.ofPattern("a h: mm", Locale.KOREA)))
                .history(history)
                .chatImages(chatImagesString)
                .messageType(getMessageTypeKey())
                .build();
    }
}
