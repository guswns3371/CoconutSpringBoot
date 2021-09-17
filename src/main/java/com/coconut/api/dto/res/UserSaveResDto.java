package com.coconut.api.dto.res;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSaveResDto {

    /**
     *     @SerializedName("isEmailOk") var isEmailOk: Boolean,
     *     @SerializedName("isRegistered") var isRegistered : Boolean
     */

    private Boolean isEmailOk;
    private Boolean isRegistered;


    @Builder
    public UserSaveResDto(Boolean isEmailOk, Boolean isRegistered) {
        this.isEmailOk = isEmailOk;
        this.isRegistered = isRegistered;
    }

}

