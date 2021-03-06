package com.coconut.domain.chat;

import com.coconut.domain.BaseTimeEntity;
import com.coconut.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    private List<UserChatRoom> userChatRoomList = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ChatHistory> chatHistoryList = new ArrayList<>();

    @Builder
    public ChatRoom(String lastMessage, String members, RoomType roomType) {
        this.lastMessage = lastMessage;
        this.members = members;
        this.roomType = roomType;
    }

    public void updateLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
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

        if (userChatRooms.isEmpty())
            return null;

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
