package com.coconut.client.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MusicDto {

    private final String albumImage;
    private final String artist;
    private final String songTitle;
    private final String albumTitle;

    @Builder
    public MusicDto(String albumImage, String songTitle, String artist, String albumTitle) {
        this.albumImage = albumImage;
        this.songTitle = songTitle;
        this.artist = artist;
        this.albumTitle = albumTitle;
    }
}
