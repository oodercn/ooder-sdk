package net.ooder.scene.discovery;

import net.ooder.scene.discovery.api.DiscoveryRequest;
import net.ooder.scene.discovery.api.DiscoveryResult;

/**
 * SceneEngine 发现钩子接口
 * 
 * <p>提供发现生命周期的事件回调，允许 SceneEngine 在发现流程的关键节点介入处理。</p>
 * 
 * <h3>使用场景：</h3>
 * <ul>
 *   <li>发现前准备 - 验证权限、初始化资源</li>
 *   <li>能力发现处理 - 自定义能力过滤或增强</li>
 *   <li>场景发现处理 - 自定义场景过滤或增强</li>
 *   <li>发现后处理 - 结果聚合、缓存更新</li>
 * </ul>
 * 
 * <h3>实现示例：</h3>
 * <pre>
 * public class CustomDiscoveryHook implements SceneEngineDiscoveryHook {
 *     
 *     public void onBeforeDiscovery(DiscoveryRequest request) {
 *         // 验证用户权限
 *         validateUserPermission(request);
 *     }
 *     
 *     public void onCapabilityDiscovered(CapabilityDTO capability) {
 *         // 增强能力信息
 *         enhanceCapability(capability);
 *     }
 *     
 *     public void onSceneDiscovered(CapabilityDTO scene) {
 *         // 检查场景驱动条件
 *         checkDriverConditions(scene);
 *     }
 *     
 *     public void onAfterDiscovery(DiscoveryResult result) {
 *         // 更新本地缓存
 *         updateCache(result);
 *     }
 * }
 * </pre>
 * 
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 * @see DiscoveryService
 * @see SceneEngineIntegration
 */
public interface SceneEngineDiscoveryHook {
    
    /**
     * 发现开始前回调
     * 
     * <p>在发现流程开始前调用，可用于：</p>
     * <ul>
     *   <li>验证请求参数</li>
     *   <li>检查用户权限</li>
     *   <li>初始化必要资源</li>
     *   <li>记录审计日志</li>
     * </ul>
     *
     * @param request 发现请求，包含来源、过滤条件等信息
     * @throws DiscoveryException 如果验证失败或初始化出错，可抛出异常终止发现流程
     */
    void onBeforeDiscovery(DiscoveryRequest request);
    
    /**
     * 能力发现回调
     * 
     * <p>每当发现一个能力时调用，可用于：</p>
     * <ul>
     *   <li>能力信息增强（添加元数据、标签等）</li>
     *   <li>能力过滤（根据权限、状态等）</li>
     *   <li>能力验证（检查完整性、兼容性）</li>
     *   <li>触发能力注册</li>
     * </ul>
     *
     * @param capability 发现的能力信息
     */
    void onCapabilityDiscovered(CapabilityDTO capability);
    
    /**
     * 场景发现回调
     * 
     * <p>每当发现一个场景时调用，可用于：</p>
     * <ul>
     *   <li>场景信息增强</li>
     *   <li>驱动条件匹配检查</li>
     *   <li>场景状态验证</li>
     *   <li>触发场景注册</li>
     * </ul>
     *
     * @param scene 发现的场景信息
     */
    void onSceneDiscovered(CapabilityDTO scene);
    
    /**
     * 发现完成后回调
     * 
     * <p>在发现流程完成后调用（无论成功与否），可用于：</p>
     * <ul>
     *   <li>结果聚合和去重</li>
     *   <li>更新本地缓存</li>
     *   <li>发布发现事件</li>
     *   <li>清理临时资源</li>
     *   <li>记录发现统计</li>
     * </ul>
     *
     * @param result 发现结果，包含所有发现的技能和场景
     */
    void onAfterDiscovery(DiscoveryResult result);
    
    /**
     * 发现失败回调
     * 
     * <p>在发现流程失败时调用，可用于：</p>
     * <ul>
     *   <li>错误处理和恢复</li>
     *   <li>记录错误日志</li>
     *   <li>通知监控服务</li>
     *   <li>触发降级策略</li>
     * </ul>
     *
     * @param request 原始发现请求
     * @param error 错误信息
     */
    default void onDiscoveryFailed(DiscoveryRequest request, String error) {
        // 默认空实现，子类可选择性重写
    }
    
    /**
     * 获取钩子优先级
     * 
     * <p>优先级高的钩子先执行，默认优先级为 0。</p>
     *
     * @return 优先级数值，数值越大优先级越高
     */
    default int getPriority() {
        return 0;
    }
    
    /**
     * 是否支持指定发现源
     * 
     * <p>用于过滤钩子适用的发现源，返回 true 表示该钩子会处理此来源的发现请求。</p>
     *
     * @param source 发现源，如 "local", "github", "gitee", "git"
     * @return true 如果支持该发现源
     */
    default boolean supportsSource(String source) {
        return true; // 默认支持所有来源
    }
}
