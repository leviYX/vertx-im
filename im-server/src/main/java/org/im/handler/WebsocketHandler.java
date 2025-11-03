package org.im.handler;

import com.alibaba.fastjson2.JSON;
import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;
import org.im.chat.ChatInfo;
import org.im.manager.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebsocketHandler implements Handler<ServerWebSocket> {

    private final static Logger LOG = LoggerFactory.getLogger(WebsocketHandler.class);

    @Override
    public void handle(ServerWebSocket ws) {
        // ws://localhost:8080/im/chat?token=123456 {}
        String path = ws.path(); // /im/chat
        String uri = ws.uri(); // /im/chat?token=123456
        if(!"/im/chat".equals(path)) {
            ws.writeFinalTextFrame("错误路径，我们只接受/im/chat路径");
            ws.reject();
            return;
        }
        // 从uri中提取token
        String token = uri.replaceAll("^.*/im/chat\\?token=(.*)$", "$1");
        if(token.isEmpty()) {
            ws.writeFinalTextFrame("token不能为空");
            ws.reject();
            return;
        }
        String userName = SessionManager.loginUser.get(token);
        if(userName == null) {
            ws.writeFinalTextFrame("token无效,请重新登陆");
            ws.reject();
            return;
        }
        ws.accept();
        SessionManager.connectionClients.put(userName,ws);

        ws.frameHandler(frame -> {
            String message = frame.textData();
            LOG.info("收到消息:{}",message);
            ChatInfo chatInfo = JSON.parseObject(message, ChatInfo.class);
            String to = chatInfo.to();
            ServerWebSocket serverWebSocket = SessionManager.connectionClients.get(to);
            serverWebSocket.writeFinalTextFrame(message);
        });
    }
}
