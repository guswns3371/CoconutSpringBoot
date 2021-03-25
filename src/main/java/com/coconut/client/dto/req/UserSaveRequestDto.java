package com.coconut.client.dto.req;

import com.coconut.domain.user.Role;
import com.coconut.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSaveRequestDto {

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
    public UserSaveRequestDto(String email, String userId, String name, String password) {
        this.email = email;
        this.userId = userId;
        this.name = name;
        this.password = password;
    }

    public User toEntity() {
        return User.builder()
                .email(email)
                .userId(userId)
                .name(name)
                .password(password)
                .role(Role.USER)
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
