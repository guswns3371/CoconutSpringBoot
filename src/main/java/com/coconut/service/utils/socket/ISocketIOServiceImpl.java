//package com.coconut.service.utils.socket;
//
//import com.corundumstudio.socketio.SocketIOClient;
//import com.corundumstudio.socketio.SocketIONamespace;
//import com.corundumstudio.socketio.SocketIOServer;
//import com.corundumstudio.socketio.listener.ConnectListener;
//import com.corundumstudio.socketio.listener.DataListener;
//import com.corundumstudio.socketio.listener.DisconnectListener;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@RequiredArgsConstructor
//@Service
//public class ISocketIOServiceImpl implements ISocketIOService{
//
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    /**
//     * Store connected clients
//     */
//    private static final Map<String, SocketIOClient> clientMap = new ConcurrentHashMap<>();
//
//    /**
//     * Custom Event`push_data_event` for service side to client communication
//     */
//    private static final String PUSH_DATA_EVENT = "push_data_event";
//
//    private final SocketIOServer socketIOServer;
//    private SocketIONamespace namespace;
//
//    /**
//     * Spring IoC After the container is created, start after loading the SocketIOServiceImpl Bean
//     */
//    @PostConstruct
//    private void autoStartup() {
//        start();
//    }
//
//    /**
//     * Spring IoC Container closes before destroying SocketIOServiceImpl Bean to avoid restarting project service port occupancy
//     */
//    @PreDestroy
//    private void autoStop() {
//        stop();
//    }
//
//    @Override
//    public void start() {
//        socketIOServer.start();
//        socketIOServer.addConnectListener(onConnected);
//        socketIOServer.addDisconnectListener(onDisconnected);
//        socketIOServer.addEventListener(PUSH_DATA_EVENT,String.class,onPushData);
//
////        this.namespace = socketIOServer.addNamespace("/socket");
////        this.namespace.addConnectListener(onConnected);
////        this.namespace.addDisconnectListener(onDisconnected);
////        this.namespace.addEventListener("chat",String.class,onPushData);
//
//        System.out.println("소켓 통신 시작");
//    }
//
//    @Override
//    public void stop() {
//        socketIOServer.stop();
//    }
//
//    @Override
//    public void pushMessageToUser(String userId, String msgContent) {
//        SocketIOClient client = clientMap.get(userId);
//        if (client != null) {
//            client.sendEvent(PUSH_DATA_EVENT, msgContent);
//        }
//    }
//
//    /**
//     * Get the userId parameter in the client url (modified here to suit individual needs and client side)
//     *
//     * @param client: Client
//     * @return: java.lang.String
//     */
//    private String getParamsByClient(SocketIOClient client) {
//        // Get the client url parameter (where userId is the unique identity)
////        Map<String, List<String>> params = client.getUrlParams();
////        List<String> userIdList = params.get("userId");
////        if (!CollectionUtils.isEmpty(userIdList)) {
////            return userIdList.get(0);
////        }
//        return null;
//    }
//
//    /**
//     * Get the connected client ip address
//     *
//     * @param client: Client
//     * @return: java.lang.String
//     */
//    private String getIpByClient(SocketIOClient client) {
//        String sa = client.getRemoteAddress().toString();
//        String clientIp = sa.substring(1, sa.indexOf(":"));
//        return clientIp;
//    }
//
//    private final ConnectListener onConnected = client -> {
//        logger.warn("************ Client: " + getIpByClient(client) + " Connected ************");
//        // Custom Events `connected` -> communicate with clients (built-in events such as Socket.EVENT_CONNECT can also be used)
//        client.sendEvent("connect", "You're connected successfully...");
//        String userId = getParamsByClient(client);
//        if (userId != null) {
//            clientMap.put(userId, client);
//        }
//    };
//
//    private final DisconnectListener onDisconnected = client -> {
//        String clientIp = getIpByClient(client);
//        logger.warn(clientIp + " *********************** " + "Client disconnected");
//        String userId = getParamsByClient(client);
//        if (userId != null) {
//            clientMap.remove(userId);
//            client.disconnect();
//        }
//    };
//
//    private final DataListener<String> onPushData = (client, data, ackSender) -> {
//        // When a client pushes a `client_info_event` event, onData accepts data, which is json data of type string here and can be Byte[], other types of object
//        String clientIp = getIpByClient(client);
//        logger.warn(clientIp + " ************ Client:" + data);
//    };
//}
