package com.coconut.base.utils.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class FilesStorageServiceImpl implements FilesStorageService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ServletContext context;

    private final Path root = Paths.get("uploads");
    private final Path html = Paths.get("uploads/html");

    @Override
    public void init() {
        try {
            if (!Files.exists(root)) {
                Files.createDirectory(root);
            }

            if (!Files.exists(html)) {
                Files.createDirectory(html);
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    @Override
    public void save(MultipartFile file, String fileName) {
        try {
            Files.copy(file.getInputStream(),
                    this.root.resolve(Objects.requireNonNull(
                            fileName)));
        } catch (Exception e) {
            logger.warn("Could not store the file. Error: " + e.getMessage());
        }

    }

    @Override
    public void saveHtml(String html, String fileName) {
        File saveDir = this.html.toFile();
        File file = new File(saveDir, fileName);
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(html.getBytes(StandardCharsets.UTF_8));
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            return loadFile(file.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public Resource loadHtml(String filename) {
        try {
            Path file = html.resolve(filename);
            return loadFile(file.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    private Resource loadFile(URI uri) throws MalformedURLException {
        Resource resource = new UrlResource(uri);
        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException("Could not read the file!");
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1)
                    .filter(path -> !path.equals(this.root))
                    .map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }
}
