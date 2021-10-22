package com.coconut.domain.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findChatRoomByMembers(String members);

    Optional<ChatRoom> findChatRoomById(Long chatRoomId);
}
