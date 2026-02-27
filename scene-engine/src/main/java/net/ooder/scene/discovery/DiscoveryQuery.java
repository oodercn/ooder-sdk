package net.ooder.scene.discovery;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 发现查询类
 * 
 * <p>封装发现查询的条件，包括查询类型、查询字符串、范围和过滤条件。</p>
 * 
 * <h3>查询条件：</h3>
 * <ul>
 *   <li>type - 查询类型（SCENE/CAPABILITY/SKILL/AGENT/PEER）</li>
 *   <li>query - 查询字符串，支持通配符 *</li>
 *   <li>scope - 发现范围</li>
 *   <li>filters - 过滤条件</li>
 * </ul>
 * 
 * <h3>使用示例：</h3>
 * <pre>
 * DiscoveryQuery query = new DiscoveryQuery(DiscoveryType.SCENE, "messaging");
 * query.setScope(DiscoveryScope.DEPARTMENT);
 * query.addFilter("category", "communication");
 * </pre>
 * 
 * @author Ooder Team
 * @version 2.3
 * @since 0.8.0
 * @see DiscoveryProvider
 * @see DiscoveryType
 */
public class DiscoveryQuery {

    /** 查询类型 */
    private DiscoveryType type;

    /** 查询字符串 */
    private String query;

    /** 发现范围 */
    private DiscoveryScope scope;

    /** 过滤条件 */
    private Map<String, Object> filters;

    /**
     * 构造器
     * 
     * @param type 查询类型
     * @param query 查询字符串
     */
    public DiscoveryQuery(DiscoveryType type, String query) {
        this.type = type;
        this.query = query;
        this.scope = DiscoveryScope.PERSONAL;
        this.filters = new ConcurrentHashMap<>();
    }

    /**
     * 获取查询类型
     * 
     * @return 查询类型
     */
    public DiscoveryType getType() {
        return type;
    }

    /**
     * 获取查询字符串
     * 
     * @return 查询字符串
     */
    public String getQuery() {
        return query;
    }

    /**
     * 获取发现范围
     * 
     * @return 发现范围
     */
    public DiscoveryScope getScope() {
        return scope;
    }

    /**
     * 设置发现范围
     * 
     * @param scope 发现范围
     */
    public void setScope(DiscoveryScope scope) {
        this.scope = scope;
    }

    /**
     * 添加过滤条件
     * 
     * @param key 条件名
     * @param value 条件值
     */
    public void addFilter(String key, Object value) {
        filters.put(key, value);
    }

    /**
     * 获取过滤条件
     * 
     * @param key 条件名
     * @return 条件值
     */
    public Object getFilter(String key) {
        return filters.get(key);
    }

    /**
     * 获取所有过滤条件
     * 
     * @return 过滤条件映射表
     */
    public Map<String, Object> getFilters() {
        return filters;
    }
}
