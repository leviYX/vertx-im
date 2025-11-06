package org.im.api;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.redis.client.Redis;
import org.im.manager.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatRecordApi {

    private final static Logger LOG = LoggerFactory.getLogger(ChatRecordApi.class);

    private static final String RECORD_SEARCH_API = "/im/chatRecord/search";

    public static void attach(Router parent, SessionManager sessionManager, Redis redis) {
        parent.post(RECORD_SEARCH_API)
                .handler(BodyHandler.create())
                .handler(context -> {
                    JsonObject bodyAsJson = context.getBodyAsJson();
                    String form = bodyAsJson.getString("form");
                    String to = bodyAsJson.getString("to");
                    String startChatTime = bodyAsJson.getString("startChatTime");
                    String endChatTime = bodyAsJson.getString("endChatTime");
                    String search = bodyAsJson.getString("search");

                    // todo
                    // install ik analyzer
                    // 当前用户是否注册用户
                    // 当前用户是否登陆
                    // 当前用户搜的from是不是自己
                    // 当前用户搜的to是不是自己的好友

                    // es to dsl search

                });
    }
}
