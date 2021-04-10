//package com.coconut.config.socket;
//
//import com.corundumstudio.socketio.SocketIOServer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class SocketIOConfig {
//
//    @Value("${socket.host}") // yaml 파일에 작성해줘야 값을 불러온다.
//    private String host;
//
//    @Value("${socket.port}")
//    private Integer port;
//
//    @Bean // bean을 등록해야 SocketIOServer 변수를 사용할 수 있다.
//    public SocketIOServer socketIOServer() {
//        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
//        config.setHostname(host);
//        config.setPort(port);
//        System.out.println("소켓 서버 "+host+":"+port);
//        return new SocketIOServer(config);
//    }
//
//}
//
//
