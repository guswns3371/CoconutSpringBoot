package com.coconut.service.utils.socket;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatData {

    private String userName;
    private String message;

    @Builder
    public ChatData(String userName, String message) {
        this.userName = userName;
        this.message = message;
    }
}
