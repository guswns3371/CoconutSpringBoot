package com.coconut.service;

import com.coconut.domain.chat.ChatRoom;
import com.coconut.domain.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public Long save(ChatRoom chatRoom) {
        if (validateDuplicateChatRoom(chatRoom)) {
            throw new IllegalStateException("이미 존재하는 채팅방입니다.");
        }
        return chatRoomRepository.save(chatRoom).getId();
    }

    private boolean validateDuplicateChatRoom(ChatRoom chatRoom) {
        return chatRoomRepository.existsChatRoomByMembers(chatRoom.getMembers());
    }

    @Transactional
    public void updateMembers(Long chatRoomId,String updateMembers) {
        chatRoomRepository.updateMembers(chatRoomId, updateMembers);
    }

    @Transactional
    public void updateLastMessage(Long chatRoomId,String lastMessage) {
        chatRoomRepository.updateLastMessage(chatRoomId, lastMessage);
    }
}
