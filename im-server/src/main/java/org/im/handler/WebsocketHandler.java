package org.im.handler;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import com.alibaba.fastjson2.JSON;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.redis.client.Command;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.Request;
import io.vertx.redis.client.Response;
import org.im.chat.ChatInfo;
import org.im.chat.ChatRecord;
import org.im.config.EsPool;
import org.im.constant.RedisConstant;
import org.im.manager.SessionManager;
import org.im.utils.FutureUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;

public class WebsocketHandler implements Handler<ServerWebSocket> {

    private final static Logger LOG = LoggerFactory.getLogger(WebsocketHandler.class);

    private Redis redis;
    private ElasticsearchAsyncClient esClient;

    public WebsocketHandler(Redis redis) {
        this.redis = redis;
        this.esClient = EsPool.getEsAsyncClient();
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
                                AtomicBoolean flag = new AtomicBoolean(false);
                                for (Response re : res) {
                                    String friend = re.toString();
                                    if (from.equals(friend)) {
                                        ws.writeFinalTextFrame("你已经是好友了").onSuccess(success -> {
                                            flag.set(true);
                                            redis.send(Request.cmd(Command.SADD).arg(RedisConstant.USER_FRIENDS_SET + from).arg(to));
                                        });
                                    }
                                }
                                if (!flag.get()) {
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
                    String chatTime = LocalDateTime.now().toString();
                    String chatId = UUID.randomUUID().toString().replaceAll("-", "");
                    ChatRecord chatRecord = new ChatRecord(chatId, from, to, data, chatTime, chatTime, 1, 0);
                    BulkRequest.Builder bulkBuilder = new BulkRequest.Builder();
                    bulkBuilder.operations(builder ->{
                        return builder.create(c ->{
                            return c.index("chat_record").document(chatRecord).id(chatRecord.chatId());
                        });
                    });
                    BulkRequest bulkRequest = bulkBuilder.build();
                    CompletableFuture<BulkResponse> future = esClient.bulk(bulkRequest);
                    FutureUtils.toFuture(future)
                            .onSuccess(
                            res -> ws.writeFinalTextFrame("聊天记录已保存"))
                            .onFailure(
                            err -> {
                                err.printStackTrace();
                                ws.writeFinalTextFrame("聊天记录保存失败");
                            }
                    );
                    break;
                default:
                    break;
            }
        });
    }

}
