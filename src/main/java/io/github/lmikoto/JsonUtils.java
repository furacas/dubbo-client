package io.github.lmikoto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

/**
 * @author liuyang
 * 2021/2/4 2:15 下午
 */
public class JsonUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @SneakyThrows
    public static String toPrettyJson(Object o){
        return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(o);
    }

    @SneakyThrows
    public static <T> T fromJson(String json, Class<T> clazz){
        return MAPPER.readValue(json, clazz);
    }

    @SneakyThrows
    public static <T> T fromJson(String json, TypeReference<T> typeReference){
        return MAPPER.readValue(json, typeReference);
    }

    public static abstract class TypeReference<T> extends com.fasterxml.jackson.core.type.TypeReference<T> {}
}
