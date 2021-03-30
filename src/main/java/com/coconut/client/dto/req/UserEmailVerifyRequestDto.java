package com.coconut.client.dto.req;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserEmailVerifyRequestDto {
    /**
     *     @SerializedName("email") val email : String,
     *     @SerializedName("secretToken") val secretToken : String
     */

    private String email;
    private String secretToken;

    @Builder
    public UserEmailVerifyRequestDto(String email, String secretToken) {
        this.email = email;
        this.secretToken = secretToken;
    }
}
