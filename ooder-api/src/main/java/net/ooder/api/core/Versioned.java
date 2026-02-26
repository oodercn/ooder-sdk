package net.ooder.api.core;

/**
 * 可版本化接口
 * 实现此接口的类具有版本信息
 */
public interface Versioned {
    
    /**
     * 获取版本号
     * @return 版本号，如 "1.0.0"
     */
    String getVersion();
    
    /**
     * 设置版本号
     * @param version 版本号
     */
    void setVersion(String version);
}
