package com.coconut;

import com.coconut.utils.file.FilesStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@RequiredArgsConstructor
@Component
public class ServerCommandLineRunner implements CommandLineRunner {

    @Resource
    private final FilesStorageService storageService;

    @Override
    public void run(String... args) throws Exception {
        storageService.init();
    }
}
