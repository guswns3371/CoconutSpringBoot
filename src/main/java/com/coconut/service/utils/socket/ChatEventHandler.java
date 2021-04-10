//package com.coconut.service.utils.socket;

//@Component
//public class ChatEventHandler {
//
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    private final SocketIOServer server;
//
//    @Autowired
//    public ChatEventHandler(SocketIOServer server) {
//        this.server = server;
//    }
//
//    @OnConnect
//    public void onConnect(SocketIOClient client) {
//        logger.warn("Socket Connect"+ client.getSessionId());
//    }
//    private final SocketIONamespace namespace;
//
//    @Autowired
//    public ChatEventHandler(SocketIOServer server) {
//        this.namespace = server.addNamespace("/chat");
//        this.namespace.addConnectListener(onConnected);
//        this.namespace.addDisconnectListener(onDisconnected);
//        this.namespace.addEventListener("chat", ChatData.class, onChatReceived());
//    }
//
//    private DataListener<ChatData> onChatReceived() {
//        return (client, data, ackSender) -> {
//            logger.warn("Client[{}] - Received chat message '{}'", client.getSessionId().toString(), data);
//            namespace.getBroadcastOperations().sendEvent("chat", data);
//        };
//    }
//
//    private DisconnectListener onDisconnected = new DisconnectListener() {
//        @Override
//        public void onDisconnect(SocketIOClient client) {
//            logger.warn("Client[{}] - Disconnected to chat module through", client.getSessionId().toString());
//
//        }
//    };
//
//    private ConnectListener onConnected = new ConnectListener() {
//        @Override
//        public void onConnect(SocketIOClient client) {
//            logger.warn("Client[{}] - Connected from chat module.", client.getSessionId().toString());
//
//        }
//    };
//}
