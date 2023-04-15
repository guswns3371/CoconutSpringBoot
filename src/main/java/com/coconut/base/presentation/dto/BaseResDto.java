package com.coconut.base.presentation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BaseResDto {
    /**
     *     @SerializedName("success") var success : Boolean,
     *     @SerializedName("message") var message : String,
     */

    private Boolean success;
    private String message;

    @Builder
    public BaseResDto(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
