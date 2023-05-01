package com.coconut.socket.presentation.dto;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
// https://firebase.google.com/docs/reference/fcm/rest/v1/projects.messages/send
public class FcmMessageDto {
  private boolean validate_only;
  private Message message;

  @Builder
  public FcmMessageDto(boolean validate_only, Message message) {
    this.validate_only = validate_only;
    this.message = message;
  }

  @Getter
  @NoArgsConstructor
  public static class Message {
    private Notification notification;
    private String token;
    private Map<String, String> data;

    @Builder
    public Message(Notification notification, String token, Map<String, String> data) {
      this.notification = notification;
      this.token = token;
      this.data = data;

    }
  }

  @NoArgsConstructor
  @Getter
  public static class Notification {
    private String title;
    private String body;
    private String image;

    @Builder
    public Notification(String title, String body, String image) {
      this.title = title;
      this.body = body;
      this.image = image;
    }
  }
}
