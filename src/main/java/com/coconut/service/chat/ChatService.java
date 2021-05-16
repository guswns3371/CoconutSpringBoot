package com.coconut.service.chat;

import com.coconut.client.dto.req.*;
import com.coconut.client.dto.res.ChatHistoryResDto;
import com.coconut.client.dto.res.ChatHistorySaveResDto;
import com.coconut.client.dto.res.ChatRoomDataResDto;
import com.coconut.client.dto.res.UserDataResDto;
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
public class ChatService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;
    private final ChatHistoryRepository chatHistoryRepository;
    private final FilesStorageService storageService;
    private final UserChatHistoryRepository userChatHistoryRepository;
    private final SimpMessageSendingOperations messageSender;

    @Transactional
    public ChatRoomDataResDto makeChatRoom(ChatRoomSaveReqDto chatRoomSaveReqDto) {
        String myRoomName = null;
        String chatRoomId;
        String userId = chatRoomSaveReqDto.getChatUserId();
        ArrayList<String> members = chatRoomSaveReqDto.getDistinctChatRoomMembers();

        ArrayList<UserDataResDto> membersInfo;
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findChatRoomByMembers(members.toString());

        // 채팅방이 이미 만들어져 있는 경우
        if (optionalChatRoom.isPresent()) {
            log.warn("이미 존재하는 채팅방");
            ChatRoom chatRoom = optionalChatRoom.get();

            chatRoomId = chatRoom.getId().toString();

            UserChatRoom userChatRoom = chatRoom.getUserChatRoom(userId);

            if (userChatRoom == null) {
                Optional<User> user = userRepository.findUserById(Long.parseLong(userId));
                userChatRoom = userChatRoomRepository.save(UserChatRoom.builder()
                        .user(user.get())
                        .chatRoom(chatRoom)
                        .build());
            }
            myRoomName = userChatRoom.getCurrentChatRoomName(userId);

            membersInfo = chatRoom.getUsers().stream()
                    .map(User::toUserDataResDto)
                    .collect(Collectors.toCollection(ArrayList::new));

            return ChatRoomDataResDto.builder()
                    .chatRoomId(chatRoomId)
                    .chatRoomName(myRoomName)
                    .chatRoomMembers(members)
                    .chatRoomMembersInfo(membersInfo)
                    .build();
        }

        // 채팅방 생성
        log.warn("채팅방 만들기");
        RoomType roomType = (members.size() == 1 && members.get(0).equals(userId)) ? RoomType.ME : RoomType.GROUP;
        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder()
                .roomType(roomType)
                .members(members.toString())
                .build());

        chatRoomId = chatRoom.getId().toString();

        List<Long> membersLong = members.stream().map(Long::parseLong).collect(Collectors.toList());
        Optional<ArrayList<User>> optionalUserArrayList = userRepository.findUserByIdIn(membersLong);

        if (optionalUserArrayList.isEmpty())
            return null;

        ArrayList<User> users = optionalUserArrayList.get();
        for (User user : users) {
            userChatRoomRepository.save(
                    UserChatRoom.builder()
                            .chatRoom(chatRoom)
                            .user(user)
                            .build());
        }

        myRoomName = users.stream()
                .filter(it -> !it.getId().equals(Long.parseLong(userId)))
                .map(User::getName)
                .collect(Collectors.joining(", "));

        // 자신과의 채팅방인 경우
        if (users.size() == 1)
            myRoomName = users.get(0).getName();

        membersInfo = users.stream()
                .map(User::toUserDataResDto)
                .collect(Collectors.toCollection(ArrayList::new));

        return ChatRoomDataResDto.builder()
                .chatRoomId(chatRoomId)
                .chatRoomName(myRoomName)
                .chatRoomMembers(members)
                .chatRoomMembersInfo(membersInfo)
                .build();
    }

    @Transactional
    public ChatRoomDataResDto getChatRoomData(ChatRoomDataReqDto chatRoomDataReqDto) {
        log.warn(chatRoomDataReqDto.toString());

        String myRoomName;
        String userId = chatRoomDataReqDto.getChatUserId();
        String chatRoomId = chatRoomDataReqDto.getChatRoomId();
        ArrayList<String> members = chatRoomDataReqDto.getChatRoomMembers();
        Optional<UserChatRoom> optionalUserChatRoom = userChatRoomRepository.findUserChatRoomByChatRoom_IdAndUser_Id(Long.parseLong(chatRoomId), Long.parseLong(userId));

        if (optionalUserChatRoom.isEmpty())
            return null;

        UserChatRoom userChatRoom = optionalUserChatRoom.get();
        myRoomName = userChatRoom.getCurrentChatRoomName(userId);

        ChatRoom chatRoom = userChatRoom.getChatRoom();
        List<Long> memberIds = chatRoom.getChatMembers();
        Optional<ArrayList<User>> optionalUserArrayList = userRepository.findUserByIdIn(memberIds);

        if (optionalUserArrayList.isEmpty())
            return null;

        ArrayList<UserDataResDto> membersInfo = optionalUserArrayList.get()
                .stream()
                .map(User::toUserDataResDto)
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
    public ArrayList<ChatRoomListReqDto> getChatRoomLists(String userId) {
        Optional<ArrayList<UserChatRoom>> optionalUserChatRooms = userChatRoomRepository.findUserChatRoomsByUser_IdOrderByModifiedDataDesc(Long.parseLong(userId));

        if (optionalUserChatRooms.isEmpty())
            return null;

        ArrayList<ChatRoomListReqDto> chatRoomListReqDtos = new ArrayList<>();
        ArrayList<UserChatRoom> userChatRooms = optionalUserChatRooms.get();

        for (UserChatRoom userChatRoom : userChatRooms) {
            ChatRoom chatRoom = userChatRoom.getChatRoom();

            if (chatRoom.getLastMessage() == null)
                continue;

            List<Long> memberIds = chatRoom.getChatMembers();
            Optional<ArrayList<User>> optionalUserArrayList = userRepository.findUserByIdIn(memberIds);

            if (optionalUserArrayList.isEmpty())
                continue;

            ArrayList<User> users = optionalUserArrayList.get();
            ArrayList<UserDataResDto> userDataResDtoArrayList;
            String chatRoomName;

            // 채팅방 이름이 존재할 경우, 존재하는 이름으로 설정
            chatRoomName = userChatRoom.getCurrentChatRoomName(userId);

            if (users.size() == 1) {
                userDataResDtoArrayList = new ArrayList<UserDataResDto>() {{
                    add(users.get(0).toUserDataResDto());
                }};
            } else {
                userDataResDtoArrayList = optionalUserArrayList.get()
                        .stream()
                        .filter(it -> !it.getId().equals(Long.parseLong(userId)))
                        .map(User::toUserDataResDto)
                        .collect(Collectors.toCollection(ArrayList::new));
            }

            UserChatRoomInfoReqDto userChatRoomInfoReqDto =
                    UserChatRoomInfoReqDto.builder()
                            .chatRoomId(Long.toString(userChatRoom.getId()))
                            .chatRoomName(chatRoomName)
                            .unReads(Integer.toString(userChatRoom.getUnReads()))
                            .chatRoomInfo(chatRoom.toChatRoomInfoReqDto())
                            .build();

            chatRoomListReqDtos.add(ChatRoomListReqDto.builder()
                    .userInfo(userDataResDtoArrayList)
                    .userChatRoomInfoReqDto(userChatRoomInfoReqDto)
                    .build());
        }

        return chatRoomListReqDtos;
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

        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findChatRoomById(Long.parseLong(chatRoomId));

        if (optionalChatRoom.isEmpty())
            return false;
        ChatRoom chatRoom = optionalChatRoom.get();

        User exitUser = chatRoom.getUsers().stream()
                .filter(it -> it.getId().equals(Long.parseLong(userId)))
                .collect(Collectors.toList()).get(0);

        // 채팅방 멤버에서 삭제
        chatRoom.exitChatRoom(userId);

        // 해당 채팅방에서 읽은 메시지 삭제
        userChatHistoryRepository.deleteUserChatHistoriesByChatHistory_ChatRoom_IdAndUser_Id(
                Long.parseLong(chatRoomId), Long.parseLong(userId)
        );

        // UserChatRoom 삭제
        Optional<UserChatRoom> optionalUserChatRoom = userChatRoomRepository.findUserChatRoomByChatRoom_IdAndUser_Id(
                Long.parseLong(chatRoomId), Long.parseLong(userId));
        if (optionalUserChatRoom.isPresent()) {
            UserChatRoom userChatRoom = optionalUserChatRoom.get();

            // 1대다 단방향 매핑 관계에 있다면 -> join column ArrayList 에 존재하는 관계도 제거해야한다.
            userChatRoom.getUser().getUserChatRoomList().removeIf(it -> it.getChatRoom().getId().equals(Long.parseLong(chatRoomId)));
            userChatRoom.getChatRoom().getUserChatRoomList().removeIf(it -> it.getUser().getId().equals(Long.parseLong(userId)));
            userChatRoomRepository.delete(userChatRoom);

            if (chatRoom.getChatMembers().size() > 2) {
                // 채팅 기록 저장
                ChatHistory savedHistory = chatHistoryRepository.save(ChatHistory.builder()
                        .user(exitUser)
                        .chatRoom(chatRoom)
                        .history(exitUser.getName() + "님이 채팅방을 나가셨습니다.")
                        .messageType(MessageType.INFO)
                        .build());

                ChatHistorySaveResDto resDto = savedHistory.toChatHistorySaveResDto();
                // 채팅방에 기록 남기기
                messageSender.convertAndSend("/sub/chat/message/" + chatRoom.getId(), resDto);

                // 채팅방에 없는 사람들에게 알리기
                chatRoom.getUsers().stream()
                        .map(User::getId)
                        .forEach(userId1 -> {
                            messageSender.convertAndSend("/sub/chat/frag/" + userId1, "유저=" + userId + " 나감");
                        });
            } else if (chatRoom.getChatMembers().size() == 1){
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
}
