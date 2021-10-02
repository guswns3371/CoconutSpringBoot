package com.coconut.domain.chat;

import com.coconut.domain.BaseTimeEntity;
import com.coconut.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.ArrayList;
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
        setUser(user);
        setChatRoom(chatRoom);
        this.unReads = unReads;
        this.chatRoomName = chatRoomName;
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

    public String getCurrentChatRoomName() {
        if (StringUtils.hasText(chatRoomName)) {
            return chatRoomName;
        }

        ArrayList<User> users = chatRoom.getUsers();
        if (users.size() == 1) {
            return users.get(0).getName();
        }

        return users.stream()
                .filter(it -> !it.equals(user))
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
