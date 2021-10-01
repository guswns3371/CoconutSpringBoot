package com.coconut.domain.chat;

import com.coconut.domain.BaseTimeEntity;
import com.coconut.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor
@Getter
public class UserChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_chat_room_id")
    private Long id;

    @Column
    private String chatRoomName;

    @Column
    private int unReads;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AbleType ableType = AbleType.ENABLE;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @Builder
    public UserChatRoom(String chatRoomName, int unReads, User user, ChatRoom chatRoom) {
        this.chatRoomName = chatRoomName;
        this.unReads = unReads;
        setUser(user);
        setChatRoom(chatRoom);
    }

    // https://cornswrold.tistory.com/355
    private void setUser(User user) {
        this.user = user;
        if (!user.getUserChatRoomList().contains(this)) {
            user.getUserChatRoomList().add(this);
        }
    }

    private void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
        if (!chatRoom.getUserChatRoomList().contains(this)) {
            chatRoom.getUserChatRoomList().add(this);
        }
    }

    public String getCurrentChatRoomName(String userId) {
        if (chatRoomName != null) {
            return chatRoomName;
        } else if (chatRoom.getUsers().size() == 1) {
            return chatRoom.getUsers().get(0).getName();
        }
        return chatRoom.getUsers().stream()
                .filter(it -> !it.getId().equals(Long.parseLong(userId)))
                .map(User::getName)
                .collect(Collectors.joining(", "));
    }

    public void updateUnReads(int unReads) {
        this.unReads = unReads;
    }

    public void addUnReads() {
        this.unReads += 1;
    }

    public void updateChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public void disableChatRoom() {
        this.ableType = AbleType.DISABLE;
    }

    public void enableChatRoom() {
        this.ableType = AbleType.ENABLE;
    }
}
