package com.coconut.client;

import com.coconut.client.model.EchoModel;
import com.coconut.service.socket.SocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SocketController {

    private final SocketService socketService;

    private static List<String> connectedUserList = Collections.synchronizedList(new ArrayList<>());

    // 소켓으로 받음
    @MessageMapping("/hello-msg-mapping") // client에서 /topic/hello-msg-mapping 로 send하면 값을 String message로 받음
    @SendTo("/topic/greetings") // client에서 /topic/greetings에 join한 사용자에게 return값을 보냄
    public EchoModel echoMessageMapping(String message) {
        log.warn("React to hello-msg-mapping : " + message);
        return EchoModel.builder()
                .echo("소켓 통신 개통 축하한다 새꺄 id="+message)
                .userIndex(message)
                .build();
    }

    @MessageMapping("/online")
    @SendTo("/topic/service")
    public List<String> onlineUsers(String userIndex) {
        connectedUserList.add(userIndex.trim());
        connectedUserList = connectedUserList.parallelStream().distinct().collect(Collectors.toList());
        log.warn("online User = "+userIndex.trim()+" : "+connectedUserList);
        return connectedUserList;
    }

    @MessageMapping("/offline")
    @SendTo("/topic/service")
    public List<String> offlineUsers(String userIndex) {
        connectedUserList.remove(userIndex.trim());
        connectedUserList = connectedUserList.parallelStream().distinct().collect(Collectors.toList());
        if (!connectedUserList.isEmpty()) {
            log.warn("offline User = "+userIndex.trim()+" : "+connectedUserList);
        }
        else {
            log.warn("offline User = "+userIndex.trim());
        }
        return connectedUserList;
    }

    // HTTP로 받음
    @RequestMapping(value = "/hello-msg-mapping"
            , method = RequestMethod.POST)
    public void echoConvertAndSend(@RequestParam("msg") String message) {
        socketService.echoMessage(message);
    }
}
