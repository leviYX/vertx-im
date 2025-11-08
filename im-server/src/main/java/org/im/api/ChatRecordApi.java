package org.im.api;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.redis.client.Redis;
import org.im.config.EsPool;
import org.im.manager.SessionManager;
import org.im.utils.CustomStringUtils;
import org.im.utils.FutureUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ChatRecordApi {

    private final static Logger LOG = LoggerFactory.getLogger(ChatRecordApi.class);

    private static final String RECORD_SEARCH_API = "/im/chatRecord/search";

    private static ElasticsearchAsyncClient esClient;


    public static void attach(Router parent, SessionManager sessionManager, Redis redis) {
        // 初始化esClient
        esClient = EsPool.getEsAsyncClient();

        parent.post(RECORD_SEARCH_API)
                .handler(BodyHandler.create())
                .handler(context -> {
                    JsonObject bodyAsJson = context.getBodyAsJson();
                    String from = bodyAsJson.getString("from");
                    String to = bodyAsJson.getString("to");
                    String startChatTime = bodyAsJson.getString("startChatTime");
                    String endChatTime = bodyAsJson.getString("endChatTime");
                    String search = bodyAsJson.getString("c");

                    // todo
                    // 当前用户是否注册用户
                    // 当前用户是否登陆
                    // 当前用户搜的from是不是自己
                    // 当前用户搜的to是不是自己的好友

                    BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
                    if (CustomStringUtils.isNotEmpty(from)) {
                        boolQueryBuilder.filter(f -> f.term(t -> t.field("from").value(from)));
                    }
                    if (CustomStringUtils.isNotEmpty(to)) {
                        boolQueryBuilder.filter(f -> f.term(t -> t.field("to").value(to)));
                    }

                    if (CustomStringUtils.isNotEmpty(startChatTime) && CustomStringUtils.isNotEmpty(endChatTime)) {
                        boolQueryBuilder.filter(f -> f.range(r -> r.date( d -> d.field("chat_time").gte(startChatTime).lte(endChatTime))));
                    }

                    // 分词字段
                    if (CustomStringUtils.isNotEmpty(search)) {
                        boolQueryBuilder.must(  f -> f.matchPhrase(m -> m.field("chat_content").slop(2).query(search)));
                    }

                    // 构建排序，按照聊天时间进行排序,正序排序
                    List<SortOptions> sortOptions = List.of(
                            SortOptions.of(so -> so.field(f -> f.field("chat_time").order(SortOrder.Asc)))
                    );

                    Query query = boolQueryBuilder.build()._toQuery();
                    SearchRequest searchRequest = SearchRequest.of(builder -> builder
                            .index("chat_record")
                            .query(query)
                            .sort(sortOptions)
                            .from(0)
                            .size(10));


                    CompletableFuture<SearchResponse<Map>> searchResponseCompletableFuture = esClient.search(searchRequest, Map.class);
                    Future<SearchResponse<Map>> future = FutureUtils.toFuture(searchResponseCompletableFuture);
                    future
                            .onSuccess(searchResponse -> {
                                List<Hit<Map>> hits = searchResponse.hits().hits();
                                JsonArray jsonArray = new JsonArray();
                                for (Hit<Map> hit : hits) {
                                    // 做一次反序列化
                                    jsonArray.add(hit.source());
                                }
                                // todo 包装了一层json,待优化
                                context.response().setStatusCode(200).end(jsonArray.toBuffer());
                            })
                            .onFailure(throwable -> {
                                context.response().setStatusCode(500).end(JsonObject.mapFrom(throwable).encode());
                            });
                });
    }
}
