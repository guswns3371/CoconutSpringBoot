package com.coconut.domain.user;

import com.coconut.api.dto.req.UserSaveReqDto;
import com.coconut.domain.BaseTimeEntity;
import com.coconut.domain.chat.ChatHistory;
import com.coconut.domain.chat.ChatRoom;
import com.coconut.domain.chat.UserChatHistory;
import com.coconut.domain.chat.UserChatRoom;
import com.coconut.service.utils.mail.TokenGenerator;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private List<UserChatRoom> userChatRoomList = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ChatHistory> chatHistoryList = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserChatHistory> userChatHistoryList = new ArrayList<>();

    @Builder
    public User(String usrId, String name, String email, String password, String stateMessage, String profilePicture, String backgroundPicture, String confirmToken, Role role, String fcmToken) {
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

    public User update(User entity) {
        if (entity.getUsrId() != null)
            this.usrId = entity.getUsrId();
        if (entity.name != null)
            this.name = entity.getName();
        if (entity.profilePicture != null)
            this.profilePicture = entity.getProfilePicture();
        if (entity.backgroundPicture != null)
            this.backgroundPicture = entity.getBackgroundPicture();
        if (entity.getStateMessage() != null)
            this.stateMessage = entity.getStateMessage();
        if (entity.getFcmToken() != null)
            this.fcmToken = entity.getFcmToken();

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

    public void disapproveUser() {
        this.role = Role.GUEST;
    }

    public UserChatRoom getUserChatRoom(String chatRoomId) {
        return this.userChatRoomList.stream()
                .filter(it -> it.getChatRoom().getId().equals(Long.parseLong(chatRoomId)))
                .collect(Collectors.toList())
                .get(0);
    }

    public int getReadMessageCount(String chatRoomId) {
        return (int) this.userChatHistoryList.stream()
                .map(UserChatHistory::getChatHistory)
                .filter(it -> it.getChatRoom().getId().equals(Long.parseLong(chatRoomId)))
                .count();
    }

    public ArrayList<ChatRoom> getChatRooms() {
        return this.userChatRoomList.stream()
                .map(UserChatRoom::getChatRoom)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userId='" + usrId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", stateMessage='" + stateMessage + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                ", backgroundPicture='" + backgroundPicture + '\'' +
                ", confirmToken='" + confirmToken + '\'' +
                ", fcmToken='" + fcmToken + '\'' +
                ", role=" + role +
                '}';
    }
}
