package com.common.core.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class JsonUtil {
    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper() {
            {
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            }
        };
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE)
        );

        mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        mapper.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);
    }

    public static String generateClassToJson(Object obj) {
        if (obj == null) {
            return null;
        }
        String json = null;
        try {
            json = mapper.writeValueAsString(obj);
        } catch (IOException e) {
            log.error(String.format("Parse class to string - errorMsg:%s", e.getMessage()));
        }
        return json;
    }


    public static <T> T generateJsonToClass(String jsonData, TypeReference valueTypeRef) {
        if (jsonData == null) {
            return null;
        }
        T object = null;
        try {
            object = mapper.readValue(jsonData, valueTypeRef);
        } catch (IOException e) {
            log.error(String.format("Parse string to class - errorMsg:%s", e.getMessage()));
        }
        return object;
    }

    public static <T> T generateJsonToClass(String jsonData, Class<T> valueTypeRef) {
        if (jsonData == null) {
            return null;
        }
        T object = null;
        try {
            object = mapper.readValue(jsonData, valueTypeRef);
        } catch (IOException e) {
            log.error(String.format("Parse string to class - errorMsg:%s", e.getMessage()));
        }
        return object;
    }
}
