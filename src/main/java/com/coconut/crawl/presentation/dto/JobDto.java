package com.coconut.crawl.presentation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class JobDto {
  private final String jobTitle;
  private final String jobLink;
  private final String companyImage;
  private final String companyName;
  private final String career;
  private final String location;
  private final String position;

  @Builder
  public JobDto(String jobTitle, String jobLink, String companyImage, String companyName, String career, String location, String position) {
    this.jobTitle = jobTitle;
    this.jobLink = jobLink;
    this.companyImage = companyImage;
    this.companyName = companyName;
    this.career = career;
    this.location = location;
    this.position = position;
  }
}
