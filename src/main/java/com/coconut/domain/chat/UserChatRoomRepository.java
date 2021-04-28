package com.coconut.domain.chat;

import com.coconut.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserChatRoomRepository extends JpaRepository<UserChatRoom,Long> {

    Optional<UserChatRoom> findUserChatRoomByUser (User user);

    Optional<UserChatRoom> findUserChatRoomByChatRoom (ChatRoom chatRoom);
}