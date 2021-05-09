package com.coconut.service.socket;

import com.coconut.client.dto.ChatMessageSocketDto;
import com.coconut.client.dto.ChatRoomSocketDto;
import com.coconut.client.dto.FcmMessageDto;
import com.coconut.client.dto.res.ChatHistorySaveResDto;
import com.coconut.config.fcm.FirebaseCloudMessageService;
import com.coconut.domain.chat.*;
import com.coconut.domain.user.User;
import com.coconut.domain.user.UserRepository;
import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocketService {

    private static List<String> connectedUserList = Collections.synchronizedList(new ArrayList<>());
    private static Map<String, ArrayList<String>> enteredUserMap = Collections.synchronizedMap(new HashMap<>());
    private final SimpMessageSendingOperations messageSender;
    private final FirebaseCloudMessageService firebaseCloudMessageService;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatHistoryRepository chatHistoryRepository;
    private final UserChatRoomRepository userChatRoomRepository;
    private final UserChatHistoryRepository userChatHistoryRepository;

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

    @Transactional
    public void enterUsers(ChatRoomSocketDto dto) {
        String chatRoomId = dto.getChatRoomId();
        String userId = dto.getChatUserId();
        ArrayList<String> users = addUser(chatRoomId, userId);

        log.warn("유저=" + userId + " : " + chatRoomId + "번 채팅방 enter =" + enteredUserMap);

        Optional<User> optionalUser = userRepository.findUserById(Long.parseLong(userId));
        Optional<ArrayList<ChatHistory>> optionalChatHistoryList = chatHistoryRepository.findChatHistoriesByChatRoom_Id(Long.parseLong(chatRoomId));

        // 채팅 기록 읽은 사람들 업데이트
        if (optionalChatHistoryList.isPresent() && optionalUser.isPresent()) {
            List<ChatHistory> chatHistoryList = optionalChatHistoryList.get();
            User user = optionalUser.get();

            for (ChatHistory history : chatHistoryList) {
                boolean isExist = userChatHistoryRepository.existsUserChatHistoryByChatHistoryAndUser(history, user);
                if (!isExist) {
                    userChatHistoryRepository.save(UserChatHistory.builder()
                            .chatHistory(history)
                            .user(user)
                            .build());
                }
            }
        }

        Optional<UserChatRoom> optionalUserChatRoom = userChatRoomRepository.findUserChatRoomByChatRoom_IdAndUser_Id(Long.parseLong(chatRoomId), Long.parseLong(userId));

        // 안읽은 메시지 0으로 설정
        if (optionalUserChatRoom.isPresent()) {
            UserChatRoom userChatRoom = optionalUserChatRoom.get();
            userChatRoom.updateUnReads(0);
        }

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
        log.warn(dto.toString());
        String userId = dto.getChatUserId();
        String chatRoomId = dto.getChatRoomId();
        String chatMessage = dto.getChatMessage();
        ArrayList<String> roomMembers = dto.getChatRoomMembers();
        ArrayList<String> readMembers = dto.getReadMembers();

        Optional<UserChatRoom> optionalUserChatRoom = userChatRoomRepository.findUserChatRoomByChatRoom_IdAndUser_Id(Long.parseLong(chatRoomId), Long.parseLong(userId));
        if (optionalUserChatRoom.isEmpty())
            return;

        UserChatRoom userChatRoom = optionalUserChatRoom.get();

        ChatRoom socketChatRoom = userChatRoom.getChatRoom();

        // 안읽은 메시지 개수 업데이트
        socketChatRoom.updateLastMessage(chatMessage);

        // 현재 채팅방속 유저들
        ArrayList<User> users = userChatRoom.getChatRoom().getUserList().stream()
                .map(UserChatRoom::getUser)
                .collect(Collectors.toCollection(ArrayList::new));

        // 메시지를 보낸 유저
        User socketUser = users.stream()
                .filter(it -> it.getId().equals(Long.parseLong(userId)))
                .collect(Collectors.toList()).get(0);

        // 채팅 기록 저장
        ChatHistory savedHistory = chatHistoryRepository.save(ChatHistory.builder()
                .user(socketUser)
                .chatRoom(socketChatRoom)
                .readMembers(Integer.toString(readMembers.size()))
                .history(chatMessage)
                .messageType(MessageType.TEXT)
                .build());

        ArrayList<User> ReadMembers = users.stream()
                .filter(it -> readMembers.contains(it.getId().toString()))
                .collect(Collectors.toCollection(ArrayList::new));

        // 메시지 읽음 표시
        for (User readUser : ReadMembers) {
            boolean isExist = userChatHistoryRepository.existsUserChatHistoryByChatHistoryAndUser(savedHistory, readUser);
            if (!isExist) {
                userChatHistoryRepository.save(UserChatHistory.builder()
                        .chatHistory(savedHistory)
                        .user(readUser)
                        .build());
            }
        }

        ChatHistorySaveResDto resDto = savedHistory.toChatHistorySaveResDto();
        messageSender.convertAndSend("/sub/chat/message/" + dto.getChatRoomId(), resDto);

        ArrayList<User> unReadMembers = users.stream()
                .filter(it -> !readMembers.contains(it.getId().toString()))
                .collect(Collectors.toCollection(ArrayList::new));

        // 메시지를 읽지 않은 유저들
        for (User unReadMember : unReadMembers) {
            String unReadMemberId = unReadMember.getId().toString();

            int totalHistoryCount = socketChatRoom.getChatHistoryList().size();

            int readCount = (int) unReadMember.getReadHistoryList().stream()
                    .map(UserChatHistory::getChatHistory)
                    .filter(it -> it.getChatRoom().getId().equals(Long.parseLong(chatRoomId)))
                    .count();

            int unReadCount = totalHistoryCount - readCount;


            unReadMember.getChatRoomList().stream()
                    .filter(it -> it.getChatRoom().equals(socketChatRoom))
                    .forEach(userChatRoom1 -> {
                        // fcm 알림
                        try {
                            String fcmToken = unReadMember.getFcmToken();

                            Map<String,String> data = new HashMap<>();
                            data.put("roomId",socketChatRoom.getId().toString());
                            data.put("roomPeople",roomMembers.toString());
                            data.put("userImage",unReadMember.getProfilePicture());
                            data.put("userName",unReadMember.getName());
                            data.put("title",userChatRoom1.getChatRoomName());
                            data.put("who",socketUser.getName());
                            data.put("body",chatMessage);

                            firebaseCloudMessageService.sendMessageTo(
                                    fcmToken,
                                    data,
                                    FcmMessageDto.Notification.builder().build());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            // 안읽은 메시지 업데이트
                            userChatRoom1.updateUnReads(unReadCount);
                            messageSender.convertAndSend("/sub/chat/frag/" + unReadMemberId, " ");
                        }
                    });

        }
    }

}
