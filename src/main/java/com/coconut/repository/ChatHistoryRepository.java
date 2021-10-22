package com.coconut.repository;

import com.coconut.domain.chat.ChatHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {

    // ChatHistory 의 user 객체의 지연로딩 설정을 무시하고 fetch
    @EntityGraph(attributePaths = {"user"})
    Optional<ArrayList<ChatHistory>> findChatHistoriesByChatRoom_Id(Long chatRoomId);

    Optional<ArrayList<ChatHistory>> findChatHistoriesByChatRoom_IdAndAndUser_Id(Long chatRoomId, Long userId);

    void deleteAllByChatRoomIdAndUserId(Long chatRoomId, Long userId);

}
