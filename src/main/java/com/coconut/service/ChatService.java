package com.coconut.service;

import com.coconut.api.dto.req.*;
import com.coconut.api.dto.res.ChatHistoryResDto;
import com.coconut.api.dto.res.ChatHistorySaveResDto;
import com.coconut.api.dto.res.ChatRoomDataResDto;
import com.coconut.api.dto.res.UserDataResDto;
import com.coconut.domain.chat.*;
import com.coconut.domain.user.User;
import com.coconut.domain.user.UserRepository;
import com.coconut.service.utils.file.FilesStorageService;
import com.coconut.service.utils.file.PathNameBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ChatService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;
    private final ChatHistoryRepository chatHistoryRepository;
    private final FilesStorageService storageService;
    private final UserChatHistoryRepository userChatHistoryRepository;
    private final SimpMessageSendingOperations messageSender;


    @Transactional
    public ChatRoomDataResDto getChatRoomData(ChatRoomDataReqDto chatRoomDataReqDto) {
        log.warn(chatRoomDataReqDto.toString());

        String myRoomName;
        ArrayList<String> members;
        String userId = chatRoomDataReqDto.getChatUserId();
        String chatRoomId = chatRoomDataReqDto.getChatRoomId();
        Optional<UserChatRoom> optionalUserChatRoom = userChatRoomRepository.findUserChatRoomByChatRoom_IdAndUser_Id(Long.parseLong(chatRoomId), Long.parseLong(userId));

        if (optionalUserChatRoom.isEmpty())
            return null;

        UserChatRoom userChatRoom = optionalUserChatRoom.get();
        myRoomName = userChatRoom.getChatRoomName();

        ChatRoom chatRoom = userChatRoom.getChatRoom();
        List<Long> memberIds = chatRoom.getLongChatMembers();
        Optional<ArrayList<User>> optionalUserArrayList = userRepository.findUserByIdIn(memberIds);
        members = memberIds.stream().map(Object::toString).collect(Collectors.toCollection(ArrayList::new));

        if (optionalUserArrayList.isEmpty())
            return null;

        ArrayList<UserDataResDto> membersInfo = optionalUserArrayList.get()
                .stream()
                .map(UserDataResDto::new)
                .collect(Collectors.toCollection(ArrayList::new));

        return ChatRoomDataResDto.builder()
                .chatRoomId(chatRoomId)
                .chatRoomName(myRoomName)
                .chatRoomMembers(members)
                .chatRoomMembersInfo(membersInfo)
                .build();
    }

    @Transactional
    public ArrayList<ChatHistoryResDto> getChatHistory(String chatRoomId) {
        Optional<ArrayList<ChatHistory>> optionalChatHistory = chatHistoryRepository.findChatHistoriesByChatRoom_Id(Long.parseLong(chatRoomId));

        if (optionalChatHistory.isEmpty())
            return null;

        ArrayList<ChatHistory> chatHistories = optionalChatHistory.get();
        chatHistories.forEach(chatHistory -> chatHistory.updateReadMembers(Integer.toString(chatHistory.getUserChatHistoryList().size())));

        return chatHistories.stream()
                .map(ChatHistory::toChatHistoryResDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Transactional
    public ArrayList<String> uploadChatImages(ChatUploadImageReqDto reqDto) {
        log.warn(reqDto.toString());

        String userId = reqDto.getUserId();
        String chatRoomId = reqDto.getChatRoomId();
        ArrayList<String> imagePathList = new ArrayList<>();

        try {
            Arrays.stream(reqDto.getImages()).forEach(file -> {
                String imagePath = PathNameBuilder.builder()
                        .userIndex(userId)
                        .chatRoomIndex(chatRoomId)
                        .fileOriginalName(file.getOriginalFilename())
                        .build()
                        .getChatImagePath()
                        .replaceAll(" ", "");

                storageService.save(file, imagePath);
                imagePathList.add(imagePath);
            });

            return imagePathList;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Transactional
    public boolean changeChatRoomName(ChatRoomNameChangeReqDto reqDto) {
        String chatRoomName = reqDto.getChatRoomName();
        String chatRoomId = reqDto.getChatRoomId();
        String userId = reqDto.getUserId();

        Optional<UserChatRoom> optionalUserChatRoom = userChatRoomRepository.findUserChatRoomByChatRoom_IdAndUser_Id(
                Long.parseLong(chatRoomId), Long.parseLong(userId)
        );

        if (optionalUserChatRoom.isEmpty())
            return false;

        UserChatRoom userChatRoom = optionalUserChatRoom.get();

        String roomName = (chatRoomName.equals("")) ? null : chatRoomName;

        userChatRoom.updateChatRoomName(roomName);
        return true;
    }

    @Transactional
    public boolean exitChatRoom(ChatRoomExitReqDto reqDto) {
        String chatRoomId = reqDto.getChatRoomId();
        String userId = reqDto.getUserId();

        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(Long.parseLong(chatRoomId));

        if (optionalChatRoom.isEmpty())
            return false;
        ChatRoom chatRoom = optionalChatRoom.get();

        User exitUser = chatRoom.getUsers().stream()
                .filter(it -> it.getId().equals(Long.parseLong(userId)))
                .collect(Collectors.toList()).get(0);

        // UserChatRoom 삭제
        Optional<UserChatRoom> optionalUserChatRoom = userChatRoomRepository.findUserChatRoomByChatRoom_IdAndUser_Id(
                Long.parseLong(chatRoomId), Long.parseLong(userId));

        if (optionalUserChatRoom.isPresent()) {
            UserChatRoom userChatRoom = optionalUserChatRoom.get();

            // 2명만 있는 채팅방의 경우, UserChatRoom을 숨긴다
            if (chatRoom.getLongChatMembers().size() == 2) {
                userChatRoom.disableChatRoom();
                return true;
            }

            // 1대다 단방향 매핑 관계에 있다면 -> join column ArrayList 에 존재하는 관계도 제거해야한다.
            userChatRoom.getUser().getUserChatRoomList().removeIf(it -> it.getChatRoom().getId().equals(Long.parseLong(chatRoomId)));
            userChatRoom.getChatRoom().getUserChatRoomList().removeIf(it -> it.getUser().getId().equals(Long.parseLong(userId)));
            userChatRoomRepository.delete(userChatRoom);

            if (chatRoom.getLongChatMembers().size() > 2) {

                // 채팅방 멤버에서 삭제
                chatRoom.exitChatRoom(userId);

                // 채팅 기록 저장
                ChatHistory savedHistory = chatHistoryRepository.save(ChatHistory.builder()
                        .user(exitUser)
                        .chatRoom(chatRoom)
                        .history("'" + exitUser.getName() + "' 님이 채팅방을 나갔습니다.")
                        .messageType(MessageType.INFO)
                        .build());

                ChatHistorySaveResDto resDto = savedHistory.toChatHistorySaveResDto();
                // 채팅방에 기록 남기기
                messageSender.convertAndSend("/sub/chat/message/" + chatRoom.getId(), resDto);

                // 채팅방에 없는 사람들에게 알리기
                chatRoom.getUsers().stream()
                        .map(User::getId)
                        .forEach(userId1 -> {
                            new Thread(() -> {
                                messageSender.convertAndSend("/sub/chat/frag/" + userId1, "유저=" + userId + " 나감");
                            }).start();
                        });

            } else if (chatRoom.getLongChatMembers().size() == 1) {

                // 총 인원이 1명인 채팅방을 나갈 때, 모든 채팅 기록 삭제
                userChatRoom.getUser().getChatHistoryList().removeIf(it -> it.getChatRoom().getId().equals(Long.parseLong(chatRoomId)));
                userChatRoom.getChatRoom().getChatHistoryList().removeIf(it -> it.getUser().getId().equals(Long.parseLong(userId)));
                chatHistoryRepository.deleteChatHistoriesByChatRoom_IdAndUser_Id(
                        Long.parseLong(chatRoomId), Long.parseLong(userId)
                );
            }
        }


        return true;
    }

    @Transactional
    public ChatRoomDataResDto inviteUser(ChatRoomDataReqDto reqDto) {
        String userId = reqDto.getChatUserId();
        String chatRoomId = reqDto.getChatRoomId();
        ArrayList<String> members;
        ArrayList<UserDataResDto> membersInfo;
        String myRoomName;

        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(Long.parseLong(chatRoomId));

        if (optionalChatRoom.isEmpty())
            return null;

        ChatRoom chatRoom = optionalChatRoom.get();

        // members 에 추가하기
        chatRoom.inviteMembers(reqDto.getChatRoomMembers());
        members = new ArrayList<>(chatRoom.getStringChatMembers());

        Optional<ArrayList<User>> optionalUserArrayList = userRepository.findUserByIdIn(chatRoom.getLongChatMembers());

        if (optionalUserArrayList.isEmpty())
            return null;

        ArrayList<User> users = optionalUserArrayList.get();

        // 초대한 유저
        User hostUser = users.stream()
                .filter(it -> it.getId().equals(Long.parseLong(userId)))
                .collect(Collectors.toList()).get(0);

        // 초대 받은 유저들
        ArrayList<User> guestUsers = users.stream()
                .filter(it -> reqDto.getChatRoomMembers().contains(it.getId().toString()))
                .filter(it -> !it.getId().equals(Long.parseLong(userId)))
                .collect(Collectors.toCollection(ArrayList::new));

        String guestNames = guestUsers.stream()
                .map(User::getName)
                .collect(Collectors.joining(", "));

        // 채팅 기록 저장
        ChatHistory savedHistory = chatHistoryRepository.save(ChatHistory.builder()
                .user(hostUser)
                .chatRoom(chatRoom)
                .history("'" + hostUser.getName() + "'님이 '" + guestNames + "' 님을 초대하였습니다.")
                .messageType(MessageType.INFO)
                .build());

        ChatHistorySaveResDto resDto = savedHistory.toChatHistorySaveResDto();

        // 채팅방에 기록 남기기
        messageSender.convertAndSend("/sub/chat/message/" + chatRoom.getId(), resDto);

        // 초대한 유저의 UserChatRoom 을 만든다
        guestUsers.forEach(user -> {
            userChatRoomRepository.save(
                    UserChatRoom.builder()
                            .chatRoom(chatRoom)
                            .user(user)
                            .build());
        });

        myRoomName = users.stream()
                .map(User::getName)
                .collect(Collectors.joining(", "));

        membersInfo = users.stream()
                .map(UserDataResDto::new)
                .collect(Collectors.toCollection(ArrayList::new));

        // 초대 알림
        users.removeAll(guestUsers);
        users.stream().parallel()
                .map(User::getId)
                .forEach(id -> {
                    messageSender.convertAndSend("/sub/chat/frag/" + id, "유저=" + id + " 초대됨");
                });


        return ChatRoomDataResDto.builder()
                .chatRoomId(chatRoomId)
                .chatRoomName(myRoomName)
                .chatRoomMembers(members)
                .chatRoomMembersInfo(membersInfo)
                .build();
    }
}
