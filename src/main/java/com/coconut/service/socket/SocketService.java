package com.coconut.service.socket;

import com.coconut.client.dto.ChatMessageSocketDto;
import com.coconut.client.dto.ChatRoomSocketDto;
import com.coconut.client.dto.res.ChatHistorySaveResDto;
import com.coconut.domain.chat.*;
import com.coconut.domain.user.User;
import com.coconut.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocketService {

    private static List<String> connectedUserList = Collections.synchronizedList(new ArrayList<>());
    private static Map<String, ArrayList<String>> enteredUserMap = Collections.synchronizedMap(new HashMap<>());
    private final SimpMessageSendingOperations messageSender;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatHistoryRepository chatHistoryRepository;

    private ArrayList<String> addUser(String chatRoomId, String userId) {
        if (enteredUserMap.isEmpty() || !enteredUserMap.containsKey(chatRoomId)) {
            enteredUserMap.put(chatRoomId, new ArrayList<>());
        }
        ArrayList<String> users = enteredUserMap.get(chatRoomId);
        if (!users.contains(userId))
            users.add(userId);

        return users.parallelStream().distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private ArrayList<String> removeUser(String chatRoomId, String userId) {
        if (enteredUserMap.isEmpty() || !enteredUserMap.containsKey(chatRoomId)) {
            return new ArrayList<>();
        }

        ArrayList<String> users = enteredUserMap.get(chatRoomId);

        if (users.size() == 0) {
            enteredUserMap.remove(chatRoomId);
            return new ArrayList<>();
        }

        if (!users.contains(userId)) {
            return users;
        }

        users.remove(userId);

        if (users.size() == 0) {
            enteredUserMap.remove(chatRoomId);
        }

        return users.parallelStream().distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void enterUsers(ChatRoomSocketDto dto) {
        String chatRoomId = dto.getChatRoomId();
        String userId = dto.getChatUserId();
        ArrayList<String> users = addUser(chatRoomId, userId);

        log.warn("유저=" + userId + " : " + chatRoomId + "번 채팅방 enter =" + enteredUserMap);

        messageSender.convertAndSend("/sub/chat/room/" + dto.getChatRoomId(), users);
    }

    public void exitUsers(ChatRoomSocketDto dto) {
        String chatRoomId = dto.getChatRoomId();
        String userId = dto.getChatUserId();
        ArrayList<String> users = removeUser(chatRoomId, userId);

        log.warn("유저=" + userId + " : " + chatRoomId + "번 채팅방 exit =" + enteredUserMap);

        messageSender.convertAndSend("/sub/chat/room/" + dto.getChatRoomId(), users);
    }

    public List<String> onlineUsers(String userIndex) {
        connectedUserList.add(userIndex.trim());
        connectedUserList = connectedUserList.parallelStream()
                .distinct()
                .collect(Collectors.toList());
        log.warn("online User = " + userIndex.trim() + " : " + connectedUserList);
        return connectedUserList;
    }

    public List<String> offlineUsers(String userIndex) {
        connectedUserList.remove(userIndex.trim());
        connectedUserList = connectedUserList.parallelStream()
                .distinct()
                .collect(Collectors.toList());
        if (!connectedUserList.isEmpty()) {
            log.warn("offline User = " + userIndex.trim() + " : " + connectedUserList);
        } else {
            log.warn("offline User = " + userIndex.trim());
        }
        return connectedUserList;
    }

    @Transactional
    public void sendMessage(ChatMessageSocketDto dto) {
        String userId  = dto.getChatUserId();
        String chatRoomId = dto.getChatRoomId();

        Optional<User> optionalUser = userRepository.findUserById(Long.parseLong(userId));
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findChatRoomById(Long.parseLong(chatRoomId));

        if (!optionalUser.isPresent() || !optionalChatRoom.isPresent()) {
            return;
        }

        User user = optionalUser.get();
        ChatRoom chatRoom = optionalChatRoom.get();
        String chatMessage = dto.getChatMessage();
        ArrayList<String> readMembers = dto.getReadMembers();
        Collections.sort(readMembers);

        ChatHistory chatHistory = chatHistoryRepository.save(
                ChatHistory.builder()
                        .user(user)
                        .chatRoom(chatRoom)
                        .history(chatMessage)
                        .messageType(MessageType.TEXT)
                        .readMembers(readMembers.toString())
                        .build()
        );

        ChatHistorySaveResDto resDto = chatHistory.toChatHistorySaveResDto();
        log.warn(resDto.toString());

        messageSender.convertAndSend("/sub/chat/message/" + dto.getChatRoomId(), resDto);
    }

}
