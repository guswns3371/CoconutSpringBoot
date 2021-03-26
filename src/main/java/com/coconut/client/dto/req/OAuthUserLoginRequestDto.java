package com.coconut.client.dto.req;

import com.coconut.domain.user.Role;
import com.coconut.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OAuthUserLoginRequestDto {

    /***
     *     @SerializedName("name") var name : String?,
     *     @SerializedName("email") var email : String?,
     *     @SerializedName("profilePicture") var profilePicture : String?
     */

    private String name;
    private String email;
    private String profilePicture;

    @Builder
    public OAuthUserLoginRequestDto(String name, String email, String profilePicture) {
        this.name = name;
        this.email = email;
        this.profilePicture = profilePicture;
    }

    public User toEntity() {
        return User.builder()
                .name(name)
                .email(email)
                .profilePicture(profilePicture)
                .role(Role.USER) // OAUTH로 가입한 회원은 USER권한을 얻는다.
                .build();
    }

    @Override
    public String toString() {
        return "OAuthUserLoginRequestDto{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                '}';
    }
}
