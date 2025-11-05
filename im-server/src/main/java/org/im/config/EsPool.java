package org.im.config;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

public class EsPool {
    private static volatile ElasticsearchClient SYNC_CLIENT;
    private static volatile ElasticsearchAsyncClient ASYNC_CLIENT;


    public static ElasticsearchClient getEsClient() {
        if(SYNC_CLIENT == null) {
            synchronized (EsPool.class) {
                HttpHost host = new HttpHost("localhost", 9200, "http");
                RestClientBuilder clientBuilder = RestClient.builder(host).setRequestConfigCallback(c -> c
                        .setConnectionRequestTimeout(5000)
                        .setConnectTimeout(5000)
                ).setHttpClientConfigCallback(c -> c
                        .setMaxConnTotal(1000)
                        .setMaxConnPerRoute(500)
                        .setDefaultIOReactorConfig(
                                IOReactorConfig.custom()
                                        .setSoKeepAlive(true)
                                        .setTcpNoDelay(true).build()));
                SYNC_CLIENT = new ElasticsearchClient(new RestClientTransport(clientBuilder.build(),new JacksonJsonpMapper()));
            }
        }
        return SYNC_CLIENT;
    }

    public static ElasticsearchAsyncClient getEsAsyncClient() {
        if(ASYNC_CLIENT == null) {
            synchronized (EsPool.class) {
                HttpHost host = new HttpHost("localhost", 9200, "http");
                RestClientBuilder clientBuilder = RestClient.builder(host).setRequestConfigCallback(c -> c
                        .setConnectionRequestTimeout(5000)
                        .setConnectTimeout(5000)
                ).setHttpClientConfigCallback(c -> c
                        .setMaxConnTotal(1000)
                        .setMaxConnPerRoute(500)
                        .setDefaultIOReactorConfig(
                                IOReactorConfig.custom()
                                        .setSoKeepAlive(true)
                                        .setTcpNoDelay(true).build()));
                ASYNC_CLIENT = new ElasticsearchAsyncClient(new RestClientTransport(clientBuilder.build(),new JacksonJsonpMapper()));
            }
        }
        return ASYNC_CLIENT;
    }

}
