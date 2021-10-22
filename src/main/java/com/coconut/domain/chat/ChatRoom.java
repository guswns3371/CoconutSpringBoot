package com.coconut.domain.chat;

import com.coconut.api.dto.req.ChatRoomInfoReqDto;
import com.coconut.domain.BaseTimeEntity;
import com.coconut.domain.user.User;
import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    @Column
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

    public List<Long> getLongChatMembers() {
        return new GsonBuilder().create().fromJson(this.members, new TypeToken<ArrayList<Long>>() {
        }.getType());
    }

    public List<String> getStringChatMembers() {
        return new GsonBuilder().create().fromJson(this.members, new TypeToken<ArrayList<String>>() {
        }.getType());
    }

    public UserChatRoom getUserChatRoom(String userId) {
        ArrayList<UserChatRoom> userChatRooms = this.userChatRoomList.stream()
                .filter(it -> it.getChatRoom().equals(this))
                .filter(it -> it.getUser().getId().equals(Long.parseLong(userId)))
                .collect(Collectors.toCollection(ArrayList::new));

        if (userChatRooms.size() == 0)
            return null;

        return userChatRooms.get(0);
    }

    public ArrayList<User> getUsers() {
        return this.userChatRoomList.stream()
                .map(UserChatRoom::getUser)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void exitChatRoom(String exitUserId) {
        List<String> members = getStringChatMembers();
        if (members.size() > 2)
            this.members = members.stream()
                    .filter(it -> !it.equals(exitUserId))
                    .collect(Collectors.toCollection(ArrayList::new))
                    .toString();
    }

    public void inviteMembers(ArrayList<String> members) {
        List<String> existMembers = getStringChatMembers();
        existMembers.addAll(members);
        existMembers = existMembers.stream().sorted().distinct().collect(Collectors.toList());
        this.members = existMembers.toString();
    }

    public ChatRoomInfoReqDto toChatRoomInfoReqDto() {
        return ChatRoomInfoReqDto.builder()
                .id(id.toString())
                .lastMessage(lastMessage)
                .roomType(roomType.getKey())
                .lastTime(getModifiedData().format(DateTimeFormatter.ofPattern("a h: mm")))
                .members(members)
                .build();
    }

}
