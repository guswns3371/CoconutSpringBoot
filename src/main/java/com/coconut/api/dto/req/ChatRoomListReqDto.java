package com.coconut.api.dto.req;

import com.coconut.api.dto.res.UserDataResDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
@Getter
public class ChatRoomListReqDto {
    /**
     * @SerializedName("chatRoomId") var chatRoomId : String?,
     * @SerializedName("unReads") var unReads : String?,
     * @SerializedName("chatRoomName") var chatRoomName : String?,
     * @SerializedName("chatRoomInfo") var chatRoomInfo : ChatRoomInfoResponse?,
     * @SerializedName("userInfo") var userInfo : ArrayList<UserDataResponse>?
     */

    private String chatRoomId;
    private String unReads;
    private String chatRoomName;
    private ChatRoomInfoReqDto chatRoomInfo;
    private ArrayList<UserDataResDto> userInfos;

    @Builder
    public ChatRoomListReqDto(UserChatRoomInfoReqDto userChatRoomInfoReqDto, ArrayList<UserDataResDto> userInfos) {
        this.chatRoomId = userChatRoomInfoReqDto.getChatRoomId();
        this.unReads = userChatRoomInfoReqDto.getUnReads();
        this.chatRoomName = userChatRoomInfoReqDto.getChatRoomName();
        this.chatRoomInfo = userChatRoomInfoReqDto.getChatRoomInfo();
        this.userInfos = userInfos;
    }

    @Override
    public String toString() {
        return "ChatRoomListReqDto{" +
                "chatRoomId='" + chatRoomId + '\'' +
                ", unReads='" + unReads + '\'' +
                ", chatRoomName='" + chatRoomName + '\'' +
                ", chatRoomInfo=" + chatRoomInfo +
                ", userInfo=" + userInfos +
                '}';
    }
}
