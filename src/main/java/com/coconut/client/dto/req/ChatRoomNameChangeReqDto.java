package com.coconut.client.dto.req;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomNameChangeReqDto {
    /**
     *     @SerializedName("chatRoomName") var chatRoomName : String?,
     *     @SerializedName("chatRoomId") var chatRoomId : String?,
     *     @SerializedName("userId") var userId : String?
     */

    private String chatRoomName;
    private String chatRoomId;
    private String userId;
}
