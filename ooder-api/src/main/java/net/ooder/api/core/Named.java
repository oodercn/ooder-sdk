package net.ooder.api.core;

/**
 * 可命名接口
 * 实现此接口的类具有名称
 */
public interface Named {
    
    /**
     * 获取名称
     * @return 名称
     */
    String getName();
    
    /**
     * 设置名称
     * @param name 名称
     */
    void setName(String name);
}
