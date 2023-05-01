package com.coconut.crawl.application;

import com.coconut.common.utils.file.FilesStorageService;
import static com.coconut.crawl.constant.CrawlUrl.DAUM_NEWS_URL;
import static com.coconut.crawl.constant.CrawlUrl.JOB_PARAMS;
import static com.coconut.crawl.constant.CrawlUrl.JOB_PLANET_URL;
import static com.coconut.crawl.constant.CrawlUrl.KOREA_COVID_DATA_URL;
import static com.coconut.crawl.constant.CrawlUrl.MUSIC_URL;
import static com.coconut.crawl.constant.CrawlUrl.NAVER_NEWS_URL;
import static com.coconut.crawl.constant.CrawlUrl.SEOULTECH_NOTICE_URL;
import static com.coconut.crawl.constant.CrawlUrl.USER_AGENT;
import com.coconut.crawl.presentation.dto.CovidStatDto;
import com.coconut.crawl.presentation.dto.JobDto;
import com.coconut.crawl.presentation.dto.MusicDto;
import com.coconut.crawl.presentation.dto.NewsDto;
import com.coconut.crawl.presentation.dto.NoticeDto;
import com.coconut.crawl.utils.selenium.SeleniumService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
@Service
public class CrawlService {

  private final FilesStorageService filesStorageService;
  private final SeleniumService seleniumService;

  public List<CovidStatDto> getKoreaCovidData() throws IOException {

    List<CovidStatDto> covidStatDtoList = new ArrayList<>();
    Document doc = Jsoup.connect(KOREA_COVID_DATA_URL)
                        .userAgent(USER_AGENT)
                        .get();
    Elements contents = doc.select("table[class=num midsize] tbody tr");

    for (Element content : contents) {
      Elements tdContents = content.select("td");

      CovidStatDto covidStatDto = CovidStatDto.builder()
                                              .country(content.select("th").text())
                                              .diffFromPrevDay(tdContents.get(0).text())
                                              .total(tdContents.get(3).text())
                                              .death(tdContents.get(6).text())
                                              .inspection(tdContents.get(7).text())
                                              .build();

      covidStatDtoList.add(covidStatDto);
    }

    return covidStatDtoList;
  }

  public List<NewsDto> getNewsData() throws IOException {
    List<NewsDto> newsDtoList = new ArrayList<>();

    Document daumDoc = Jsoup.connect(DAUM_NEWS_URL)
                            .userAgent(USER_AGENT)
                            .get();

    Elements daumContents = daumDoc.select("ul.list_news2 li");
    for (Element content : daumContents) {

      Element thumbContent = content.selectFirst("div.cont_thumb > strong.tit_thumb");
      String thumbNailImage = content.select("a.link_thumb > img.thumb_g").attr("src");
      String newsUrl = thumbContent.select("a.link_txt").attr("href");
      String title = thumbContent.select("a.link_txt").text();
      String newsName = thumbContent.select("span.info_news").text();

      NewsDto newsDto = NewsDto.builder()
                               .newsUrl(newsUrl)
                               .thumbNailImage(thumbNailImage)
                               .title(title)
                               .newsName(newsName)
                               .siteName("DAUM")
                               .build();

      newsDtoList.add(newsDto);
    }

    Document naverDoc = Jsoup.connect(NAVER_NEWS_URL)
                             .userAgent(USER_AGENT)
                             .get();

    Elements naverContents = naverDoc.select("div[class=_officeCard _officeCard0] div.rankingnews_box");
    for (Element content : naverContents) {
      String newsName = content.select("a > strong.rankingnews_name").text();
      Elements liContents = content.select("ul li");

      for (Element liContent : liContents) {
        String thumbNailImage = liContent.select("a > img").attr("src");
        String title = liContent.select("div.list_content > a").text();
        String newsUrl = liContent.select("div.list_content > a").attr("href");

        NewsDto newsDto = NewsDto.builder()
                                 .newsUrl("https://news.naver.com" + newsUrl)
                                 .thumbNailImage(thumbNailImage)
                                 .title(title)
                                 .newsName(newsName)
                                 .siteName("NAVER")
                                 .build();

        newsDtoList.add(newsDto);
      }
    }

    Collections.shuffle(newsDtoList);
    return newsDtoList;
  }

