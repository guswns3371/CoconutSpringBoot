package com.coconut.domain.chat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageType {
    TEXT("TEXT","텍스트"),
    FILE("FILE","파일");

    private final String key;
    private final String title;
}
