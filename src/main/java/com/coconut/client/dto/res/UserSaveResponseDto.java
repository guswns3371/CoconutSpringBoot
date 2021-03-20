package com.coconut.client.dto.res;

import com.coconut.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSaveResponseDto {

    /**
     *     @SerializedName("isEmailOk") var isEmailOk: Boolean,
     *     @SerializedName("isRegistered") var isRegistered : Boolean
     */

    private Boolean isEmailOk;
    private Boolean isRegistered;


    @Builder
    public UserSaveResponseDto(Boolean isEmailOk, Boolean isRegistered) {
        this.isEmailOk = isEmailOk;
        this.isRegistered = isRegistered;
    }

}
