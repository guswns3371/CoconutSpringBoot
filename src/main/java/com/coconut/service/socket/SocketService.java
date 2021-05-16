package com.coconut.service.socket;

import com.coconut.client.dto.ChatMessageSocketDto;
import com.coconut.client.dto.ChatRoomSocketDto;
import com.coconut.client.dto.res.ChatHistorySaveResDto;
import com.coconut.config.fcm.FirebaseCloudMessageService;
import com.coconut.domain.chat.*;
import com.coconut.domain.user.User;
import com.coconut.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocketService {

    private static List<String> connectedUserList = Collections.synchronizedList(new ArrayList<>());
    private static final Map<String, ArrayList<String>> enteredUserMap = Collections.synchronizedMap(new HashMap<>());
    private final SimpMessageSendingOperations messageSender;
    private final FirebaseCloudMessageService firebaseCloudMessageService;
    private final UserRepository userRepository;
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

    // Isolation.SERIALIZABLE : 트랜잭션이 완료될 때까지 SELECT 문장이 사용하는 모든 데이터에 shared lock 이 걸린다
    @Transactional(isolation = Isolation.SERIALIZABLE)
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
                // hmm?
                boolean isExist = userChatHistoryRepository.existsUserChatHistoryByChatHistoryAndUser(history, user);
                if (!isExist && !history.getMessageType().equals(MessageType.INFO)) {
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
        ArrayList<String> chatImages = dto.getChatImages();

        MessageType messageType;
        String stringChatImages;
        String chatHistory;

        if (dto.getMessageType().equals(MessageType.IMAGE.getKey())) {
            chatMessage = "사진을 보냈습니다.";
            messageType = MessageType.IMAGE;
            stringChatImages = chatImages.toString();
        } else {
            messageType = MessageType.TEXT;
            stringChatImages = null;
        }

        chatHistory = chatMessage;

        Optional<UserChatRoom> optionalUserChatRoom = userChatRoomRepository.findUserChatRoomByChatRoom_IdAndUser_Id(Long.parseLong(chatRoomId), Long.parseLong(userId));
        if (optionalUserChatRoom.isEmpty())
            return;


        UserChatRoom socketUserChatRoom = optionalUserChatRoom.get();
        // modifiedDate 업데이트 (메시지 보낸 유저의 채팅방 목록 정렬 업데이트를 위함)
        socketUserChatRoom.onModifiedDateUpdate();

        ChatRoom socketChatRoom = socketUserChatRoom.getChatRoom();
        // 마지막 메시지 업데이트
        socketChatRoom.updateLastMessage(chatMessage);
        // 현재 채팅방속 유저들
        ArrayList<User> users = socketChatRoom.getUsers();

        // 메시지를 보낸 유저
        User socketUser = users.stream()
                .filter(it -> it.getId().equals(Long.parseLong(userId)))
                .collect(Collectors.toList()).get(0);

        // 채팅 기록 저장
        ChatHistory savedHistory = chatHistoryRepository.save(ChatHistory.builder()
                .user(socketUser)
                .chatRoom(socketChatRoom)
                .readMembers(Integer.toString(readMembers.size()))
                .history(chatHistory)
                .chatImages(stringChatImages)
                .messageType(messageType)
                .build());

        ArrayList<User> ReadMembers = users.stream()
                .filter(it -> readMembers.contains(it.getId().toString()))
                .collect(Collectors.toCollection(ArrayList::new));

        // 메시지 읽음 표시
        for (User readUser : ReadMembers) {
            boolean isExist = userChatHistoryRepository.existsUserChatHistoryByChatHistoryAndUser(savedHistory, readUser);
            if (!isExist && !savedHistory.getMessageType().equals(MessageType.INFO)) {
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

        int totalHistoryCount, readCount, unReadCount;
        String unReadMemberChatRoomName;
        // 메시지를 읽지 않은 유저들
        for (User unReadMember : unReadMembers) {

            UserChatRoom unReadMemberUserChatRoom = unReadMember.getUserChatRoom(socketChatRoom.getId().toString());
            // 현재 채팅방을 나간 유저에 한하여 UserChatRoom 을 다시 만든다.
            if (unReadMemberUserChatRoom == null) {
                unReadMemberUserChatRoom = userChatRoomRepository.save(UserChatRoom.builder()
                        .chatRoom(socketChatRoom)
                        .user(unReadMember)
                        .build());
            }

            totalHistoryCount = socketChatRoom.getChatHistoryList().size();
            readCount = unReadMember.getReadMessageCount(chatRoomId);
            unReadCount = totalHistoryCount - readCount;

            // 안읽은 메시지 수 업데이트
            unReadMemberUserChatRoom.updateUnReads(unReadCount);
            unReadMemberChatRoomName = unReadMemberUserChatRoom.getCurrentChatRoomName(unReadMember.getId().toString());

            // fcm 알림
            try {
                String fcmToken = unReadMember.getFcmToken();

                if (fcmToken != null) {
                    Map<String, String> data = new HashMap<>();
                    data.put("roomId", socketChatRoom.getId().toString());
                    data.put("roomPeople", roomMembers.toString());
                    data.put("userImage", socketUser.getProfilePicture());
                    data.put("userName", socketUser.getName());
                    data.put("chatRoomName", unReadMemberChatRoomName);
                    data.put("chatMessage", chatHistory);

                    firebaseCloudMessageService.sendMessageTo(fcmToken, data);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 채팅방 목록 업데이트
        // 안읽은 메시지 수 업데이트 & fcm 알림 후에 socket 으로 업데이트해야한다.
        unReadMembers.stream()
                .map(User::getId)
                .forEach(unReadMemberId ->
                        messageSender.convertAndSend(
                                "/sub/chat/frag/" + unReadMemberId,
                                "마지막 메시지=" + socketChatRoom.getLastMessage()));
    }

}
