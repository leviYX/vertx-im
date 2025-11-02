package org.im.eventbus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.stream.Collectors;

public class SensorDataVerticle extends AbstractVerticle {

    private final Logger LOG = LoggerFactory.getLogger(SensorDataVerticle.class);

    private final HashMap<String,Double> lastData = new HashMap<>();

    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        vertx.eventBus().consumer(Topic.SENSOR_UPDATE_ADDRESS,this::update);
        vertx.eventBus().consumer(Topic.SENSOR_AVERAGE_ADDRESS,this::average);

        startPromise.complete();
    }

    private void update(Message<JsonObject> message){
        JsonObject body = message.body();
        String sensorId = body.getString("sensorId");
        Double temperature = body.getDouble("temperature");
        lastData.put(sensorId,temperature);
        LOG.info("Received temperature update: sensorId={}, temperature={}", sensorId, temperature);
        message.reply("SensorDataVerticle update success");
    }

    private void average(Message<JsonObject> message){
        double average = lastData.values().stream().collect(Collectors.averagingDouble(Double::doubleValue));
        JsonObject ack = new JsonObject();
        ack.put("average",average);
        ack.put("desc","SensorDataVerticle average temperature");
        message.reply(ack);
    }
}
