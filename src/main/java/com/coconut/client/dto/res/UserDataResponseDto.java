package com.coconut.client.dto.res;

import com.coconut.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserDataResponseDto {
    /**
     *     @SerializedName("id") var id : String,
     *     @SerializedName("user_id") var user_id : String,
     *     @SerializedName("name") var name : String,
     *     @SerializedName("email") var email : String,
     *     @SerializedName("state_message") var state_message : String ?,
     *     @SerializedName("profile_picture") var profile_picture : String ?,
     *     @SerializedName("background_picture") var background_picture : String ?,
     *     @SerializedName("err") var err : String ?,
     *     var status : Boolean
     */

    private Long id;
    private String userId;
    private String name;
    private String email;
    private String stateMessage;
    private String profilePicture;
    private String backgroundPicture;

    @Builder
    public UserDataResponseDto(Long id, String userId, String name, String email, String stateMessage, String profilePicture, String backgroundPicture) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.stateMessage = stateMessage;
        this.profilePicture = profilePicture;
        this.backgroundPicture = backgroundPicture;
    }

    public static UserDataResponseDto toDto(User entity) {
        return UserDataResponseDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .name(entity.getName())
                .email(entity.getEmail())
                .stateMessage(entity.getStateMessage())
                .profilePicture(entity.getProfilePicture())
                .backgroundPicture(entity.getBackgroundPicture())
                .build();
    }
}
