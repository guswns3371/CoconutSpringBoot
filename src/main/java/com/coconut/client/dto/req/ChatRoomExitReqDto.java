package com.coconut.client.dto.req;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomExitReqDto {
    /**
     *     @SerializedName("chatRoomId") var chatRoomId : String?,
     *     @SerializedName("userId") var userId : String?
     */

    private String chatRoomId;
    private String userId;
}
