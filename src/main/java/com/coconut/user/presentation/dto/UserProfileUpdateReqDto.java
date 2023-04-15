package com.coconut.user.presentation.dto;

import com.coconut.user.domain.entity.User;
import com.coconut.base.utils.file.PathNameBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
public class UserProfileUpdateReqDto {
    /**
     * var id : RequestBody?,
     * var userId : RequestBody?,
     * var name : RequestBody?,
     * var message : RequestBody?,
     * var profileImage : MultipartBody.Part?,
     * var backImage : MultipartBody.Part?
     *
     * @Part("id") id : RequestBody?,
     * @Part("userId") userId : RequestBody?,
     * @Part("name") name : RequestBody?,
     * @Part("message") message : RequestBody?,
     * @Part images : Array<MultipartBody.Part?>?
     * <p>
     * prepareFilePart("profileImage",profileImage),
     * prepareFilePart("backImage",backImage))
     */

    private String id;
    private String userId;
    private String name;
    private String message;
    private MultipartFile profileImage;
    private MultipartFile backImage;

    @Builder
    public UserProfileUpdateReqDto(String id,
                                   String userId,
                                   String name,
                                   String message,
                                   MultipartFile profileImage,
                                   MultipartFile backImage) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.message = message;
        this.profileImage = profileImage;
        this.backImage = backImage;
    }

    public User toEntity() {
        String profileImagePath = null;
        String backImagePath = null;

        if (profileImage != null)
            profileImagePath = PathNameBuilder.builder()
                    .userIndex(id)
                    .fileOriginalName(profileImage.getOriginalFilename())
                    .build()
                    .getProfileImagePath();

        if (backImage != null)
            backImagePath = PathNameBuilder.builder()
                    .userIndex(id)
                    .fileOriginalName(backImage.getOriginalFilename())
                    .build()
                    .getBackgroundImagePath();

        return User.builder()
                .usrId(userId)
                .name(name)
                .stateMessage(message)
                .profilePicture(profileImagePath)
                .backgroundPicture(backImagePath)
                .build();
    }

    @Override
    public String toString() {
        return "UserProfileUpdateRequestDto{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", message='" + message + '\'' +
                ", profileImage=" + profileImage +
                ", backImage=" + backImage +
                '}';
    }
}
