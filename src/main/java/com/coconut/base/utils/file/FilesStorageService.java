package com.coconut.base.utils.file;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface FilesStorageService {

    public void init();

    public void save(MultipartFile file, String fileName);

    public void saveHtml(String html, String fileName);

    public Resource load(String filename);

    public Resource loadHtml(String filename);

    public void deleteAll();

    public Stream<Path> loadAll();
}
