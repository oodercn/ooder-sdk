package net.ooder.api.core;

/**
 * 可标识接口
 * 实现此接口的类具有唯一标识
 */
public interface Identifiable {
    
    /**
     * 获取唯一标识
     * @return 标识符
     */
    String getId();
    
    /**
     * 设置唯一标识
     * @param id 标识符
     */
    void setId(String id);
}
