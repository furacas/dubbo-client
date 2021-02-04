package io.github.lmikoto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

/**
 * @author liuyang
 * 2021/2/4 2:15 下午
 */
public class JsonUtils {
    private static ObjectMapper mapper = new ObjectMapper();

    @SneakyThrows
    public static String toPrettyJson(Object o){
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
    }
}
