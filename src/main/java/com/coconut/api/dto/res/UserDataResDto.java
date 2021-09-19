package com.coconut.api.dto.res;

import com.coconut.domain.user.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserDataResDto {
    /**
     *     @SerializedName("id") var id : String,
     *     @SerializedName("user_id") var user_id : String,
     *     @SerializedName("name") var name : String,
     *     @SerializedName("email") var email : String,
     *     @SerializedName("state_message") var state_message : String ?,
     *     @SerializedName("profile_picture") var profile_picture : String ?,
     *     @SerializedName("background_picture") var background_picture : String ?,
     *     @SerializedName("err") var err : String ?,
     *     @SerializedName("status") var status : Boolean?
     */

    @ApiModelProperty(example = "유저 식별자")
    private Long id;

    @ApiModelProperty(example = "유저 아이디")
    private String userId;

    @ApiModelProperty(example = "유저 이름")
    private String name;

    @ApiModelProperty(example = "유저 이메일")
    private String email;

    @ApiModelProperty(example = "유저 상태메시지")
    private String stateMessage;

    @ApiModelProperty(example = "유저 프로필 사진")
    private String profilePicture;

    @ApiModelProperty(example = "유저 프로필 배경 사진")
    private String backgroundPicture;

    @ApiModelProperty(example = "에러 메시지")
    private String err;

    @ApiModelProperty(example = "성공여부")
    private boolean status;

    @Builder
    public UserDataResDto(Long id, String userId, String name, String email, String stateMessage, String profilePicture, String backgroundPicture, String err, boolean status) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.stateMessage = stateMessage;
        this.profilePicture = profilePicture;
        this.backgroundPicture = backgroundPicture;
        this.err = err;
        this.status = status;
    }

    public static UserDataResDto toDto(User entity) {
        return UserDataResDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .name(entity.getName())
                .email(entity.getEmail())
                .stateMessage(entity.getStateMessage())
                .profilePicture(entity.getProfilePicture())
                .backgroundPicture(entity.getBackgroundPicture())
                .build();
    }

    @Override
    public String toString() {
        return "UserDataResDto{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", stateMessage='" + stateMessage + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                ", backgroundPicture='" + backgroundPicture + '\'' +
                ", err='" + err + '\'' +
                '}';
    }
}
