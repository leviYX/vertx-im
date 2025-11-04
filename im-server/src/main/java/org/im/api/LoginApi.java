package org.im.api;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.redis.client.Command;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.Request;
import org.im.constant.RedisConstant;
import org.im.manager.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginApi {

    private final static Logger LOG = LoggerFactory.getLogger(LoginApi.class);

    private static final String LOGIN_API = "/im/login";

    public static void attach(Router parent, SessionManager sessionManager, Redis redis) {
        parent.post(LOGIN_API)
                .handler(BodyHandler.create())
                .handler(context -> {
                    JsonObject bodyAsJson = context.getBodyAsJson();
                    String username = bodyAsJson.getString("username");
                    // 校验是否为注册用户
                    redis.send(Request.cmd(Command.HGET).arg(RedisConstant.USER_REGISTER_INFO).arg(username))
                            .onSuccess(res -> {
                                if (res == null){
                                    context.end("用户" + username + "未注册,请先注册");
                                }else {
                                    // 校验通过, 登陆
                                    LOG.info("用户登陆: {}", username);
                                    String token = sessionManager.login(username);
                                    context.end("用户" + username + "登陆成功,token: " + token);
                                }
                            })
                            .onFailure(err -> err.printStackTrace());
                });
    }
}
