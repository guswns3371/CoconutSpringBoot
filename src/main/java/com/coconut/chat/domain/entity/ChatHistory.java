package com.coconut.chat.domain.entity;

import com.coconut.auth.presentation.dto.UserDataResDto;
import com.coconut.base.domain.BaseTimeEntity;
import com.coconut.chat.domain.constant.MessageType;
import com.coconut.chat.presentation.dto.ChatHistorySaveResDto;
import com.coconut.user.domain.entity.User;
import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
  private ChatHistory(User user, ChatRoom chatRoom, String history, MessageType messageType, String chatImages) {
    setUser(user);
    setChatRoom(chatRoom);
    this.history = Objects.requireNonNull(history);
    this.messageType = Objects.requireNonNull(messageType);
    this.chatImages = chatImages;
  }

  public static ChatHistory create(User user, ChatRoom chatRoom, String history, MessageType messageType) {
    return ChatHistory.builder()
                      .user(user)
                      .chatRoom(chatRoom)
                      .history(history)
                      .messageType(messageType)
                      .build();
  }

  public static ChatHistory create(User user, ChatRoom chatRoom, String history, MessageType messageType, String chatImages) {
    return ChatHistory.builder()
                      .user(user)
                      .chatRoom(chatRoom)
                      .history(history)
                      .messageType(messageType)
                      .chatImages(chatImages)
                      .build();
  }

  private void setUser(User user) {
    this.user = Objects.requireNonNull(user);
    if (!user.getChatHistoryList().contains(this)) {
      user.getChatHistoryList().add(this);
    }
  }

  private void setChatRoom(ChatRoom chatRoom) {
    this.chatRoom = Objects.requireNonNull(chatRoom);
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
