package com.coconut.domain.chat;

import com.coconut.domain.BaseTimeEntity;
import com.coconut.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class UserChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String chatRoomName;

    @Column
    private int unReads;

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
    public void setUser(User user) {
        this.user = user;
        if (!user.getChatRoomList().contains(this)) {
            user.getChatRoomList().add(this);
        }
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
        if (!chatRoom.getUserList().contains(this)) {
            chatRoom.getUserList().add(this);
        }
    }
}
