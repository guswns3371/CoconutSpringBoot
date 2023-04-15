package com.coconut.auth.presentation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserFcmUpdateReqDto {

    /**
     *     @SerializedName("userId") var userId : String,
     *     @SerializedName("fcmToken") var fcmToken : String?
     */

    private String userId;
    private String fcmToken;

    @Builder
    public UserFcmUpdateReqDto(String userId, String fcmToken) {
        this.userId = userId;
        this.fcmToken = fcmToken;
    }

}
