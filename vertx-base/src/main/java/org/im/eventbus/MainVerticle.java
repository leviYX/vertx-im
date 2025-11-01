package org.im.eventbus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

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
            }else{
                startPromise.fail(res.cause());
            }
        });
    }
}
