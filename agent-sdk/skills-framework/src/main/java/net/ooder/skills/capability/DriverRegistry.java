package net.ooder.skills.capability;

import java.util.Map;
import java.util.Set;

/**
 * 驱动注册接口
 * 
 * <p>用于 Skills 向 Engine 注册驱动实现</p>
 * 
 * <h3>使用示例：</h3>
 * <pre>
 * // Skills 实现
 * public class MinioVfsDriver implements AtomicCapability {
 *     
 *     @Override
 *     public CapabilityAddress getAddress() {
 *         return CapabilityAddress.VFS_MINIO;
 *     }
 *     
 *     @Override
 *     public Set&lt;String&gt; getSupportedOperations() {
 *         return Set.of("upload", "download", "delete", "list");
 *     }
 *     
 *     @Override
 *     public Result execute(String operation, Map&lt;String, Object&gt; params, ContextReference contextRef) {
 *         // 执行具体操作
 *     }
 * }
 * 
 * // 注册驱动
 * driverRegistry.register(CapabilityAddress.VFS_MINIO, new MinioVfsDriver());
 * </pre>
 *
 * @author Agent-SDK Team
 * @version 2.4.0
 * @since 2.4.0
 */
public interface DriverRegistry {
    
    /**
     * 注册驱动
     * 
     * @param address 能力地址
     * @param driver 驱动实例
     */
    void register(CapabilityAddress address, AtomicCapability driver);
    
    /**
     * 注销驱动
     * 
     * @param address 能力地址
     */
    void unregister(CapabilityAddress address);
    
    /**
     * 获取驱动
     * 
     * @param address 能力地址
     * @return 驱动实例
     */
    AtomicCapability getDriver(CapabilityAddress address);
    
    /**
     * 检查是否已注册
     * 
     * @param address 能力地址
     * @return 是否已注册
     */
    boolean isRegistered(CapabilityAddress address);
    
    /**
     * 获取已注册的所有地址
     * 
     * @return 地址集合
     */
    Set<CapabilityAddress> getRegisteredAddresses();
    
    /**
     * 获取指定分类的所有驱动
     * 
     * @param category 能力分类
     * @return 驱动映射
     */
    Map<CapabilityAddress, AtomicCapability> getDriversByCategory(CapabilityCategory category);
}
