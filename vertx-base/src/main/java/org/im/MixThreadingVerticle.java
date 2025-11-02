package org.im;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MixThreadingVerticle extends AbstractVerticle {

    private final Logger LOG = LoggerFactory.getLogger(MixThreadingVerticle.class);

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MixThreadingVerticle());
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        // 获取上下文
        Context context = vertx.getOrCreateContext();
        Thread thread = new Thread(() -> {
            // 以下代码执行在eventloop中
            context.runOnContext(v -> {
                LOG.info("MixThreadingVerticle started on thread: {}", Thread.currentThread().getName());
            });
        });
        thread.start();
    }
}
