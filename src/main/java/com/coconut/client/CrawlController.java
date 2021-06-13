package com.coconut.client;

import com.coconut.client.dto.*;
import com.coconut.service.crawling.CrawlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CrawlController {

    private final CrawlService crawlService;

    @GetMapping("/api/crawl/covid")
    public List<CovidStatDto> getKoreaCovidData() throws IOException {
        return crawlService.getKoreaCovidData();
    }

    @GetMapping("/api/crawl/news")
    public List<NewsDto> getNewsData() throws IOException {
        return crawlService.getNewsData();
    }

    @GetMapping("/api/crawl/music")
    public List<MusicDto> getMusicTopList() throws IOException {
        return crawlService.getMusicTopList();
    }

    @GetMapping("/api/crawl/notice")
    public List<NoticeDto> getSeoulTechList() throws IOException {
        return crawlService.getSeoulTechList();
    }

    @GetMapping("/api/crawl/job")
    public List<JobDto> getJobList() throws IOException {
        return crawlService.getJobList();
    }
}
