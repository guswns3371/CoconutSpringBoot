package com.coconut.service.utils.file;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FileInfo {
    private String name;
    private String url;

    @Builder
    public FileInfo(String name, String url) {
        this.name = name;
        this.url = url;
    }
}
