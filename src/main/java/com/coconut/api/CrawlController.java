package com.coconut.api;

import com.coconut.api.dto.*;
import com.coconut.service.CrawlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/crawl")
public class CrawlController {

    private final CrawlService crawlService;

    @GetMapping("/covid")
    public List<CovidStatDto> getKoreaCovidData() throws IOException {
        return crawlService.getKoreaCovidData();
    }

    @GetMapping("/news")
    public List<NewsDto> getNewsData() throws IOException {
        return crawlService.getNewsData();
    }

    @GetMapping("/music")
    public List<MusicDto> getMusicTopList() throws IOException {
        return crawlService.getMusicTopList();
    }

    @GetMapping("/notice")
    public List<NoticeDto> getSeoulTechList() throws IOException {
        return crawlService.getSeoulTechList();
    }

    @GetMapping("/job")
    public List<JobDto> getJobList() throws IOException {
        return crawlService.getJobList();
    }
}