  public List<MusicDto> getMusicTopList() throws IOException {
    List<MusicDto> musicDtoList = new ArrayList<>();

    for (int i = 1; i <= 2; i++) {
      Document doc = Jsoup.connect(MUSIC_URL + "?ditc=D&ymd=20211027&hh=14&rtm=Y&pg=" + i)
                          .userAgent(USER_AGENT)
                          .get();

      Elements contents = doc.select("tr[class=list]");
      for (Element content : contents) {
        String albumImage = content.select("a[class=cover] > img").attr("src");
        String songTitle = content.select("a[class=title ellipsis]").text();
        String albumTitle = content.select("a[class=albumtitle ellipsis]").text();
        String artist = content.select("a[class=artist ellipsis]").text();

        MusicDto musicDto = MusicDto.builder()
                                    .albumImage("https:" + albumImage)
                                    .songTitle(songTitle)
                                    .artist(artist)
                                    .albumTitle(albumTitle)
                                    .build();

        musicDtoList.add(musicDto);
      }
    }

    return musicDtoList;
  }

  public List<NoticeDto> getSeoulTechList() throws IOException {
    List<NoticeDto> noticeDtoList = new ArrayList<>();
    Document doc = Jsoup.connect(SEOULTECH_NOTICE_URL)
                        .userAgent(USER_AGENT)
                        .get();

    Elements contents = doc.select("table tbody tr");

    for (Element content : contents) {
      String link = content.select("td[class=tit dn2] > a").attr("href");
      String title = content.select("td[class=tit dn2] > a").text();
      String author = content.select("td.dn4").text();
      String date = content.select("td.dn5").text();

      NoticeDto noticeDto = NoticeDto.builder()
                                     .link(SEOULTECH_NOTICE_URL + link)
                                     .title(title)
                                     .author(author)
                                     .date(date)
                                     .build();

      noticeDtoList.add(noticeDto);
    }

    return noticeDtoList;
  }

  public List<JobDto> getJobList() throws IOException {
    List<JobDto> jobDtoList = new ArrayList<>();

    Document doc = Jsoup.connect(JOB_PLANET_URL + JOB_PARAMS + "&page=1")
                        .userAgent(USER_AGENT)
                        .get();

    Elements contents = doc.select("div[class=result_unit_con]");
    for (Element content : contents) {
      String companyImage = content.select("span.llogo > a > img").attr("src");
      String jobLink = content.select("span.llogo > a").attr("href");

      Element itemBody = content.selectFirst("div[class=result_unit_info]");
      String jobTitle = itemBody.select("div[class=unit_head] > a").text();
      String companyName = itemBody.select("p[class=company_name] > a[class=btn_open]").text();
      String salary = itemBody.select("a[class=salary]").text();
      String location = itemBody.select("span[class=tags ]")
                                .stream()
                                .map(Element::text)
                                .collect(Collectors.joining(" "));
      String dDay = itemBody.select("div[class=unit_head] > span").text();

      if (!StringUtils.hasText(salary)) {
        salary = "-";
      }
      if (dDay.contains("(")) {
        dDay = dDay.substring(0, 4);
      } else if (!StringUtils.hasText(dDay)) {
        dDay = "-";
      }
      JobDto jobDto = JobDto.builder()
                            .companyName(companyName)
                            .companyImage(companyImage)
                            .jobLink(JOB_PLANET_URL + jobLink)
                            .location(location)
                            .position(salary)
                            .jobTitle(jobTitle)
                            .career(dDay)
                            .build();
      jobDtoList.add(jobDto);
    }

    return jobDtoList;
  }

}
