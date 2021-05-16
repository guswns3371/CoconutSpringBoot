package com.coconut.domain.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {

    Optional<ArrayList<ChatHistory>> findChatHistoriesByChatRoom_Id(Long chatRoomId);

    Optional<ArrayList<ChatHistory>> findChatHistoriesByChatRoom_IdAndAndUser_Id(Long chatRoomId, Long userId);

    Optional<Boolean> deleteChatHistoriesByChatRoom_IdAndUser_Id(Long chatRoomId, Long userId);

}
