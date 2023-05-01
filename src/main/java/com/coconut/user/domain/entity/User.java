package com.coconut.user.domain.entity;

import com.coconut.base.domain.BaseTimeEntity;
import com.coconut.chat.domain.entity.ChatHistory;
import com.coconut.chat.domain.entity.UserChatHistory;
import com.coconut.chat.domain.entity.UserChatRoom;
import com.coconut.common.utils.mail.TokenGenerator;
import com.coconut.user.domain.constant.Role;
import java.util.ArrayList;
import java.util.List;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class User extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String email;

  private String usrId;
  private String password;
  private String stateMessage;
  private String profilePicture;
  private String backgroundPicture;
  private String confirmToken;
  private String fcmToken;

  @Enumerated(EnumType.STRING)
  private Role role = Role.GUEST;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private final List<UserChatRoom> userChatRoomList = new ArrayList<>();

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private final List<ChatHistory> chatHistoryList = new ArrayList<>();

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private final List<UserChatHistory> userChatHistoryList = new ArrayList<>();

  @Builder
  private User(String usrId, String name, String email, String password, String stateMessage, String profilePicture, String backgroundPicture, String confirmToken, Role role, String fcmToken) {
    this.usrId = usrId;
    this.name = name;
    this.email = email;
    this.password = password;
    this.stateMessage = stateMessage;
    this.profilePicture = profilePicture;
    this.backgroundPicture = backgroundPicture;
    this.confirmToken = confirmToken;
    this.role = role;
    this.fcmToken = fcmToken;
  }

  public static User create(String email, String name, String userId, String password, Role role) {
    return User.builder()
               .email(email)
               .name(name)
               .usrId(userId)
               .password(password)
               .role(role)
               .build();
  }

  public User update(User entity) {
    if (entity.getUsrId() != null) {
      this.usrId = entity.getUsrId();
    }
    if (entity.name != null) {
      this.name = entity.getName();
    }
    if (entity.profilePicture != null) {
      this.profilePicture = entity.getProfilePicture();
    }
    if (entity.backgroundPicture != null) {
      this.backgroundPicture = entity.getBackgroundPicture();
    }
    if (entity.getStateMessage() != null) {
      this.stateMessage = entity.getStateMessage();
    }
    if (entity.getFcmToken() != null) {
      this.fcmToken = entity.getFcmToken();
    }
    return this;
  }

  public String updateConfirmToken() {
    this.confirmToken = new TokenGenerator().generateNewToken();
    return this.confirmToken;
  }

  public void updateFcmToken(String fcmToken) {
    this.fcmToken = fcmToken;
  }

  public String getRoleKey() {
    return this.role.getKey();
  }

  public void approveUser() {
    this.role = Role.USER;
  }

  public UserChatRoom getUserChatRoom(String chatRoomId) {
    return this.userChatRoomList.stream().filter(it -> it.getChatRoom().getId().equals(Long.parseLong(chatRoomId))).collect(Collectors.toList()).get(0);
  }

  @Override
  public String toString() {
    return "User{" + "id=" + id + ", userId='" + usrId + '\'' + ", name='" + name + '\'' + ", email='" + email + '\'' + ", password='" + password + '\'' + ", stateMessage='" + stateMessage + '\''
           + ", profilePicture='" + profilePicture + '\'' + ", backgroundPicture='" + backgroundPicture + '\'' + ", confirmToken='" + confirmToken + '\'' + ", fcmToken='" + fcmToken + '\'' + ", role="
           + role + '}';
  }
}
