package org.im.utils;

import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import jakarta.json.stream.JsonGenerator;

import java.io.StringWriter;

public class EsUtils {

    private static final JacksonJsonpMapper JACKSON_JSONP_MAPPER = new JacksonJsonpMapper();

    public static String getDslByRequest(SearchRequest searchRequest) {
        StringWriter dsl = new StringWriter();
        try (JsonGenerator generator = JACKSON_JSONP_MAPPER.jsonProvider().createGenerator(dsl)){
            searchRequest.serialize(generator, JACKSON_JSONP_MAPPER);
            generator.flush();
            return dsl.toString();
        }
    }
}
