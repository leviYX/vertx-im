package org.im;

import io.vertx.core.http.ServerWebSocket;
import org.im.group.GroupDomin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PriceBroadcast {

    private static final Logger LOG = LoggerFactory.getLogger(PriceBroadcast.class);

    // 连接的客户端，key为用户名称，value为websocket连接
    private static final Map<String, ServerWebSocket> connectionClients = new ConcurrentHashMap<>();

    private String sendName;

    public PriceBroadcast(String sendName) {
        this.sendName = sendName;
    }


    public void singleChat(String userName){
        ServerWebSocket ws = connectionClients.get(userName);
        if (ws != null) {
            ws.writeTextMessage(userName);
        }
    }

//    public void groupChat(String userName) {
//        connectionClients.forEach((user,ws) ->{
//
//        });
//        connectionClients.values().forEach(ws -> {
//             if (!ws.textHandlerID().equals(userName)) {
//                 ws.writeTextMessage(textHandlerID);
//            }
//        });
//    }

    public void register(String userName, ServerWebSocket webSocket) {
        LOG.info("注册用户 :{}", userName);
        connectionClients.put(userName, webSocket);
    }

    public void unregister(String userName) {
        LOG.info("登出用户 :{}", userName);
        connectionClients.remove(userName);
    }

}
