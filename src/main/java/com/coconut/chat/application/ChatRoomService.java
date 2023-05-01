package com.coconut.chat.application;

import com.coconut.chat.domain.entity.ChatRoom;
import com.coconut.chat.domain.repository.ChatRoomRepository;
import com.coconut.user.domain.entity.User;
import java.util.ArrayList;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ChatRoomService {

  private final ChatRoomRepository chatRoomRepository;

  @Transactional
  public ChatRoom save(ChatRoom chatRoom) {
    return chatRoomRepository.save(chatRoom);
  }

  public boolean existsChatRoomByMembers(String members) {
    return chatRoomRepository.existsChatRoomByMembers(members);
  }

  public Optional<ChatRoom> findByMembers(String members) {
    return chatRoomRepository.findChatRoomByMembers(members);
  }

  public Optional<ChatRoom> findById(Long chatRoomId) {
    return chatRoomRepository.findById(chatRoomId);
  }

  public ArrayList<User> findUsersByChatRoomId(Long chatRoomId) {
    Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(chatRoomId);
    if (optionalChatRoom.isEmpty()) {
      return new ArrayList<>();
    }

    return optionalChatRoom.get().getUsers();
  }

  @Transactional
  public void updateMembers(Long chatRoomId, String updateMembers) {
    chatRoomRepository.updateMembers(chatRoomId, updateMembers);
  }

  @Transactional
  public void updateLastMessage(Long chatRoomId, String lastMessage) {
    chatRoomRepository.updateLastMessage(chatRoomId, lastMessage);
  }
}
