package com.coconut.chat.domain.repository;

import com.coconut.chat.domain.entity.ChatRoom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

  Optional<ChatRoom> findChatRoomByMembers(String members);

  boolean existsChatRoomByMembers(String members);

  @Modifying
  @Query("update ChatRoom cr set cr.members =: members where cr.id =: id")
  void updateMembers(@Param("id") Long chatRoomId, @Param("members") String members);

  @Modifying
  @Query("update ChatRoom cr set cr.lastMessage =: message where cr.id =: id")
  void updateLastMessage(@Param("id") Long chatRoomId, @Param("message") String message);

}
