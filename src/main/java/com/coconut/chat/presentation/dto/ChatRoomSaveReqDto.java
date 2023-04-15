package com.coconut.chat.presentation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
public class ChatRoomSaveReqDto {

    /**
     * @SerializedName("chatUserId") var chatUserId : String?,
     * @SerializedName("chatRoomMembers") var chatRoomMembers : ArrayList<String>
     */

    private Long chatUserId;
    private ArrayList<String> chatRoomMembers;

    @Builder
    public ChatRoomSaveReqDto(Long chatUserId, ArrayList<String> chatRoomMembers) {
        this.chatUserId = chatUserId;
        this.chatRoomMembers = chatRoomMembers.stream()
                .distinct()
                .sorted()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<String> getDistinctChatRoomMembers() {
        return this.chatRoomMembers.stream()
                .distinct()
                .sorted()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Long> getMemberLongIds() {
        return chatRoomMembers.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}
