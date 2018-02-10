package org.smartRpc.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 使用jackson解决对象的序列化问题
 */
public class SerializationUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerializationUtil.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static <T> String toJson(T obj){
        String json = null;

        try {
            json = OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LOGGER.error(" convert pojo to json failure");
        }
        return  json;
    }

    public static <T> T fromJson(byte[] json,Class<?> tclass){
        T obj = null;
        try {
            obj = (T) OBJECT_MAPPER.readValue(json,tclass);
        } catch (IOException e) {
            LOGGER.error("convert json to pojo failure");
        }
        return obj;
    }
}
