package com.coconut.crawl.presentation.dto;

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
  private final String siteName;

  @Builder
  public NewsDto(String thumbNailImage, String newsUrl, String title, String newsName, String siteName) {
    this.thumbNailImage = thumbNailImage;
    this.newsUrl = newsUrl;
    this.title = title;
    this.newsName = newsName;
    this.siteName = siteName;
  }
}
