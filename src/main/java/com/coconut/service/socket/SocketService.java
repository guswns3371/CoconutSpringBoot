package com.coconut.service.socket;

import com.coconut.client.dto.ChatMessageSocketDto;
import com.coconut.client.dto.ChatRoomSocketDto;
import com.coconut.client.dto.res.ChatHistorySaveResDto;
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
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatHistoryRepository chatHistoryRepository;
    private final UserChatRoomRepository userChatRoomRepository;

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

        Optional<ArrayList<ChatHistory>> optionalChatHistoryList = chatHistoryRepository.findChatHistoriesByChatRoom_Id(Long.parseLong(chatRoomId));
        Optional<UserChatRoom> optionalUserChatRoom = userChatRoomRepository.findUserChatRoomByChatRoom_IdAndUser_Id(Long.parseLong(chatRoomId), Long.parseLong(userId));

        // 채팅 기록 읽은 사람들 업데이트
        if (optionalChatHistoryList.isPresent()) {
            List<ChatHistory> chatHistoryList = optionalChatHistoryList.get();
            chatHistoryList.parallelStream().forEach(history -> {
                history.updateReadMembers(userId);
            });
        }

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
        ArrayList<String> chatRoomMembers = dto.getChatRoomMembers();
        ArrayList<String> readMembers = dto.getReadMembers();

        Optional<UserChatRoom> optionalUserChatRoom = userChatRoomRepository.findUserChatRoomByChatRoom_IdAndUser_Id(Long.parseLong(chatRoomId), Long.parseLong(userId));
        if (!optionalUserChatRoom.isPresent())
            return;

        Collections.sort(readMembers);
        UserChatRoom userChatRoom = optionalUserChatRoom.get();
        User user = userChatRoom.getUser();
        ChatRoom chatRoom = userChatRoom.getChatRoom();
        chatRoom.updateLastMessage(chatMessage);

        ChatHistory chatHistory = ChatHistory.builder()
                .user(user)
                .chatRoom(chatRoom)
                .history(chatMessage)
                .messageType(MessageType.TEXT)
                .readMembers(readMembers.toString())
                .build();

        ChatHistorySaveResDto resDto = chatHistoryRepository.save(chatHistory).toChatHistorySaveResDto();
        messageSender.convertAndSend("/sub/chat/message/" + dto.getChatRoomId(), resDto);


        ArrayList<String> unReadMembers = chatRoomMembers.stream()
                .filter(it -> !readMembers.contains(it))
                .collect(Collectors.toCollection(ArrayList::new));

        System.out.println("unReadMembers=" + unReadMembers);

        for (String unReadMemberId : unReadMembers) {
            System.out.println("/sub/chat/frag/" + unReadMemberId);
            Optional<ArrayList<ChatHistory>> optionalChatHistories = chatHistoryRepository.findChatHistoriesByChatRoom_IdAndAndUser_Id(Long.parseLong(chatRoomId), Long.parseLong(unReadMemberId));
            Optional<UserChatRoom> chatRoomOptional = userChatRoomRepository.findUserChatRoomByChatRoom_IdAndUser_Id(Long.parseLong(chatRoomId), Long.parseLong(unReadMemberId));

            if (optionalChatHistories.isPresent() && chatRoomOptional.isPresent()) {
                ArrayList<ChatHistory> chatHistories = optionalChatHistories.get();
                AtomicInteger unReadNum = new AtomicInteger();

                chatHistories.forEach(it -> {
                    ArrayList<String> memberList = new GsonBuilder().create().fromJson(it.getReadMembers(), new TypeToken<ArrayList<String>>() {
                    }.getType());
                    System.out.println(memberList);
                    System.out.println(unReadMemberId);
                    if (!memberList.contains(unReadMemberId))
                        unReadNum.getAndIncrement();

                });
                System.out.println(unReadNum.get());

//                unReadNum.set((int) chatHistories.stream()
//                        .<ArrayList<String>>map(it -> new GsonBuilder().create().fromJson(it.getReadMembers(), new TypeToken<ArrayList<String>>() {
//                        }.getType()))
//                        .filter(readMemberList -> !readMemberList.contains(unReadMemberId))
//                        .count());

                UserChatRoom room = chatRoomOptional.get();
                room.updateUnReads(unReadNum.get());
            }

            messageSender.convertAndSend("/sub/chat/frag/" + unReadMemberId, " ");
        }
    }

}
