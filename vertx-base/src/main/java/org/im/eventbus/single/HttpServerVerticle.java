package org.im.eventbus.single;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.TimeoutStream;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import org.im.eventbus.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServerVerticle extends AbstractVerticle {

    private final Logger LOG = LoggerFactory.getLogger(HttpServerVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        // 从配置中获取监听端口
        Integer port = config().getInteger("port");
        LOG.info("HttpServerVerticle started on thread: {}", Thread.currentThread().getName());

        vertx.createHttpServer().requestHandler(this::reqHandler)
                .listen(port).onComplete(res -> {
                    if(res.succeeded()){
                        LOG.info("HttpServerVerticle started on port: {}", port);
                        startPromise.complete();
                    }else{
                        startPromise.fail(res.cause());
                    }
                });
    }

    private void reqHandler(HttpServerRequest request){
        String reqPath = request.path();
        if ("/".equals(reqPath)){
            request.response().sendFile("index.html");
        }else if("/sse".equals(reqPath)){
            doSse(request);
        }else {
            request.response().setStatusCode(404).end();
        }

    }

    private void doSse(HttpServerRequest request) {
        HttpServerResponse response = request.response();
        response.putHeader("content-type", "text/event-stream")
                .putHeader("cache-control", "no-cache")
                .setChunked(true);
        MessageConsumer<JsonObject> consumer = vertx.eventBus().<JsonObject>consumer(Topic.SENSOR_UPDATE_ADDRESS);

        consumer.handler(msg -> {
           response.write("event: update\n");
           response.write("data: " + msg.body().encodePrettily() + "\n\n");
        });


        TimeoutStream ticks = vertx.periodicStream(1000);
        ticks.handler(id -> {
            vertx.eventBus().<JsonObject>request("sensor.average","",reply ->{
                if (reply.succeeded()) {
                    response.write("event: average\n");
                    response.write("data: " + reply.result().body().encodePrettily() + "\n\n");
                }
            });
        });

        response.endHandler(v ->{
           consumer.unregister();
           ticks.cancel();
        });


    }
}
