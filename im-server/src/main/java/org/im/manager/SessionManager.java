package org.im.manager;

import io.vertx.core.http.ServerWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private final static Logger LOG = LoggerFactory.getLogger(SessionManager.class);

    // 登陆到系统的用户 k:token v:userName
    public static final Map<String,String> loginUser = new ConcurrentHashMap<>();

    // 连接客户端，k:userName v:websocket连接
    public static final Map<String, ServerWebSocket> connectionClients = new ConcurrentHashMap<>();

    public String login(String username) {
        LOG.info("login username:{}",username);
        if (loginUser.containsKey(username)) {
            return loginUser.get(username);
        }
        String token = generateToken(username);
        loginUser.put(token,username);
        return token;
    }

    private String generateToken(String username) {
        return username + "_" + System.currentTimeMillis();
    }

}
