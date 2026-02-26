package net.ooder.util.collection;

import java.util.Collection;
import java.util.Map;

/**
 * 集合工具类
 */
public final class CollectionUtils {
    
    private CollectionUtils() {
        // 禁止实例化
    }
    
    /**
     * 判断集合是否为空
     * @param collection 集合
     * @return 是否为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
    
    /**
     * 判断集合是否不为空
     * @param collection 集合
     * @return 是否不为空
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }
    
    /**
     * 判断 Map 是否为空
     * @param map Map
     * @return 是否为空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
    
    /**
     * 判断 Map 是否不为空
     * @param map Map
     * @return 是否不为空
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }
    
    /**
     * 获取集合大小，null 返回 0
     * @param collection 集合
     * @return 大小
     */
    public static int size(Collection<?> collection) {
        return collection == null ? 0 : collection.size();
    }
    
    /**
     * 获取 Map 大小，null 返回 0
     * @param map Map
     * @return 大小
     */
    public static int size(Map<?, ?> map) {
        return map == null ? 0 : map.size();
    }
}
