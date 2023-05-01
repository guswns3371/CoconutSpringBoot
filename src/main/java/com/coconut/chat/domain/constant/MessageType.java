package com.coconut.chat.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageType {
  TEXT("TEXT", "텍스트"),
  IMAGE("IMAGE", "이미지"),
  FILE("FILE", "파일"),
  INFO("INFO", "알림");

  private final String key;
  private final String title;
}
