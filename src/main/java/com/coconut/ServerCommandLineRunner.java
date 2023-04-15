package com.coconut;

import com.coconut.base.utils.file.FilesStorageService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@RequiredArgsConstructor
@Component
public class ServerCommandLineRunner implements CommandLineRunner, ApplicationListener<ContextClosedEvent> {

    @Resource
    private final FilesStorageService storageService;

    @Override
    public void run(String... args) throws Exception {
        storageService.init();
    }

    /**
     * '애플리케이션'이 죽었을 때 '한 번' 실행됩니다
     * */
    @Override
    public void onApplicationEvent(@NotNull ContextClosedEvent event) {
    }

}
