package net.ooder.skills.config;

import java.util.*;

/**
 * 配置节点
 *
 * <p>表示配置树中的一个节点，支持嵌套结构和点号路径访问</p>
 *
 * @author Agent-SDK Team
 * @version 2.4.0
 * @since 2.4.0
 */
public class ConfigNode {

    private final Map<String, Object> data;

    public ConfigNode() {
        this.data = new LinkedHashMap<>();
    }

    public ConfigNode(Map<String, Object> data) {
        this.data = data != null ? new LinkedHashMap<>(data) : new LinkedHashMap<>();
    }

    /**
     * 获取原始数据
     */
    public Map<String, Object> getData() {
        return Collections.unmodifiableMap(data);
    }

    /**
     * 获取值
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        if (key == null || key.isEmpty()) {
            return null;
        }

        // 处理点号路径
        if (key.contains(".")) {
            return getNested(key);
        }

        return data.get(key);
    }

    /**
     * 获取字符串值
     */
    public String getString(String key) {
        Object value = get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * 获取字符串值，带默认值
     */
    public String getString(String key, String defaultValue) {
        String value = getString(key);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取整数值
     */
    public Integer getInt(String key) {
        Object value = get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 获取整数值，带默认值
     */
    public int getInt(String key, int defaultValue) {
        Integer value = getInt(key);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取长整数值
     */
    public Long getLong(String key) {
        Object value = get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 获取布尔值
     */
    public Boolean getBoolean(String key) {
        Object value = get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return null;
    }

    /**
     * 获取布尔值，带默认值
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        Boolean value = getBoolean(key);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取嵌套配置节点
     */
    @SuppressWarnings("unchecked")
    public ConfigNode getNode(String key) {
        Object value = get(key);
        if (value instanceof Map) {
            return new ConfigNode((Map<String, Object>) value);
        }
        return null;
    }

    /**
     * 获取嵌套 Map
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getNested(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }

        String[] keys = path.split("\\.");
        Object current = data;

        for (String key : keys) {
            if (current instanceof Map) {
                current = ((Map<String, Object>) current).get(key);
                if (current == null) {
                    return null;
                }
            } else {
                return null;
            }
        }

        if (current instanceof Map) {
            return (Map<String, Object>) current;
        }

        return null;
    }

    /**
     * 设置值
     */
    public void put(String key, Object value) {
        if (key == null || key.isEmpty()) {
            return;
        }

        // 处理点号路径
        if (key.contains(".")) {
            putNested(key, value);
            return;
        }

        data.put(key, value);
    }

    /**
     * 设置嵌套值
     */
    @SuppressWarnings("unchecked")
    public void putNested(String path, Object value) {
        if (path == null || path.isEmpty()) {
            return;
        }

        String[] keys = path.split("\\.");
        Map<String, Object> current = data;

        for (int i = 0; i < keys.length - 1; i++) {
            String key = keys[i];
            Object next = current.get(key);

            if (!(next instanceof Map)) {
                next = new LinkedHashMap<String, Object>();
                current.put(key, next);
            }

            current = (Map<String, Object>) next;
        }

        current.put(keys[keys.length - 1], value);
    }

    /**
     * 检查是否包含键
     */
    public boolean containsKey(String key) {
        return get(key) != null;
    }

    /**
     * 获取所有键
     */
    public Set<String> keySet() {
        return data.keySet();
    }

    /**
     * 是否为空
     */
    public boolean isEmpty() {
        return data.isEmpty();
    }

    /**
     * 合并另一个配置节点
     *
     * @param other 另一个配置节点
     * @return 新的合并后的配置节点
     */
    public ConfigNode merge(ConfigNode other) {
        ConfigNode result = new ConfigNode();
        result.data.putAll(this.data);

        if (other != null) {
            deepMerge(result.data, other.data);
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private void deepMerge(Map<String, Object> target, Map<String, Object> source) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map && target.get(key) instanceof Map) {
                // 递归合并 Map
                deepMerge(
                    (Map<String, Object>) target.get(key),
                    (Map<String, Object>) value
                );
            } else {
                // 直接覆盖
                target.put(key, value);
            }
        }
    }

    /**
     * 转换为 Map
     */
    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(data);
    }

    @Override
    public String toString() {
        return "ConfigNode{" + data + '}';
    }
}
