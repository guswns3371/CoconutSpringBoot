package com.coconut.auth.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserEmailVerifyReqDto {
    /**
     *     @SerializedName("email") val email : String,
     *     @SerializedName("secretToken") val secretToken : String
     */

    private String email;
    private String secretToken;

}
