package com.coconut.chat.application;

import com.coconut.chat.domain.entity.ChatRoom;
import com.coconut.chat.domain.entity.UserChatRoom;
import com.coconut.user.domain.entity.User;
import com.coconut.chat.domain.repository.UserChatRoomRepository;
import com.coconut.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    /**
     * 트랜잭션 안에서 User 엔티티를 가져와 UserChatRoom 에 세팅해줘야 한다.
     * 영속성 컨택스트에 없는 User 객체로 UserChatRoom 을 생성할 경우
     * failed to lazily initialize a collection of role 에러 발생
     * <p>
     * -> UserChatRoom 을 생성할 때, 지연로딩 세팅된 user.getUserChatRoomList() 을 다루기 때문이다.
     */
    @Transactional
    public void saveAll(List<Long> userIds, ChatRoom chatRoom) {
        Optional<ArrayList<User>> optionalUsers = userRepository.findUserByIdIn(userIds);
        if (optionalUsers.isEmpty()) {
            throw new IllegalStateException("존재하지 않는 유저들 입니다.");
        }

        ArrayList<UserChatRoom> userChatRooms = optionalUsers.get().stream()
                .map(user -> UserChatRoom.builder()
                        .chatRoom(chatRoom)
                        .user(user)
                        .build())
                .collect(Collectors.toCollection(ArrayList::new));

        userChatRoomRepository.saveAll(userChatRooms);
    }

    public boolean exist(Long chatRoomId, Long userId) {
        return userChatRoomRepository.existsUserChatRoomByChatRoom_IdAndUser_Id(chatRoomId, userId);
    }
    public Optional<UserChatRoom> findByUserIdAndChatRoomId(Long userId, Long chatRoomId) {
        return userChatRoomRepository.findUserChatRoomByChatRoom_IdAndUser_Id(chatRoomId, userId);
    }

    public ArrayList<UserChatRoom> findAllByUserId(Long userId) {
        ArrayList<UserChatRoom> userChatRooms = userChatRoomRepository.findUserChatRoomsByUser_IdOrderByModifiedDateDesc(userId);
        userChatRooms.forEach(it -> it.updateChatRoomName(it.getCurrentChatRoomName()));
        return userChatRoomRepository.findUserChatRoomsByUser_IdOrderByModifiedDateDesc(userId);
    }

    public Optional<ArrayList<UserChatRoom>> findAllByChatRoomId(Long chatRoomId) {
        return userChatRoomRepository.findAllByChatRoom_Id(chatRoomId);
    }
}
