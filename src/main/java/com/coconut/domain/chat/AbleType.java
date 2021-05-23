package com.coconut.domain.chat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AbleType {
    ENABLE("ENABLE", "유효"),
    DISABLE("DISABLE", "무효");

    private final String key;
    private final String title;
}
