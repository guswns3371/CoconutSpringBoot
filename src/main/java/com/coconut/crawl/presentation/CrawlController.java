package com.coconut.crawl.presentation;

import com.coconut.crawl.application.CrawlService;
import com.coconut.crawl.presentation.dto.CovidStatDto;
import com.coconut.crawl.presentation.dto.JobDto;
import com.coconut.crawl.presentation.dto.MusicDto;
import com.coconut.crawl.presentation.dto.NewsDto;
import com.coconut.crawl.presentation.dto.NoticeDto;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/crawl")
public class CrawlController {

  private final CrawlService crawlService;

  @GetMapping("/covid")
  public ResponseEntity<List<CovidStatDto>> getKoreaCovidData() throws IOException {
    return ResponseEntity.ok(crawlService.getKoreaCovidData());
  }

  @GetMapping("/news")
  public ResponseEntity<List<NewsDto>> getNewsData() throws IOException {
    return ResponseEntity.ok(crawlService.getNewsData());
  }

  @GetMapping("/music")
  public ResponseEntity<List<MusicDto>> getMusicTopList() throws IOException {
    return ResponseEntity.ok(crawlService.getMusicTopList());
  }

  @GetMapping("/notice")
  public ResponseEntity<List<NoticeDto>> getSeoulTechList() throws IOException {
    return ResponseEntity.ok(crawlService.getSeoulTechList());
  }

  @GetMapping("/job")
  public ResponseEntity<List<JobDto>> getJobList() throws IOException {
    return ResponseEntity.ok(crawlService.getJobList());
  }
}
