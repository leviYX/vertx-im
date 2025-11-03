package org.im.config;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * 配置文件加载器
 */
public class ConfigLoader {
    /**
     * 加载配置文件，读取classpath下的application.yaml文件,返回为JsonObject
     *
     * @param vertx
     * @return
     */
    public static Future<JsonObject> load(Vertx vertx){
        ConfigStoreOptions yamlStore = new ConfigStoreOptions()
                .setType("file")
                .setFormat("yaml")
                .setConfig(new JsonObject().put("path", "application.yaml"));
        ConfigRetriever retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions().addStore(yamlStore));
        return retriever.getConfig();
    }
}
