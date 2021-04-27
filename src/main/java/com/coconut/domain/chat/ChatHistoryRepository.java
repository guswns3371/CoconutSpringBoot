package com.coconut.domain.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {

    Optional<List<ChatHistory>> findChatHistoriesByChatRoom_Id(Long chatRoomId);
}
