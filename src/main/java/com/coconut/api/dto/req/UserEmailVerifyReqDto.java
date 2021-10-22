package com.coconut.api.dto.req;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserEmailVerifyReqDto {
    /**
     *     @SerializedName("email") val email : String,
     *     @SerializedName("secretToken") val secretToken : String
     */

    private String email;
    private String secretToken;

    @Builder
    public UserEmailVerifyReqDto(String email, String secretToken) {
        this.email = email;
        this.secretToken = secretToken;
    }
}
