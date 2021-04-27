package com.coconut.client.dto.res;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserLoginResDto {
    /***
     *   @SerializedName("isCorrect") var isCorrect : Boolean,
     *   @SerializedName("isConfirmed") var isConfirmed : Boolean,
     *   @SerializedName("id") var user_idx : String // user idx
     */

    private Boolean isCorrect;
    private Boolean isConfirmed;
    private String id;

    @Builder
    public UserLoginResDto(Boolean isCorrect, Boolean isConfirmed, String id) {
        this.isCorrect = isCorrect;
        this.isConfirmed = isConfirmed;
        this.id = id;
    }
}
