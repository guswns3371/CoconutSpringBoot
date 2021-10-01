package com.coconut.domain.chat;

import com.coconut.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface UserChatHistoryRepository extends JpaRepository<UserChatHistory, Long> {

    Optional<ArrayList<UserChatHistory>> findUserChatHistoriesByChatHistoryAndUser(ChatHistory chatHistory, User user);

    boolean existsUserChatHistoryByChatHistoryAndUser(ChatHistory chatHistory, User user);

    boolean existsByChatHistory_IdAndUser_Id(Long chatHistoryId, Long userId);

    ArrayList<UserChatHistory> findUserChatHistoriesByChatHistory_Id(Long chatHistoryId);
}
