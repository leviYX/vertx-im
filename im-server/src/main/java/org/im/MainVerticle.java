//package org.im;
//
//import io.vertx.core.AbstractVerticle;
//import io.vertx.core.DeploymentOptions;
//import io.vertx.core.Promise;
//import io.vertx.core.Vertx;
//import io.vertx.core.json.JsonObject;
//import org.im.handler.WebSocketHandler;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class MainVerticle extends AbstractVerticle {
//
//    private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
//
//    public static void main(String[] args) {
//        Vertx vertx = Vertx.vertx();
//
//        DeploymentOptions deploymentOptions = new DeploymentOptions().setConfig(new JsonObject().put("ws.port", 8080));
//        vertx.deployVerticle(new MainVerticle(),deploymentOptions);
//    }
//
//    @Override
//    public void start(Promise<Void> startPromise) throws Exception {
//
//        Integer port = config().getInteger("ws.port", 8080);
//        // 启动http服务端监听程序，处理websocket请求
//        vertx.createHttpServer()
//                // 升级为websocket协议，ws协议是从http协议升级而来的，因为要兼容http协议，是为了弥补
//                .webSocketHandler(new WebSocketHandler())
//                .listen(port, result -> {
//                    if (result.succeeded()) {
//                        LOG.info("HTTP server started on port {}",port);
//                        startPromise.complete();
//                    }else {
//                        LOG.error("Failed to start HTTP server", result.cause());
//                        startPromise.fail(result.cause());
//                    }
//                });
//    }
//}
