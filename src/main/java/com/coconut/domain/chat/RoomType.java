package com.coconut.domain.chat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomType {
    GROUP("GROUP","그룹채팅"),
    ME("ME","나와의채팅");

    private final String key;
    private final String title;
}
