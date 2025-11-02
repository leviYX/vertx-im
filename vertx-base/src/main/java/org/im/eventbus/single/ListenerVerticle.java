package org.im.eventbus.single;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import org.im.eventbus.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 监听事件总线温度数据更新Verticle
 * 该Verticle会订阅事件总线的温度数据更新地址，当有温度数据更新时，会打印到日志中
 */
public class ListenerVerticle extends AbstractVerticle {

    private final Logger LOG = LoggerFactory.getLogger(ListenerVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        LOG.info("ListenerVerticle started on thread: {}", Thread.currentThread().getName());
        MessageConsumer<JsonObject> consumer = vertx.eventBus()
                .<JsonObject>consumer(Topic.SENSOR_UPDATE_ADDRESS, msg -> {
                    JsonObject body = msg.body();
                    String sensorId = body.getString("sensorId");
                    Double temperature = body.getDouble("temperature");
                    LOG.info("Received temperature update: sensorId={}, temperature={}", sensorId, temperature);
                });

        startPromise.complete();
    }
}
