package com.coconut.service.chat;

import com.coconut.client.dto.req.ChatRoomSaveReqDto;
import com.coconut.client.dto.res.ChatHistoryResDto;
import com.coconut.client.dto.res.ChatRoomSaveResDto;
import com.coconut.client.dto.res.UserDataResDto;
import com.coconut.domain.chat.*;
import com.coconut.domain.user.User;
import com.coconut.domain.user.UserRepository;
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
    public ChatRoomSaveResDto makeChatRoom(ChatRoomSaveReqDto chatRoomSaveReqDto) {
        log.warn("makeChatRoom");
        log.warn("user id =" + chatRoomSaveReqDto.getChatUserId() + ", people=" + chatRoomSaveReqDto.getChatRoomMembers().toString());

        String myRoomName = null;
        String chatRoomId;
        String myIndex = chatRoomSaveReqDto.getChatUserId();

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
                    .filter(it -> it.getUser().getId().equals(Long.parseLong(myIndex)))
                    .collect(Collectors.toList())
                    .get(0)
                    .getChatRoomName();

            membersInfo = userChatRoomList.stream()
                    .map(UserChatRoom::getUser)
                    .map(User::toUserDataResDto)
                    .collect(Collectors.toCollection(ArrayList::new));


            return ChatRoomSaveResDto.builder()
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

        for (String userId : members) {
            userRepository.findUserById(Long.parseLong(userId)).ifPresent(users::add);
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

            if (user.getId().toString().equals(myIndex)) {
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

        return ChatRoomSaveResDto.builder()
                .chatRoomId(chatRoomId)
                .chatRoomName(myRoomName)
                .chatRoomMembers(members)
                .chatRoomMembersInfo(membersInfo)
                .build();
    }

    @Transactional
    public ArrayList<ChatHistoryResDto> getChatHistory(String id) {
        log.warn("getChatHistory> chatRoomId="+id);
        Optional<List<ChatHistory>> optionalChatHistory = chatHistoryRepository.findChatHistoriesByChatRoom_Id(Long.parseLong(id));

        return optionalChatHistory.map(chatHistories -> chatHistories.stream()
                .map(ChatHistory::toChatHistoryResDto)
                .collect(Collectors.toCollection(ArrayList::new)))
                .orElse(null);

    }
}
