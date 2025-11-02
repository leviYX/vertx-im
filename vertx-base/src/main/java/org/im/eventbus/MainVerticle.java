package org.im.eventbus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class MainVerticle extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MainVerticle());
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx.deployVerticle(new HeatVerticle()).onComplete(res -> {
            if(res.succeeded()){
                vertx.deployVerticle(new ListenerVerticle());
                vertx.deployVerticle(new SensorDataVerticle());
                JsonObject config = new JsonObject();
                config.put("port", 8080);
                DeploymentOptions options = new DeploymentOptions().setConfig(config);
                vertx.deployVerticle(new HttpServerVerticle(),options);
            }else{
                startPromise.fail(res.cause());
            }
        });
    }
}
