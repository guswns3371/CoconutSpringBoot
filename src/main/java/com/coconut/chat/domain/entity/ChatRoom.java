package com.coconut.chat.domain.entity;

import com.coconut.base.domain.BaseTimeEntity;
import com.coconut.chat.domain.constant.RoomType;
import com.coconut.user.domain.entity.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class ChatRoom extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "chat_room_id")
  private Long id;

  @Column
  private String lastMessage;

  @Column(unique = true)
  private String members;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RoomType roomType = RoomType.GROUP;

  @OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private final List<UserChatRoom> userChatRoomList = new ArrayList<>();

  @OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private final List<ChatHistory> chatHistoryList = new ArrayList<>();

  private ChatRoom(String members, RoomType roomType) {
    this.members = members;
    this.roomType = roomType;
  }

  public static ChatRoom create(String members, RoomType roomType) {
    return new ChatRoom(members, roomType);
  }

  public void updateLastMessage(String lastMessage) {
    this.lastMessage = Objects.requireNonNull(lastMessage);
  }

  public List<Long> getUserIds() {
    return getChatMembers().stream()
                           .map(Long::parseLong)
                           .collect(Collectors.toList());
  }

  public UserChatRoom getUserChatRoom(String userId) {
    ArrayList<UserChatRoom> userChatRooms = this.userChatRoomList.stream()
                                                                 .filter(it -> it.getChatRoom().equals(this))
                                                                 .filter(it -> it.getUser().getId().equals(Long.parseLong(userId)))
                                                                 .collect(Collectors.toCollection(ArrayList::new));

    if (userChatRooms.isEmpty()) {return null;}

    return userChatRooms.get(0);
  }

  public ArrayList<User> getUsers() {
    return this.userChatRoomList.stream()
                                .map(UserChatRoom::getUser)
                                .collect(Collectors.toCollection(ArrayList::new));
  }

  public void exitRoom(Long userId) {
    this.members = getChatMembers().stream()
                                   .filter(it -> !it.equals(userId.toString()))
                                   .collect(Collectors.toCollection(ArrayList::new))
                                   .toString();
  }

  public void addMembers(ArrayList<String> members) {
    List<String> existMembers = getChatMembers();
    existMembers.addAll(members);
    this.members = existMembers.stream().sorted().distinct().collect(Collectors.toList()).toString();
  }

  public List<String> getChatMembers() {
    return new ArrayList<>(Arrays.asList(members.substring(1, members.length() - 1).split(", ")));
  }

}
