package org.im.utils;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

import java.util.concurrent.CompletionStage;

public class FutureUtils {

    public static <T> Future<T> toFuture(CompletionStage<T> cs) {
        return Future.fromCompletionStage(cs, Vertx.currentContext());
    }
}
