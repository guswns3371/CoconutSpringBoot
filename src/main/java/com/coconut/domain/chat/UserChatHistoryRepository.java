package com.coconut.domain.chat;

import com.coconut.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface UserChatHistoryRepository extends JpaRepository<UserChatHistory, Long> {

    Optional<ArrayList<UserChatHistory>> findUserChatHistoriesByChatHistoryAndUser(ChatHistory chatHistory, User user);
    boolean existsUserChatHistoryByChatHistoryAndUser(ChatHistory chatHistory, User user);

    Optional<Boolean> deleteUserChatHistoriesByChatHistory_ChatRoom_IdAndUser_Id(Long chatRoomId, Long userId);
}
