package com.coconut.client.socket;

import com.coconut.service.socket.SocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SocketChatController {

    private final SocketService socketService;

    private static List<String> enteredUserList = Collections.synchronizedList(new ArrayList<>());


}
