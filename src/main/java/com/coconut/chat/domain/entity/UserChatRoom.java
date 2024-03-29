package com.coconut.chat.domain.entity;

import com.coconut.base.domain.BaseTimeEntity;
import com.coconut.chat.domain.constant.AbleType;
import com.coconut.user.domain.entity.User;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

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

  private UserChatRoom(User user, ChatRoom chatRoom) {
    setUser(user);
    setChatRoom(chatRoom);
  }

  public static UserChatRoom create(User user, ChatRoom chatRoom) {
    return new UserChatRoom(user, chatRoom);
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

  public void remove() {
    user.getUserChatRoomList().remove(this);
    chatRoom.getUserChatRoomList().remove(this);
  }

  public void removeHistory() {
    user.getUserChatHistoryList().removeIf(it -> it.getUser().equals(user));
    chatRoom.getChatHistoryList().removeIf(it -> it.getChatRoom().equals(chatRoom));
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
