package org.im.handler;

import com.alibaba.fastjson2.JSON;
import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.redis.client.Command;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.Request;
import org.im.chat.ChatInfo;
import org.im.constant.RedisConstant;
import org.im.manager.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebsocketHandler implements Handler<ServerWebSocket> {

    private final static Logger LOG = LoggerFactory.getLogger(WebsocketHandler.class);

    private Redis redis;

    public WebsocketHandler(Redis redis) {
        this.redis = redis;
    }

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
            int type = chatInfo.type();
            String from = chatInfo.from();
            String to = chatInfo.to();
            String data = chatInfo.data();
            switch (type) {
                case 1:
                    // 加好友
                    redis.send(Request.cmd(Command.SMEMBERS).arg(RedisConstant.USER_FRIENDS_SET + to))
                            .onSuccess(res -> {
                                if(res.get(from) != null) {
                                    ws.writeFinalTextFrame("你已经是好友了").onSuccess(success -> {
                                        redis.send(Request.cmd(Command.SET).arg(RedisConstant.USER_FRIENDS_SET + from).arg(to));
                                    });
                                }else {
                                    String msg = String.format("你好%s,我是%s,我们现在已经是好友了。",to,from);
                                    ServerWebSocket serverWebSocketJoin = SessionManager.connectionClients.get(to);
                                    if(serverWebSocketJoin != null) {
                                        serverWebSocketJoin.writeFinalTextFrame(msg).onSuccess(v -> {
                                            redis.send(Request.cmd(Command.SADD).arg(RedisConstant.USER_FRIENDS_SET + userName).arg(to))
                                                    .onSuccess(r -> System.out.println("追加后长度=" + r.toLong()))
                                                    .onFailure(err -> err.printStackTrace());
                                        });
                                    }
                                }
                            })
                            .onFailure(err -> err.printStackTrace());
                    break;
                case 2:
                    // 普通聊天
                    ServerWebSocket serverWebSocket = SessionManager.connectionClients.get(to);
                    serverWebSocket.writeFinalTextFrame(message);
                    break;
                default:
                    break;
            }
        });
    }
}
