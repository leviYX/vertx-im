package org.im.eventbus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.UUID;

/**
 * 模拟温度传感器Verticle
 * 该Verticle会定时向事件总线发布温度数据，数据格式为：
 * {
 *     "sensorId": "唯一传感器ID",
 *     "temperature": 23.5
 * }
 */
public class HeatVerticle extends AbstractVerticle {

    private final Logger LOG = LoggerFactory.getLogger(HeatVerticle.class);

    // 生成温度随机数
    private Random random = new Random();

    // 使用uuid来模拟传感器ID
    private final String sensorId = UUID.randomUUID().toString();

    // 保存当前的温度
    private double temperature = 37.0;

    // 模拟温度传感器在每5-6秒内发布一次温度数据,这里为base interval, 实际发布时间会随机偏移1秒(Random控制)
    private static final int HEAT_INTERVAL = 5000;

    @Override
    public void start(Promise<Void> startPromise) {
        LOG.info("HeatVerticle started on thread: {}", Thread.currentThread().getName());
        updateTemperature();
        startPromise.complete();
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        LOG.info("HeatVerticle stopped on thread: {}", Thread.currentThread().getName());
        // close  todo
        stopPromise.complete();
    }

    private void updateTemperature() {
        // 构建温度模拟数据
        JsonObject payload = new JsonObject().put("sensorId", sensorId).put("temperature", temperature);
        // 温度数据发送到事件总线总，让其他Verticle可以订阅并处理,
        vertx.eventBus().publish(Topic.SENSOR_UPDATE_ADDRESS,payload);
        // 模拟温度传感器在每5-6秒内发布一次温度数据
        vertx.setTimer(HEAT_INTERVAL + random.nextInt(1000),id -> {
            // 模拟温度变化，这里可能出现负数，导致温度下降，注意即可
            temperature += random.nextDouble();
            LOG.info("HeatVerticle update temperature: {}", temperature);
            // 定时器中递归调用，继续模拟温度变化，定时发布数据上去
            updateTemperature();
        });
    }
}
