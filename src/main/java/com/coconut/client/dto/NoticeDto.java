package com.coconut.client.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class NoticeDto {

    private final String link;
    private final String title;
    private final String author;
    private final String date;

    @Builder
    public NoticeDto(String link, String title, String author, String date) {
        this.link = link;
        this.title = title;
        this.author = author;
        this.date = date;
    }
}
