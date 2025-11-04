package org.im.api;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.im.manager.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterApi {

    private final static Logger LOG = LoggerFactory.getLogger(RegisterApi.class);

    private static final String LOGIN_API = "/im/login";

    public static void attach(Router parent, SessionManager sessionManager){
        parent.post(LOGIN_API)
                .handler(BodyHandler.create())
                .handler(context -> {
                    JsonObject bodyAsJson = context.getBodyAsJson();
                    String username = bodyAsJson.getString("username");
                    LOG.info("用户登陆: {}", username);
                    String token = sessionManager.login(username);
                    context.end("用户" + username + "登陆成功,token: " + token);
                });
    }
}
