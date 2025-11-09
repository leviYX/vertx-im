package org.im.manager;

import io.vertx.core.Future;
import io.vertx.redis.client.Command;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.Request;
import org.im.constant.RedisConstant;

import java.util.Objects;

public class UserManager {

    public static boolean isRegister(Redis redis,String username) {
        var responseFuture = redis.send(Request.cmd(Command.HGET).arg(RedisConstant.USER_REGISTER_INFO).arg(username));
        Future<Boolean> mapped = responseFuture.map(Objects::nonNull);
        // 阻塞等待结果返回
        return mapped.toCompletionStage().toCompletableFuture().join();
    }
}
