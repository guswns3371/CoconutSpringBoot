package com.coconut.chat.domain.repository;

import com.coconut.chat.domain.entity.UserChatRoom;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserChatRoomRepository extends JpaRepository<UserChatRoom, Long> {

  Optional<UserChatRoom> findUserChatRoomByChatRoom_IdAndUser_Id(Long chatRoomId, Long userId);

  ArrayList<UserChatRoom> findUserChatRoomsByUser_IdOrderByModifiedDateDesc(Long userId);

  Optional<ArrayList<UserChatRoom>> findAllByChatRoom_Id(Long chatRoomId);

  boolean existsUserChatRoomByChatRoom_IdAndUser_Id(Long chatRoomId, Long userId);

}
