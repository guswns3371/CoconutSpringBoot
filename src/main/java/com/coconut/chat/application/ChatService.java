package com.coconut.chat.application;

import com.coconut.chat.presentation.dto.ChatHistoryResDto;
import com.coconut.chat.presentation.dto.ChatHistorySaveResDto;
import com.coconut.chat.presentation.dto.ChatRoomDataResDto;
import com.coconut.auth.presentation.dto.UserDataResDto;
import com.coconut.chat.domain.constant.AbleType;
import com.coconut.chat.domain.constant.MessageType;
import com.coconut.chat.domain.constant.RoomType;
import com.coconut.chat.domain.entity.ChatHistory;
import com.coconut.chat.domain.entity.ChatRoom;
import com.coconut.chat.domain.entity.UserChatRoom;
import com.coconut.chat.domain.repository.ChatHistoryRepository;
import com.coconut.chat.domain.repository.ChatRoomRepository;
import com.coconut.chat.domain.repository.UserChatHistoryRepository;
import com.coconut.chat.domain.repository.UserChatRoomRepository;
import com.coconut.chat.presentation.dto.*;
import com.coconut.user.application.UserService;
import com.coconut.user.domain.entity.User;
import com.coconut.user.domain.repository.UserRepository;
import com.coconut.base.utils.file.FilesStorageService;
import com.coconut.base.utils.file.PathNameBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
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

    private final UserService userService;
    private final ChatRoomService chatRoomService;
    private final ChatHistoryService chatHistoryService;
    private final UserChatRoomService userChatRoomService;
    private final UserChatHistoryService userChatHistoryService;

    @Transactional
    public ChatRoomDataResDto makeChatRoom(ChatRoomSaveReqDto reqDto) {
        Long userId = reqDto.getChatUserId();
        ArrayList<String> members = reqDto.getChatRoomMembers();

        // 유저 목록 가져오기
        ArrayList<User> users = userService.findUsersByIds(reqDto.getMemberLongIds()).orElseThrow(
                () -> new IllegalStateException("존재하지 않는 유저입니다.")
        );
        // 채팅방 이름
        String roomName = (users.size() == 1) ? users.get(0).getName() : getChatRoomName(users, userId);
        // 채팅방이 이미 존재하는 경우
        Optional<ChatRoom> optionalChatRoom = chatRoomService.findByMembers(members.toString());
        if (optionalChatRoom.isPresent()) {

            ChatRoom chatRoom = optionalChatRoom.get();

            // 나갔던 채팅방일 경우, UserChatRoom 을 다시 생성한다.
            userChatRoomService.saveAll(reqDto.getMemberLongIds(), chatRoom);
            return ChatRoomDataResDto.builder()
                    .chatRoomId(chatRoom.getId().toString())
                    .chatRoomName(roomName)
                    .chatRoomMembers(members)
                    .chatRoomMembersInfo(getUserData(users))
                    .build();
        }
        // 채팅방 종류
        RoomType roomType = (members.size() == 1 && members.get(0).equals(userId.toString())) ? RoomType.ME : RoomType.GROUP;
        // 채팅방 생성
        ChatRoom chatRoom = chatRoomService.save(ChatRoom.builder()
                .roomType(roomType)
                .members(members.toString())
                .build());

        // 유저마다 개별 UserChatRoom 생성 : saveAll
        userChatRoomService.saveAll(reqDto.getMemberLongIds(), chatRoom);

        return ChatRoomDataResDto.builder()
                .chatRoomId(chatRoom.getId().toString())
                .chatRoomName(roomName)
                .chatRoomMembers(members)
                .chatRoomMembersInfo(getUserData(users))
                .build();
    }

    public ArrayList<ChatRoomListReqDto> getChatRoomLists(Long userId) {
        ArrayList<ChatRoomListReqDto> chatRoomListReqDtoList = new ArrayList<>();
        ArrayList<UserDataResDto> userDataResDtoList;
        UserChatRoomInfoReqDto userChatRoomInfoReqDto;

        // UserChatRoom 목록 가져오기
        ArrayList<UserChatRoom> userChatRooms = userChatRoomService.findAllByUserId(userId);
        if (userChatRooms.isEmpty()) {
            return null;
        }

        // UserChatRoom 리스트
        for (UserChatRoom userChatRoom : userChatRooms) {
            // DISABLE 인 채팅방은 목록에 담지 않는다.
            if (userChatRoom.getAbleType().equals(AbleType.DISABLE)) {
                continue;
            }
            ChatRoom chatRoom = userChatRoom.getChatRoom();
            // LastMessage 가 없는 채팅방은 목록에 담지 않는다.
            if (!StringUtils.hasText(chatRoom.getLastMessage())) {
                continue;
            }
            // 채팅방속 유저 목록
            ArrayList<User> users = chatRoomService.findUsersByChatRoomId(chatRoom.getId());
            // 유저가 없는 채팅방은 목록에 담지 않는다.
            if (users.isEmpty()) {
                continue;
            }
            // UserChatRoom 정보
            userChatRoomInfoReqDto = new UserChatRoomInfoReqDto(userChatRoom);
            // 사용자 정보
            userDataResDtoList = getUserData(users);
            if (chatRoom.getRoomType().equals(RoomType.GROUP)) {
                // 그룹 채팅방인 경우 본인의 정보를 삭제한다.
                userDataResDtoList.removeIf(it -> it.getId().equals(userId));
            }
            // ChatRoomListReqDto 정보 추가
            chatRoomListReqDtoList.add(ChatRoomListReqDto.builder()
                    .userChatRoomInfoReqDto(userChatRoomInfoReqDto)
                    .userInfos(userDataResDtoList)
                    .build());
        }

        if (chatRoomListReqDtoList.isEmpty()) {
            return null;
        }

        return chatRoomListReqDtoList;
    }

    public ChatRoomDataResDto getChatRoomData(Long roomId, Long userId) {
        UserChatRoom userChatRoom = userChatRoomService.findByUserIdAndChatRoomId(userId, roomId).orElseThrow(
                () -> new IllegalStateException("존재하지 않는 UserChatRoom")
        );
        Optional<ArrayList<User>> optionalUsers = userService.findUsersByIds(userChatRoom.getChatRoom().getUserIds());

        if (optionalUsers.isEmpty()) {
            return null;
        }

        ArrayList<User> users = optionalUsers.get();

        // 자신의 프로필을 맨 앞으로
        User user = users.stream()
                .filter(it -> it.getId().equals(userId))
                .collect(Collectors.toList()).get(0);
        users.remove(user);
        users.add(0, user);

        return ChatRoomDataResDto.builder()
                .chatRoomId(roomId.toString())
                .chatRoomName(userChatRoom.getCurrentChatRoomName())
                .chatRoomMembers(new ArrayList<>(userChatRoom.getChatRoom().getChatMembers()))
                .chatRoomMembersInfo(users.stream()
                        .map(UserDataResDto::new)
                        .collect(Collectors.toCollection(ArrayList::new)))
                .build();
    }

    public ArrayList<ChatHistoryResDto> getChatHistory(Long id) {
        Optional<ArrayList<ChatHistory>> optionalChatHistories = chatHistoryService.findAllMessages(id);
        if (optionalChatHistories.isEmpty()) {
            return null;
        }

        return optionalChatHistories.get().stream()
                .map(ChatHistoryResDto::new)
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
        String roomName = (StringUtils.isEmpty(chatRoomName)) ? null : chatRoomName;
        userChatRoom.updateChatRoomName(roomName);
        return true;
    }

    @Transactional
    public ChatRoomDataResDto inviteUser(ChatRoomDataReqDto reqDto) {
        Long userId = reqDto.getChatUserId();
        Long chatRoomId = reqDto.getChatRoomId();
        ArrayList<String> chatRoomMembers = reqDto.getChatRoomMembers();

        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(chatRoomId);
        if (optionalChatRoom.isEmpty())
            return null;

        // members 에 추가하기
        ChatRoom chatRoom = optionalChatRoom.get();
        chatRoom.addMembers(chatRoomMembers);

        Optional<ArrayList<User>> optionalUserArrayList = userRepository.findUserByIdIn(chatRoom.getUserIds());
        if (optionalUserArrayList.isEmpty())
            return null;

        ArrayList<User> users = optionalUserArrayList.get();

        // 초대한 유저
        User hostUser = users.stream()
                .filter(it -> it.getId().equals(userId))
                .collect(Collectors.toList()).get(0);

        // 초대 받은 유저들
        ArrayList<User> guestUsers = users.stream()
                .filter(it -> chatRoomMembers.contains(it.getId().toString()))
                .collect(Collectors.toCollection(ArrayList::new));

        // 초대 받은 유저들의 UserChatRoom 을 만든다
        guestUsers.forEach(user -> {
            userChatRoomRepository.save(
                    UserChatRoom.builder()
                            .chatRoom(chatRoom)
                            .user(user)
                            .build());
        });

        // 채팅 기록 저장
        String guestNames = getChatRoomName(guestUsers, userId);
        ChatHistory savedHistory = chatHistoryRepository.save(ChatHistory.builder()
                .user(hostUser)
                .chatRoom(chatRoom)
                .history("'" + hostUser.getName() + "'님이 '" + guestNames + "' 님을 초대하였습니다.")
                .messageType(MessageType.INFO)
                .build());

        // 채팅방에 기록 남기기
        ChatHistorySaveResDto resDto = savedHistory.toChatHistorySaveResDto();
        messageSender.convertAndSend("/sub/chat/message/" + chatRoom.getId(), resDto);

        // 기존 유저들에게 알림
        users.stream().parallel()
                .filter(user -> !guestUsers.contains(user))
                .map(User::getId)
                .forEach(id -> {
                    messageSender.convertAndSend("/sub/chat/frag/" + id, "유저=" + id + " 초대됨");
                });

        String myRoomName = getChatRoomName(users, userId);
        ArrayList<String> members = new ArrayList<>(chatRoom.getChatMembers());
        ArrayList<UserDataResDto> membersInfo = users.stream()
                .map(UserDataResDto::new)
                .collect(Collectors.toCollection(ArrayList::new));

        return ChatRoomDataResDto.builder()
                .chatRoomId(chatRoomId.toString())
                .chatRoomName(myRoomName)
                .chatRoomMembers(members)
                .chatRoomMembersInfo(membersInfo)
                .build();
    }

    @Transactional
    public boolean exitChatRoom(ChatRoomExitReqDto reqDto) {
        Long chatRoomId = reqDto.getChatRoomId();
        Long userId = reqDto.getUserId();

        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(chatRoomId);
        if (optionalChatRoom.isEmpty()){
            return false;
        }

        Optional<UserChatRoom> optionalUserChatRoom = userChatRoomRepository.findUserChatRoomByChatRoom_IdAndUser_Id(chatRoomId, userId);
        if (optionalUserChatRoom.isEmpty()) {
            return false;
        }

        ChatRoom chatRoom = optionalChatRoom.get();
        UserChatRoom userChatRoom = optionalUserChatRoom.get();

        int userSize = chatRoom.getUserIds().size();
        if (userSize > 2) {
            // 총 인원이 2명 이상인 채팅방을 나갈 때
            // 나갈 유저
            User exitUser = chatRoom.getUsers().stream()
                    .filter(it -> it.getId().equals(userId))
                    .collect(Collectors.toList()).get(0);

            // 채팅방 멤버에서 삭제
            chatRoom.exitRoom(userId);

            // 매핑관계 끊기
            userChatRoom.remove();
            userChatRoomRepository.delete(userChatRoom);

            // 채팅 기록 저장
            ChatHistory savedHistory = chatHistoryRepository.save(ChatHistory.builder()
                    .user(exitUser)
                    .chatRoom(chatRoom)
                    .history("'" + exitUser.getName() + "' 님이 채팅방을 나갔습니다.")
                    .messageType(MessageType.INFO)
                    .build());

            // 채팅방에 알리기
            ChatHistorySaveResDto resDto = savedHistory.toChatHistorySaveResDto();
            messageSender.convertAndSend("/sub/chat/message/" + chatRoom.getId(), resDto);

            // 채팅방에 없는 사람들에게 알리기
            chatRoom.getUsers().stream()
                    .map(User::getId)
                    .forEach(userId1 -> {
                        new Thread(() -> {
                            messageSender.convertAndSend("/sub/chat/frag/" + userId1, "유저=" + userId + " 나감");
                        }).start();
                    });

        } else if (userSize == 2) {
            // 총 인원이 2명인 채팅방을 나갈 때, UserChatRoom 을 숨긴다
            userChatRoom.disableChatRoom();
        } else if (userSize == 1) {
            // 총 인원이 1명인 채팅방을 나갈 때 (나와의 채팅)
            // 매핑관계 끊기
            userChatRoom.remove();
            userChatRoomRepository.delete(userChatRoom);

            // 모든 채팅 기록 삭제
            userChatRoom.removeHistory();
            chatHistoryRepository.deleteAllByChatRoomIdAndUserId(chatRoomId, userId);
        }

        return true;
    }

    private String getChatRoomName(ArrayList<User> users, Long userId) {
        return users.stream()
                .filter(user -> !user.getId().equals(userId))
                .map(User::getName)
                .collect(Collectors.joining(", "));
    }

    private ArrayList<UserDataResDto> getUserData(ArrayList<User> users) {
        return users.stream()
                .map(UserDataResDto::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
