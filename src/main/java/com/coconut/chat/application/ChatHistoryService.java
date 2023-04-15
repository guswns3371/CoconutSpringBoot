package com.coconut.chat.application;

import com.coconut.chat.domain.entity.ChatHistory;
import com.coconut.chat.domain.repository.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ChatHistoryService {

    private final ChatHistoryRepository chatHistoryRepository;

    @Transactional
    public Long save(ChatHistory chatHistory) {
        return chatHistoryRepository.save(chatHistory).getId();
    }

    @Transactional
    public void deleteAllByUserIdAndChatRoomId(Long userId, Long chatRoomId) {
        chatHistoryRepository.deleteAllByChatRoomIdAndUserId(chatRoomId, userId);
    }

    public Optional<ArrayList<ChatHistory>> findAllMessages(Long chatRoomId) {
        return chatHistoryRepository.findChatHistoriesByChatRoom_Id(chatRoomId);
    }

    public Optional<ArrayList<ChatHistory>> findMessagesByChatRoomIdAndUserId(Long chatRoomId, Long userId) {
        return chatHistoryRepository.findChatHistoriesByChatRoom_IdAndAndUser_Id(chatRoomId, userId);
    }

}
