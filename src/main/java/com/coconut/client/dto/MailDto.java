package com.coconut.client.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MailDto {
    private String address;
    private String title;
    private String token;

    @Builder
    public MailDto(String address, String title, String token) {
        this.address = address;
        this.title = title;
        this.token = token;
    }
}
