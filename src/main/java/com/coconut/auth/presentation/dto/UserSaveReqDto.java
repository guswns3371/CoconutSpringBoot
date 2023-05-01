package com.coconut.auth.presentation.dto;

import com.coconut.user.domain.constant.Role;
import com.coconut.user.domain.entity.User;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
               .usrId(userId)
               .name(name)
               .password(hashedPassword)
               .role(Role.GUEST) // 일반적인 방법으로 회원가입하면 이메일을 인증해야 USER 권한을 얻는다.
               .build();
  }

}
