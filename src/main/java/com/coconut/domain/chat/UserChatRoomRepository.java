package com.coconut.domain.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface UserChatRoomRepository extends JpaRepository<UserChatRoom, Long> {

    Optional<UserChatRoom> findUserChatRoomByChatRoom_IdAndUser_Id(Long chatRoomId, Long userId);

    Optional<ArrayList<UserChatRoom>> findUserChatRoomsByUser_IdOrderByModifiedDataDesc(Long userId);

    Optional<Boolean> deleteUserChatRoomByChatRoom_IdAndUser_Id(Long chatRoomId, Long userId);

}
