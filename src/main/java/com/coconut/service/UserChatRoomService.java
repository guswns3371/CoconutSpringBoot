package com.coconut.service;

import com.coconut.domain.chat.UserChatRoom;
import com.coconut.domain.chat.UserChatRoomRepository;
import com.coconut.domain.user.UserRepository;
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
public class UserChatRoomService {

    private final UserChatRoomRepository userChatRoomRepository;
    private final UserRepository userRepository;

    @Transactional
    public UserChatRoom save(UserChatRoom userChatRoom) {
        return userChatRoomRepository.save(userChatRoom);
    }

    public Optional<ArrayList<UserChatRoom>> findAllByUserId(Long userId) {
        return userChatRoomRepository.findAllByUser_Id(userId);
    }

    public Optional<ArrayList<UserChatRoom>> findAllByChatRoomId(Long chatRoomId) {
        return userChatRoomRepository.findAllByChatRoom_Id(chatRoomId);
    }

    public Optional<UserChatRoom> findByUserIdAndChatRoomId(Long userId, Long chatRoomId) {
        return userChatRoomRepository.findUserChatRoomByChatRoom_IdAndUser_Id(chatRoomId, userId);
    }
}
