package com.coconut.client.dto.req;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLoginReqDto {
    /***
     *     @SerializedName("email") var email : String,
     *     @SerializedName("password") var password : String
     */

    private String email;
    private String password;

    @Builder
    public UserLoginReqDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
