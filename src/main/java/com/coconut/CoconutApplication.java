package com.coconut;

import com.coconut.util.file.FilesStorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
public class CoconutApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(CoconutApplication.class, args);
    }

    @Resource
    private FilesStorageService storageService;

    @Override
    public void run(String... args) throws Exception {
        storageService.init();
    }
}
