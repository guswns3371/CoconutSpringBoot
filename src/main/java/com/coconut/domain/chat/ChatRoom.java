package com.coconut.domain.chat;

import com.coconut.client.dto.req.ChatRoomInfoReqDto;
import com.coconut.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String lastMessage;

    @Column
    private String members;

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserChatRoom> userList = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ChatHistory> chatHistoryList = new ArrayList<>();

    @Builder
    public ChatRoom(String lastMessage, String members) {
        this.lastMessage = lastMessage;
        this.members = members;
    }

    public void updateLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public ChatRoomInfoReqDto toChatRoomInfoReqDto() {
        return ChatRoomInfoReqDto.builder()
                .id(id.toString())
                .lastMessage(lastMessage)
                .lastTime(getModifiedData().format(DateTimeFormatter.ofPattern("a h시 mm분")))
                .members(members)
                .build();
    }

}
