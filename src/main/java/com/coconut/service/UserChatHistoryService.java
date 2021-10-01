package com.coconut.service;

import com.coconut.domain.chat.UserChatHistory;
import com.coconut.domain.chat.UserChatHistoryRepository;
import com.coconut.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserChatHistoryService {

    private final UserChatHistoryRepository userChatHistoryRepository;

    @Transactional
    public UserChatHistory save(UserChatHistory userChatHistory) {
        return userChatHistoryRepository.save(userChatHistory);
    }

    public boolean exits(Long chatHistoryId, Long userId) {
        return userChatHistoryRepository.existsByChatHistory_IdAndUser_Id(chatHistoryId, userId);
    }

    public ArrayList<User> findReadUsers(Long chatHistoryId) {
        ArrayList<UserChatHistory> result = userChatHistoryRepository.findUserChatHistoriesByChatHistory_Id(chatHistoryId);
        return result.stream()
                .map(UserChatHistory::getUser)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
