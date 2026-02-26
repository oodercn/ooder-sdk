package net.ooder.sdk.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.Map;

public final class JsonUtils {
    
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    
    private JsonUtils() {
    }
    
    public static String toJson(Object obj) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(obj);
    }
    
    public static String toJsonQuietly(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert to JSON", e);
        }
    }
    
    public static <T> T fromJson(String json, Class<T> clazz) throws IOException {
        return OBJECT_MAPPER.readValue(json, clazz);
    }
    
    public static <T> T fromJsonQuietly(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON", e);
        }
    }
    
    public static Map<String, Object> toMap(String json) throws IOException {
        return OBJECT_MAPPER.readValue(json, Map.class);
    }
    
    public static Map<String, Object> toMapQuietly(String json) {
        try {
            return OBJECT_MAPPER.readValue(json, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON to map", e);
        }
    }
    
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER.copy();
    }
}
