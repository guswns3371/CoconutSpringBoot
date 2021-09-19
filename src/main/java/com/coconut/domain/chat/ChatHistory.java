package com.coconut.domain.chat;

import com.coconut.api.dto.res.ChatHistoryResDto;
import com.coconut.api.dto.res.ChatHistorySaveResDto;
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
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@Entity
public class ChatHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String history;

    @Column
    private String readMembers;

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
    public ChatHistory(String history, String readMembers, String chatImages, MessageType messageType, User user, ChatRoom chatRoom) {
        this.history = history;
        this.readMembers = readMembers;
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

    public void updateReadMembers(String readMembers) {
        this.readMembers = readMembers;
    }

    public ArrayList<User> getReadUsers() {
        return this.userChatHistoryList.stream()
                .map(UserChatHistory::getUser)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ChatHistoryResDto toChatHistoryResDto() {

        ArrayList<String> chatImagesString = null;
        if (chatImages != null) {
            chatImagesString =
                    new GsonBuilder().create().fromJson(chatImages, new TypeToken<ArrayList<String>>() {
                    }.getType());
        }

        return ChatHistoryResDto.builder()
                .userInfo(user.toUserDataResDto())
                .chatRoomId(chatRoom.getId().toString())
                .chatUserId(user.getId().toString())
                .readMembers(readMembers)
                .time(getCreatedData().format(DateTimeFormatter.ofPattern("a h: mm")))
                .history(history)
                .chatImages(chatImagesString)
                .messageType(getMessageTypeKey())
                .build();
    }

    public ChatHistorySaveResDto toChatHistorySaveResDto() {

        ArrayList<String> chatImagesString = null;
        if (chatImages != null) {
            chatImagesString =
                    new GsonBuilder().create().fromJson(chatImages, new TypeToken<ArrayList<String>>() {
                    }.getType());
        }

        return ChatHistorySaveResDto.builder()
                .userInfo(user.toUserDataResDto())
                .chatRoomId(chatRoom.getId().toString())
                .chatUserId(user.getId().toString())
                .readMembers(readMembers)
                .time(getCreatedData().format(DateTimeFormatter.ofPattern("a h: mm")))
                .history(history)
                .chatImages(chatImagesString)
                .messageType(getMessageTypeKey())
                .build();
    }
}
