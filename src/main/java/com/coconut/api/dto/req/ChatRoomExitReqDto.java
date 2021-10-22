package com.coconut.api.dto.req;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomExitReqDto {
    /**
     *     @SerializedName("chatRoomId") var chatRoomId : String?,
     *     @SerializedName("userId") var userId : String?
     */

    private Long chatRoomId;
    private Long userId;
}
