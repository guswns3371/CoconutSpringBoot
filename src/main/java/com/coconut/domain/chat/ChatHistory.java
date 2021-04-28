package com.coconut.domain.chat;

import com.coconut.client.dto.res.ChatHistoryResDto;
import com.coconut.client.dto.res.ChatHistorySaveResDto;
import com.coconut.domain.BaseTimeEntity;
import com.coconut.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.format.DateTimeFormatter;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType = MessageType.TEXT;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @Builder
    public ChatHistory(String history, String readMembers, MessageType messageType,  User user, ChatRoom chatRoom) {
        this.history = history;
        this.readMembers = readMembers;
        this.messageType = messageType;
        setUser(user);
        setChatRoom(chatRoom);
    }

    public void setUser(User user) {
        this.user = user;
        if (!user.getChatHistoryList().contains(this)) {
            user.getChatHistoryList().add(this);
        }
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
        if (!chatRoom.getChatHistoryList().contains(this)) {
            chatRoom.getChatHistoryList().add(this);
        }
    }

    public String getMessageTypeKey() {
        return this.messageType.getKey();
    }

    public ChatHistoryResDto toChatHistoryResDto() {
        return ChatHistoryResDto.builder()
                .userInfo(user.toUserDataResDto())
                .chatRoomId(chatRoom.getId().toString())
                .chatUserId(user.getId().toString())
                .readMembers(readMembers)
                .time(getCreatedData().format(DateTimeFormatter.ofPattern("a h시 mm분")))
                .history(history)
                .messageType(getMessageTypeKey())
                .build();
    }

    public ChatHistorySaveResDto toChatHistorySaveResDto() {
        return ChatHistorySaveResDto.builder()
                .userInfo(user.toUserDataResDto())
                .chatRoomId(chatRoom.getId().toString())
                .chatUserId(user.getId().toString())
                .readMembers(readMembers)
                .time(getCreatedData().format(DateTimeFormatter.ofPattern("a h시 mm분")))
                .history(history)
                .messageType(getMessageTypeKey())
                .build();
    }
}
