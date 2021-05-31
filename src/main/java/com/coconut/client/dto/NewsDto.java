package com.coconut.client.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class NewsDto {

    private final String thumbNailImage;
    private final String newsUrl;
    private final String title;
    private final String newsName;

    @Builder
    public NewsDto(String thumbNailImage, String newsUrl, String title, String newsName) {
        this.thumbNailImage = thumbNailImage;
        this.newsUrl = newsUrl;
        this.title = title;
        this.newsName = newsName;
    }
}
