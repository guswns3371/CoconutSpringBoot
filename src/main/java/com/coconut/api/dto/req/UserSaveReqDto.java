package com.coconut.api.dto.req;

import com.coconut.domain.user.Role;
import com.coconut.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSaveReqDto {

    /**
     *     @SerializedName("email") var userEmail : String,
     *     @SerializedName("userId") var userId : String,
     *     @SerializedName("name") var userName : String,
     *     @SerializedName("password") var userPassword : String
     */

    private String email;
    private String userId;
    private String name;
    private String password;

    @Builder
    public UserSaveReqDto(String email, String userId, String name, String password) {
        this.email = email;
        this.userId = userId;
        this.name = name;
        this.password = password;
    }

    public User toEntity(String hashedPassword) {
        return User.builder()
                .email(email)
                .uId(userId)
                .name(name)
                .password(hashedPassword)
                .role(Role.GUEST) // 일반적인 방법으로 회원가입하면 이메일을 인증해야 USER 권한을 얻는다.
                .build();
    }

    @Override
    public String toString() {
        return "UserSaveRequestDto{" +
                "email='" + email + '\'' +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
