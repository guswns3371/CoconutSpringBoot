package com.coconut.service;

import com.coconut.api.dto.ChatMessageSocketDto;
import com.coconut.api.dto.ChatRoomSocketDto;
import com.coconut.api.dto.res.ChatHistorySaveResDto;
import com.coconut.config.fcm.FirebaseCloudMessageService;
import com.coconut.domain.chat.*;
import com.coconut.domain.user.User;
import com.coconut.repository.ChatHistoryRepository;
import com.coconut.repository.UserChatHistoryRepository;
import com.coconut.repository.UserChatRoomRepository;
import com.coconut.repository.UserRepository;
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

    public static List<String> connectedUserList = Collections.synchronizedList(new ArrayList<>());
    public static Map<String, ArrayList<String>> enteredUserMap = Collections.synchronizedMap(new HashMap<>());

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

    // Isolation.SERIALIZABLE : ??????????????? ????????? ????????? SELECT ????????? ???????????? ?????? ???????????? shared lock ??? ?????????
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void enterUsers(ChatRoomSocketDto dto) {
        String chatRoomId = dto.getChatRoomId();
        String userId = dto.getChatUserId();
        ArrayList<String> users = addUser(chatRoomId, userId);

        log.warn("??????=" + userId + " : " + chatRoomId + "??? ????????? enter =" + enteredUserMap);

        Optional<User> optionalUser = userRepository.findUserById(Long.parseLong(userId));
        Optional<ArrayList<ChatHistory>> optionalChatHistoryList = chatHistoryRepository.findChatHistoriesByChatRoom_Id(Long.parseLong(chatRoomId));

        // ?????? ?????? ?????? ????????? ????????????
        if (optionalChatHistoryList.isPresent() && optionalUser.isPresent()) {
            List<ChatHistory> chatHistoryList = optionalChatHistoryList.get();
            User user = optionalUser.get();

            chatHistoryList.forEach(history -> {
                boolean isExist = userChatHistoryRepository.existsUserChatHistoryByChatHistoryAndUser(history, user);
                if (!isExist && !history.getMessageType().equals(MessageType.INFO)) {
                    // ????????? ?????? ??????
                    userChatHistoryRepository.save(UserChatHistory.builder()
                            .chatHistory(history)
                            .user(user)
                            .build());

                    // ?????? ????????? ??????
                    history.updateReadCount();
                }
            });
        }

        Optional<UserChatRoom> optionalUserChatRoom =
                userChatRoomRepository.findUserChatRoomByChatRoom_IdAndUser_Id(Long.parseLong(chatRoomId), Long.parseLong(userId));

        // ????????? ????????? 0?????? ??????
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

        log.warn("??????=" + userId + " : " + chatRoomId + "??? ????????? exit =" + enteredUserMap);

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
//        ArrayList<String> readMembers = dto.getReadMembers();
        ArrayList<String> readMembers = enteredUserMap.get(chatRoomId);
        ArrayList<String> chatImages = dto.getChatImages();

        MessageType messageType;
        String stringChatImages;
        String chatHistory;

        if (dto.getMessageType().equals(MessageType.IMAGE.getKey())) {
            chatMessage = "????????? ???????????????.";
            messageType = MessageType.IMAGE;
            stringChatImages = chatImages.toString();
        } else {
            messageType = MessageType.TEXT;
            stringChatImages = null;
        }

        chatHistory = chatMessage;

        Optional<UserChatRoom> optionalUserChatRoom =
                userChatRoomRepository.findUserChatRoomByChatRoom_IdAndUser_Id(Long.parseLong(chatRoomId), Long.parseLong(userId));
        if (optionalUserChatRoom.isEmpty()) {
            throw new IllegalStateException("???????????? ?????? UserChatRoom");
        }


        UserChatRoom socketUserChatRoom = optionalUserChatRoom.get();
        // modifiedDate ???????????? (????????? ?????? ????????? ????????? ?????? ?????? ??????????????? ??????)
        socketUserChatRoom.onModifiedDateUpdate();


        ChatRoom socketChatRoom = socketUserChatRoom.getChatRoom();
        // ????????? ????????? ????????????
        socketChatRoom.updateLastMessage(chatMessage);

        // ?????? ???????????? ?????????
        ArrayList<User> users = socketChatRoom.getUsers();
        // ?????? ?????? ????????? ????????? ??????
        if (users.size() == 2)
            socketUserChatRoom.enableChatRoom();

        // ???????????? ?????? ??????
        User socketUser = users.stream()
                .filter(it -> it.getId().equals(Long.parseLong(userId)))
                .collect(Collectors.toList()).get(0);

        // ?????? ?????? ??????
        ChatHistory savedHistory = chatHistoryRepository.save(ChatHistory.builder()
                .user(socketUser)
                .chatRoom(socketChatRoom)
                .history(chatHistory)
                .chatImages(stringChatImages)
                .messageType(messageType)
                .build());


        ArrayList<User> ReadMembers = users.stream()
                .filter(it -> readMembers.contains(it.getId().toString()))
                .collect(Collectors.toCollection(ArrayList::new));

        // ????????? ?????? ??????
        ReadMembers.forEach(readUser -> {
            boolean isExist = userChatHistoryRepository.existsUserChatHistoryByChatHistoryAndUser(savedHistory, readUser);
            if (!isExist && !savedHistory.getMessageType().equals(MessageType.INFO)) {
                userChatHistoryRepository.save(UserChatHistory.builder()
                        .chatHistory(savedHistory)
                        .user(readUser)
                        .build());
            }
        });

        // ?????? ????????? ??????
        savedHistory.updateReadCount();

        // ????????? ?????????
        ChatHistorySaveResDto resDto = savedHistory.toChatHistorySaveResDto();
        messageSender.convertAndSend("/sub/chat/message/" + dto.getChatRoomId(), resDto);

        ArrayList<User> unReadMembers = users.stream()
                .filter(it -> !readMembers.contains(it.getId().toString()))
                .collect(Collectors.toCollection(ArrayList::new));

        // 2??? ???????????? ???????????? ?????? ???????????? ????????? ??????
        if (unReadMembers.size() == 1)
            unReadMembers.get(0).getUserChatRoom(socketChatRoom.getId().toString()).enableChatRoom();

        unReadMembers.forEach(unReadMember -> {
            // fcm ??????
            String fcmToken = unReadMember.getFcmToken();

            UserChatRoom unReadMemberUserChatRoom =
                    unReadMember.getUserChatRoom(socketChatRoom.getId().toString());
            // ????????? ??????
            String unReadMemberChatRoomName =
                    unReadMemberUserChatRoom.getCurrentChatRoomName();
            // ????????? ????????? ??????
            unReadMemberUserChatRoom.addUnReads();

            new Thread(() -> {
                // fcm ??????
                try {
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
                } finally {
                    // ????????? ?????? ????????????
                    messageSender.convertAndSend(
                            "/sub/chat/frag/" + unReadMember.getId(),
                            "????????? ?????????=" + socketChatRoom.getLastMessage());
                }
            }).start();

        });

    }
}
