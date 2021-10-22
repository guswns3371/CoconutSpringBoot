package com.coconut.api.dto.req;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserChatRoomInfoReqDto {

    /**
     *     @SerializedName("chatRoomId") var chatRoomId : String?,
     *     @SerializedName("unReads") var unReads : String?,
     *     @SerializedName("chatRoomName") var chatRoomName : String?,
     *     @SerializedName("chatRoomInfo") var chatRoomInfo : ChatRoomInfoResponse?,
     */

    private String chatRoomId;
    private String unReads;
    private String chatRoomName;
    private ChatRoomInfoReqDto chatRoomInfo;

    @Builder
    public UserChatRoomInfoReqDto(String chatRoomId, String unReads, String chatRoomName, ChatRoomInfoReqDto chatRoomInfo) {
        this.chatRoomId = chatRoomId;
        this.unReads = unReads;
        this.chatRoomName = chatRoomName;
        this.chatRoomInfo = chatRoomInfo;
    }
}
