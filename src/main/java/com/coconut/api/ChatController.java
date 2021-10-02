package com.coconut.api;

import com.coconut.api.dto.req.*;
import com.coconut.api.dto.res.ChatHistoryResDto;
import com.coconut.api.dto.res.ChatRoomDataResDto;
import com.coconut.api.dto.res.UserDataResDto;
import com.coconut.domain.chat.AbleType;
import com.coconut.domain.chat.ChatRoom;
import com.coconut.domain.chat.RoomType;
import com.coconut.domain.chat.UserChatRoom;
import com.coconut.domain.user.User;
import com.coconut.service.*;
import com.coconut.service.utils.file.FilesStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final EntityManager em;

    private final UserService userService;
    private final ChatRoomService chatRoomService;
    private final ChatHistoryService chatHistoryService;
    private final UserChatRoomService userChatRoomService;
    private final UserChatHistoryService userChatHistoryService;
    private final SimpMessageSendingOperations messageSender;
    private final FilesStorageService storageService;


    @PostMapping("/room/make")
    public ChatRoomDataResDto makeChatRoom(@RequestBody ChatRoomSaveReqDto reqDto) {
        String userId = reqDto.getChatUserId();
        ArrayList<String> members = reqDto.getChatRoomMembers();

        // 유저 목록 가져오기
        Optional<ArrayList<User>> optionalUsers = userService.findUsersByIds(reqDto.getMemberLongIds());
        if (optionalUsers.isEmpty()) {
            throw new IllegalStateException("존재하지 않는 유저들 입니다.");
        }
        ArrayList<User> users = optionalUsers.get();

        // 채팅방 이름
        String roomName = (users.size() == 1) ? users.get(0).getName() : chatRoomName(users, Long.parseLong(userId));

        // 채팅방이 이미 존재하는 경우
        Optional<ChatRoom> optionalChatRoom = chatRoomService.findByMembers(members.toString());
        if (optionalChatRoom.isPresent()) {
            return ChatRoomDataResDto.builder()
                    .chatRoomId(optionalChatRoom.get().getId().toString())
                    .chatRoomName(roomName)
                    .chatRoomMembers(members)
                    .chatRoomMembersInfo(getUserData(users))
                    .build();
        }

        // 채팅방 생성
        RoomType roomType = (members.size() == 1 && members.get(0).equals(userId)) ? RoomType.ME : RoomType.GROUP;
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

    private String chatRoomName(ArrayList<User> users, Long userId) {
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

    @GetMapping("/room/list/{userId}")
    public ArrayList<ChatRoomListReqDto> getChatRoomLists(@PathVariable Long userId) {
        ArrayList<ChatRoomListReqDto> chatRoomListReqDtoList = new ArrayList<>();
        ArrayList<UserDataResDto> userDataResDtoList = new ArrayList<>();
        UserChatRoomInfoReqDto userChatRoomInfoReqDto;

        // UserChatRoom 목록 가져오기
        Optional<ArrayList<UserChatRoom>> optionalUserChatRooms = userChatRoomService.findAllByUserId(userId);
        if (optionalUserChatRooms.isEmpty()) {
            return null;
        }

        // UserChatRoom 리스트
        for (UserChatRoom userChatRoom : optionalUserChatRooms.get()) {
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
            ArrayList<User> users = chatRoom.getUsers();

            // User response dto
            if (chatRoom.getRoomType().equals(RoomType.ME)) {
                // 자신과의 채팅방인 경우
                userDataResDtoList.add(new UserDataResDto(users.get(0)));
            } else if (chatRoom.getRoomType().equals(RoomType.GROUP)) {
                // 그룹 채팅방인 경우
                userDataResDtoList = users.stream()
                        .filter(user1 -> !user1.getId().equals(userId))
                        .map(UserDataResDto::new)
                        .collect(Collectors.toCollection(ArrayList::new));
            }

            // UserChatRoom response dto
            userChatRoomInfoReqDto = new UserChatRoomInfoReqDto(userChatRoom);

            // ChatRoomListReqDto 리스트
            chatRoomListReqDtoList.add(ChatRoomListReqDto.builder()
                    .userChatRoomInfoReqDto(userChatRoomInfoReqDto)
                    .userInfos(userDataResDtoList)
                    .build());
        }

        if (chatRoomListReqDtoList.size() == 0) {
            return null;
        }

        return chatRoomListReqDtoList;
    }

    @PostMapping("/room/info")
    public ChatRoomDataResDto getChatRoomData(@RequestBody ChatRoomDataReqDto chatRoomDataReqDto) {
        return chatService.getChatRoomData(chatRoomDataReqDto);
    }

    @PostMapping("/room/invite")
    public ChatRoomDataResDto inviteUser(@RequestBody ChatRoomDataReqDto chatRoomDataReqDto) {
        return chatService.inviteUser(chatRoomDataReqDto);
    }

    @GetMapping("/{chatRoomId}")
    public ArrayList<ChatHistoryResDto> getChatHistory(@PathVariable String chatRoomId) {
        return chatService.getChatHistory(chatRoomId);
    }

    @PostMapping("/room/name")
    public boolean changeChatRoomName(@RequestBody ChatRoomNameChangeReqDto reqDto) {
        return chatService.changeChatRoomName(reqDto);
    }

    @PostMapping("/room/exit")
    public boolean exitChatRoom(@RequestBody ChatRoomExitReqDto reqDto) {
        return chatService.exitChatRoom(reqDto);
    }

    @PostMapping(
            value = "/upload/image",
            consumes = {
                    MediaType.MULTIPART_FORM_DATA_VALUE,
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public ArrayList<String> uploadChatImages(
            @RequestPart(value = "userId", required = false) String userId,
            @RequestPart(value = "chatRoomId", required = false) String chatRoomId,
            @RequestPart(required = false) MultipartFile[] images
    ) {
        return chatService.uploadChatImages(
                ChatUploadImageReqDto.builder()
                        .userId(userId)
                        .chatRoomId(chatRoomId)
                        .images(images)
                        .build());
    }
}
