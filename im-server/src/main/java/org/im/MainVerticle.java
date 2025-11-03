package org.im;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisOptions;
import org.im.api.LoginApi;
import org.im.config.ConfigLoader;
import org.im.handler.WebsocketHandler;
import org.im.manager.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

    private static final String DEFAULT_REDIS_HOST = "127.0.0.1";
    private static final Integer DEFAULT_REDIS_PORT = 6379;
    private static final Integer DEFAULT_SERVER_PORT = 8080;

    private final static Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

    private static Redis REDIS;

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MainVerticle());
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        // 加载配置文件
        Future<JsonObject> load = ConfigLoader.load(vertx);
        load.onSuccess(config -> {
            LOG.info("yaml config is : {}", config);
            // 创建redis连接池
            // REDIS = createRedisPool(config);

            Integer port = config.getInteger("server.port", DEFAULT_SERVER_PORT);
            // 启动主流程服务
            startUp(startPromise,port);
        }).onFailure(startPromise::fail);
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        // 关闭redis连接池
        REDIS.close();
        stopPromise.complete();
    }

    private void startUp(Promise<Void> startPromise,Integer port) {
        SessionManager sessionManager = new SessionManager();
        HttpServer httpServer = vertx.createHttpServer();
        // 创建路由,springMvc 中的 dispatcherServlet 负责路由分发
        Router router = Router.router(vertx);

        // 注册登陆路由
        LoginApi.attach(router,sessionManager);

        httpServer.requestHandler(router)
                .webSocketHandler(new WebsocketHandler())
                .listen(port, res -> {
                    if (res.succeeded()) {
                        LOG.info("http server started on port {}", port);
                        startPromise.complete();
                    } else {
                        LOG.error("http server start failed on port {}", port, res.cause());
                        startPromise.fail(res.cause());
                    }
                });
    }

    private Redis createRedisPool(JsonObject config) {
        JsonObject redis = config.getJsonObject("redis");
        String host = redis.getString("host", DEFAULT_REDIS_HOST);
        Integer port = redis.getInteger("port", DEFAULT_REDIS_PORT);

        RedisOptions options = new RedisOptions()
                .setConnectionString(String.format("redis://%s:%d", host, port))
                .setMaxPoolSize(Runtime.getRuntime().availableProcessors());
        return Redis.createClient(vertx,options);
    }
}
