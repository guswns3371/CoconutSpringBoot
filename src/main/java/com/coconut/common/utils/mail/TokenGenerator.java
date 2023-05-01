package com.coconut.common.utils.mail;

import java.security.SecureRandom;
import java.util.Base64;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TokenGenerator {
  private final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

  public String generateNewToken() {
    SecureRandom secureRandom = new SecureRandom();
    byte[] randomBytes = new byte[20];
    secureRandom.nextBytes(randomBytes);
    return base64Encoder.encodeToString(randomBytes);
  }
}
