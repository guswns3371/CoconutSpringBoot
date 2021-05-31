package com.coconut.service.crawling;

import com.coconut.client.dto.CovidStatDto;
import com.coconut.client.dto.MusicDto;
import com.coconut.client.dto.NewsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.coconut.service.utils.url.CrawlUrl.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class CrawlService {

    public List<CovidStatDto> getKoreaCovidData() throws IOException {

        List<CovidStatDto> covidStatDtoList = new ArrayList<>();
        Document doc = Jsoup.connect(KOREA_COVID_DATA_URL)
                .userAgent(USER_AGENT)
                .get();
        Elements contents = doc.select("table tbody tr");

        for (Element content : contents) {
            Elements tdContents = content.select("td");

            CovidStatDto covidStatDto = CovidStatDto.builder()
                    .country(content.select("th").text())
                    .diffFromPrevDay(tdContents.get(0).text())
                    .total(tdContents.get(1).text())
                    .death(tdContents.get(2).text())
                    .incidence(tdContents.get(3).text())
                    .inspection(tdContents.get(4).text())
                    .build();

            covidStatDtoList.add(covidStatDto);
        }

        return covidStatDtoList;
    }

    public List<NewsDto> getNewsData() throws IOException {
        List<NewsDto> newsDtoList = new ArrayList<>();
        Document doc = Jsoup.connect(DAUM_NEWS_URL)
                .userAgent(USER_AGENT)
                .get();

        Elements contents = doc.select("ul.list_news2 li");

        for (Element content : contents) {

            String newsUrl = content.select("div.cont_thumb > strong.tit_thumb > a.link_txt").attr("href");
            String thumbNailImage = content.select("a.link_thumb > img.thumb_g").attr("src");
            String title = content.select("div.cont_thumb > strong.tit_thumb > a.link_txt").text();
            String newsName = content.select("div.cont_thumb > strong.tit_thumb > span.info_news").text();

            NewsDto newsDto = NewsDto.builder()
                    .newsUrl(newsUrl)
                    .thumbNailImage(thumbNailImage)
                    .title(title)
                    .newsName(newsName)
                    .build();

            newsDtoList.add(newsDto);
        }

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
}
