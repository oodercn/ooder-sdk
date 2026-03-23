package net.ooder.sdk.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.alibaba.fastjson2.JSONWriter;

import java.util.List;
import java.util.Map;

/**
 * JSON 工具类
 * 统一使用 Fastjson 进行 JSON 处理
 *
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class JsonUtils {

    private JsonUtils() {
        // 工具类，禁止实例化
    }

    /**
     * 将对象转换为 JSON 字符串
     *
     * @param obj 对象
     * @return JSON 字符串
     */
    public static String toJson(Object obj) {
        return JSON.toJSONString(obj, JSONWriter.Feature.WriteMapNullValue);
    }

    /**
     * 将对象转换为格式化的 JSON 字符串
     *
     * @param obj 对象
     * @return 格式化的 JSON 字符串
     */
    public static String toJsonPretty(Object obj) {
        return JSON.toJSONString(obj, JSONWriter.Feature.WriteMapNullValue, JSONWriter.Feature.PrettyFormat);
    }

    /**
     * 将 JSON 字符串转换为对象
     *
     * @param json  JSON 字符串
     * @param clazz 目标类
     * @param <T>   泛型
     * @return 对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    /**
     * 将 JSON 字符串转换为对象（支持泛型）
     *
     * @param json JSON 字符串
     * @param type 类型引用
     * @param <T>  泛型
     * @return 对象
     */
    public static <T> T fromJson(String json, TypeReference<T> type) {
        return JSON.parseObject(json, type);
    }

    /**
     * 将 JSON 字符串转换为 Map
     *
     * @param json JSON 字符串
     * @return Map
     */
    public static Map<String, Object> toMap(String json) {
        return JSON.parseObject(json, new TypeReference<Map<String, Object>>() {});
    }

    /**
     * 将 JSON 字符串转换为 List
     *
     * @param json  JSON 字符串
     * @param clazz 元素类
     * @param <T>   泛型
     * @return List
     */
    public static <T> List<T> toList(String json, Class<T> clazz) {
        return JSON.parseArray(json, clazz);
    }

    /**
     * 将对象转换为 JSONObject
     *
     * @param obj 对象
     * @return JSONObject
     */
    public static JSONObject toJsonObject(Object obj) {
        if (obj instanceof String) {
            return JSON.parseObject((String) obj);
        }
        return (JSONObject) JSON.toJSON(obj);
    }

    /**
     * 将对象转换为 JSONArray
     *
     * @param obj 对象
     * @return JSONArray
     */
    public static JSONArray toJsonArray(Object obj) {
        if (obj instanceof String) {
            return JSON.parseArray((String) obj);
        }
        return (JSONArray) JSON.toJSON(obj);
    }

    /**
     * 从 JSON 字符串中获取指定字段的值
     *
     * @param json      JSON 字符串
     * @param fieldName 字段名
     * @return 字段值
     */
    public static Object getField(String json, String fieldName) {
        JSONObject jsonObject = JSON.parseObject(json);
        return jsonObject.get(fieldName);
    }

    /**
     * 从 JSON 字符串中获取指定字段的字符串值
     *
     * @param json      JSON 字符串
     * @param fieldName 字段名
     * @return 字符串值
     */
    public static String getStringField(String json, String fieldName) {
        JSONObject jsonObject = JSON.parseObject(json);
        return jsonObject.getString(fieldName);
    }

    /**
     * 验证字符串是否为有效的 JSON
     *
     * @param json 字符串
     * @return 是否有效
     */
    public static boolean isValidJson(String json) {
        try {
            JSON.parse(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
