package com.coconut.repository;

import com.coconut.domain.chat.UserChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface UserChatRoomRepository extends JpaRepository<UserChatRoom, Long> {

    Optional<UserChatRoom> findUserChatRoomByChatRoom_IdAndUser_Id(Long chatRoomId, Long userId);

    ArrayList<UserChatRoom> findUserChatRoomsByUser_IdOrderByModifiedDateDesc(Long userId);

    Optional<ArrayList<UserChatRoom>> findAllByChatRoom_Id(Long chatRoomId);

    boolean existsUserChatRoomByChatRoom_IdAndUser_Id(Long chatRoomId,Long userId);

}
