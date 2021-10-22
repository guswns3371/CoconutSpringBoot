package com.coconut.service;

import com.coconut.api.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.coconut.utils.url.CrawlUrl.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class CrawlService {

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
        Document doc = Jsoup.connect(MELON_MUSIC_URL)
                .userAgent(USER_AGENT)
                .get();

        Elements contents = doc.select("table tbody tr");

        for (Element content : contents) {
            String albumImage = content.select("td > div.wrap > a.image_typeAll > img").attr("src");
            String songTitle = content.select("td > div.wrap > div.wrap_song_info > div[class=ellipsis rank01] > span > a").text();
            String artist = content.select("td > div.wrap > div.wrap_song_info > div[class=ellipsis rank02] > a").text();
            String albumTitle = content.select("td > div.wrap > div.wrap_song_info > div[class=ellipsis rank03] > a").text();

            MusicDto musicDto = MusicDto.builder()
                    .albumImage(albumImage)
                    .songTitle(songTitle)
                    .artist(artist)
                    .albumTitle(albumTitle)
                    .build();

            musicDtoList.add(musicDto);
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

        for (int i = 1; i <= 3; i++) {
            Document doc = Jsoup.connect(PROGRAMMERS_URL + "job?page=" + i)
                    .userAgent(USER_AGENT)
                    .get();

            Elements contents = doc.select("ul[class=list-positions] li[class=list-position-item ]");
            for (Element content : contents) {
                Element itemBody = content.selectFirst("div[class=item-body]");
                String companyImage = content.select("div[class=item-header] > img").attr("src");
                String jobTitle = itemBody.select("h5[class=position-title] > a").text();
                String jobLink = itemBody.select("h5[class=position-title] > a").attr("href");
                String companyName = itemBody.select("h6[class=company-name]").text();
                String career = itemBody.select("ul[class=company-info] > li[class=experience]").text();
                String location = itemBody.select("ul[class=company-info] > li[class=location]").text();
                String position = itemBody.select("ul[class=list-position-tags] li[class=stack-item js-position-tag-filter-item cursor-pointer]")
                        .stream()
                        .map(Element::text)
                        .collect(Collectors.joining(" "));

                if (companyName.contains("평")) {
                    companyName = companyName.split("(평균)")[0];
                }

                JobDto jobDto = JobDto.builder()
                        .companyName(companyName)
                        .companyImage(companyImage)
                        .jobLink(PROGRAMMERS_URL + jobLink)
                        .location(location)
                        .position(position)
                        .jobTitle(jobTitle)
                        .career(career)
                        .build();

                jobDtoList.add(jobDto);
            }
        }
        return jobDtoList;
    }
}
