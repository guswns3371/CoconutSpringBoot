package com.coconut.service.chat;

import com.coconut.client.dto.req.ChatRoomDataReqDto;
import com.coconut.client.dto.req.ChatRoomListReqDto;
import com.coconut.client.dto.req.ChatRoomSaveReqDto;
import com.coconut.client.dto.req.UserChatRoomInfoReqDto;
import com.coconut.client.dto.res.ChatHistoryResDto;
import com.coconut.client.dto.res.ChatRoomDataResDto;
import com.coconut.client.dto.res.UserDataResDto;
import com.coconut.domain.chat.*;
import com.coconut.domain.user.User;
import com.coconut.domain.user.UserRepository;
import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;
    private final ChatHistoryRepository chatHistoryRepository;

    @Transactional
    public ChatRoomDataResDto makeChatRoom(ChatRoomSaveReqDto chatRoomSaveReqDto) {
        String myRoomName = null;
        String chatRoomId;
        String userId = chatRoomSaveReqDto.getChatUserId();
        ArrayList<String> members = chatRoomSaveReqDto.getChatRoomMembers();
        Collections.sort(members);

        ArrayList<User> users = new ArrayList<>();
        ArrayList<UserDataResDto> membersInfo;
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findChatRoomByMembers(members.toString());

        // 채팅방이 이미 만들어져 있는 경우
        if (optionalChatRoom.isPresent()) {
            log.warn("이미 존재하는 채팅방");
            ChatRoom chatRoom = optionalChatRoom.get();

            chatRoomId = chatRoom.getId().toString();

            ArrayList<UserChatRoom> userChatRoomList = new ArrayList<>(chatRoom.getUserList());

            myRoomName = userChatRoomList.stream()
                    .filter(it -> it.getChatRoom().getId().equals(Long.parseLong(chatRoomId)))
                    .filter(it -> it.getUser().getId().equals(Long.parseLong(userId)))
                    .collect(Collectors.toList())
                    .get(0)
                    .getChatRoomName();

            membersInfo = userChatRoomList.stream()
                    .map(UserChatRoom::getUser)
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
        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder()
                .members(members.toString())
                .build());

        chatRoomId = chatRoom.getId().toString();

        for (String memberId : members) {
            userRepository.findUserById(Long.parseLong(memberId)).ifPresent(users::add);
        }

        if (users.size() == 0) {
            log.warn("존재하지 않은 유저들");
            return null;
        }

        for (User user : users) {
            String chatRoomName = users.stream()
                    .map(User::getName)
                    .filter(name -> !name.contains(user.getName()))
                    .collect(Collectors.joining(", "));

            // 자신과의 채팅방인 경우
            if (users.size() == 1) {
                chatRoomName = user.getName();
            }

            if (user.getId().toString().equals(userId)) {
                myRoomName = chatRoomName;
            }

            userChatRoomRepository.save(
                    UserChatRoom.builder()
                            .chatRoom(chatRoom)
                            .user(user)
                            .chatRoomName(chatRoomName)
                            .build());
        }

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
        myRoomName = userChatRoom.getChatRoomName();

        List<Long> memberIds =
                new GsonBuilder().create().fromJson(members.toString(), new TypeToken<ArrayList<Long>>() {
                }.getType());

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
        chatHistories.forEach(chatHistory -> chatHistory.updateReadMembers(Integer.toString(chatHistory.getReadUserList().size())));

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

            UserChatRoomInfoReqDto userChatRoomInfoReqDto =
                    UserChatRoomInfoReqDto.builder()
                            .chatRoomId(Long.toString(userChatRoom.getId()))
                            .chatRoomName(userChatRoom.getChatRoomName())
                            .unReads(Integer.toString(userChatRoom.getUnReads()))
                            .chatRoomInfo(chatRoom.toChatRoomInfoReqDto())
                            .build();

            List<Long> memberIds =
                    new GsonBuilder().create().fromJson(chatRoom.getMembers(), new TypeToken<ArrayList<Long>>() {
                    }.getType());
            Optional<ArrayList<User>> optionalUserArrayList = userRepository.findUserByIdIn(memberIds);

            if (optionalUserArrayList.isEmpty())
                continue;

            ArrayList<User> users = optionalUserArrayList.get();
            ArrayList<UserDataResDto> userDataResDtoArrayList;

            if (users.size() == 1) {
                userDataResDtoArrayList = users.stream()
                        .map(User::toUserDataResDto)
                        .collect(Collectors.toCollection(ArrayList::new));
            } else {
                userDataResDtoArrayList = optionalUserArrayList.get()
                        .stream()
                        .map(User::toUserDataResDto)
                        .filter(it -> !it.getId().equals(Long.parseLong(userId)))
                        .collect(Collectors.toCollection(ArrayList::new));
            }

            chatRoomListReqDtos.add(ChatRoomListReqDto.builder()
                    .userChatRoomInfoReqDto(userChatRoomInfoReqDto)
                    .userInfo(userDataResDtoArrayList)
                    .build());
        }

        return chatRoomListReqDtos;
    }
}
