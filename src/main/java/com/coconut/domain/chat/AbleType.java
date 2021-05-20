package com.coconut.domain.chat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AbleType {
    ENABLE("ENABLE","��ȿ"),
    DISABLE("DISABLE","��ȿ");

    private final String key;
    private final String title;
}
