package com.coconut.client.dto.res;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BaseResponseDto {
    /**
     *     @SerializedName("success") var success : Boolean,
     *     @SerializedName("message") var message : String,
     */

    private Boolean success;
    private String message;

    @Builder
    public BaseResponseDto(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
