package com.coconut.domain.chat;

import com.coconut.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface UserChatRoomRepository extends JpaRepository<UserChatRoom, Long> {

    Optional<UserChatRoom> findUserChatRoomByUser(User user);

    Optional<UserChatRoom> findUserChatRoomByChatRoom_Id(Long chatRoomId);

    Optional<UserChatRoom> findUserChatRoomByChatRoom_IdAndUser_Id(Long chatRoomId, Long userId);

    Optional<ArrayList<UserChatRoom>> findUserChatRoomsByUser_Id(Long userId);

    Optional<ArrayList<UserChatRoom>> findUserChatRoomsByUser_IdOrderByModifiedDataDesc(Long userId);

    Optional<ArrayList<UserChatRoom>> findUserChatRoomsByChatRoom_IdAndUser_Id(Long chatRoomId, Long userId);
}
