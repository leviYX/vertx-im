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

    /**
     * 根据token获取用户名
     *
     * @param token
     * @return
     */
    public static String getUsernameByToken(String token) {
        return loginUser.get(token);
    }

    /**
     * 校验user是否登陆
     *
     * @param token 请求携带的token，该值为登录时获取，后续接入jwt todo
     * @param username 校验的用户名
     * @return
     */
    public boolean isLogin(String token,String username) {
        String user = loginUser.get(token);
        return user != null && user.equals(username);
    }

    private String generateToken(String username) {
        return username + "_" + System.currentTimeMillis();
    }

}
