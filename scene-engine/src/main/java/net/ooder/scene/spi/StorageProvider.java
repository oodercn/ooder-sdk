package net.ooder.scene.spi;

import java.util.Map;
import java.util.Optional;

/**
 * 存储提供者 SPI
 *
 * <p>核心存储接口，所有驱动必须实现</p>
 *
 * <p>实现要求：</p>
 * <ul>
 *   <li>Tiny: 文件存储</li>
 *   <li>Small: JDBC 数据库</li>
 *   <li>Enterprise: 分布式存储</li>
 * </ul>
 *
 * @author Ooder Team
 * @since 3.0.0
 */
public interface StorageProvider {

    /**
     * 获取数据
     *
     * @param collection 集合名称
     * @param key 键
     * @param type 值类型
     * @return 值
     */
    <T> Optional<T> get(String collection, String key, Class<T> type);

    /**
     * 存储数据
     *
     * @param collection 集合名称
     * @param key 键
     * @param value 值
     */
    <T> void put(String collection, String key, T value);

    /**
     * 删除数据
     *
     * @param collection 集合名称
     * @param key 键
     */
    void remove(String collection, String key);

    /**
     * 获取集合所有数据
     *
     * @param collection 集合名称
     * @param type 值类型
     * @return 数据映射
     */
    <T> Map<String, T> getAll(String collection, Class<T> type);

    /**
     * 检查是否存在
     *
     * @param collection 集合名称
     * @param key 键
     * @return 是否存在
     */
    boolean exists(String collection, String key);

    /**
     * 清空集合
     *
     * @param collection 集合名称
     */
    void clear(String collection);

    /**
     * 获取提供者名称
     *
     * @return 名称
     */
    default String getProviderName() {
        return this.getClass().getSimpleName();
    }

    /**
     * 获取提供者类型
     *
     * @return 类型: tiny, small, enterprise, fallback
     */
    String getProviderType();
}
