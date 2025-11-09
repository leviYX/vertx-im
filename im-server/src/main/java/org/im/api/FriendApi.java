package org.im.api;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.redis.client.Redis;
import org.im.manager.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 好友接口 列表，删除好友，拉黑好友
 * 因为以上功能均不需要和好友进行信息发送，所以不再ws中处理，而添加好友成功之后需要给好友发送一条通知消息 代码结构后续抽取到platform模块 todo
 *
 */
public class FriendApi {

    private final static Logger LOG = LoggerFactory.getLogger(FriendApi.class);

    // 好友列表
    private static final String FRIEND_LIST_API = "/im/friend/list";
    // 删除好友
    private static final String FRIEND_DEL_API = "/im/friend/del";
    // 拉黑好友
    private static final String FRIEND_BLACK_API = "/im/friend/black";


    public static void attach(Router parent, SessionManager sessionManager, Redis redis) {

        parent.post(FRIEND_LIST_API)
                .handler(BodyHandler.create())
                .handler(context -> {

                    // todo 好友列表
                });

        parent.post(FRIEND_DEL_API)
                .handler(BodyHandler.create())
                .handler(context -> {

                    // todo 删除好友
                });

        parent.post(FRIEND_BLACK_API)
                .handler(BodyHandler.create())
                .handler(context -> {

                    // todo 拉黑好友
                });
    }
}
