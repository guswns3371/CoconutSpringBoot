package com.coconut.client.dto.res;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class OAuthUserLoginResponseDto {

    /***
     *     @SerializedName("userId") var userId : String?,
     *     @SerializedName("email") var email : String?,
     *     @SerializedName("name") var name : String?,
     *     @SerializedName("profilePicture") var profilePicture : String?
     */

    private String userId;
    private String email;
    private String name;
    private String profilePicture;

    @Builder
    public OAuthUserLoginResponseDto(String userId, String email, String name, String profilePicture) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.profilePicture = profilePicture;
    }


}
