package org.im.api;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.redis.client.Command;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.Request;
import org.im.constant.RedisConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 注册接口
 */
public class RegisterApi {

    private final static Logger LOG = LoggerFactory.getLogger(RegisterApi.class);

    private static final String REGISTER_API = "/im/register";

    public static void attach(Router parent, Redis redis){
        parent.post(REGISTER_API)
                .handler(BodyHandler.create())
                .handler(context -> {
                    JsonObject bodyAsJson = context.getBodyAsJson();
                    String username = bodyAsJson.getString("username");
                    LOG.info("用户注册: {}", username);
                    // 写入redis todo
                    redis.send(Request.cmd(Command.HSET).arg(RedisConstant.USER_REGISTER_INFO).arg(username).arg(1))
                            .onSuccess(res -> System.out.println("追加后长度=" + res.toLong()))
                            .onFailure(err -> System.err.println("追加失败: " + err));
                    context.end("用户" + username + "注册成功");
                });
    }
}
