package net.ooder.scene.discovery;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 发现提供者接口
 * 
 * <p>定义能力发现提供者的标准接口，所有具体的发现方式（UDP、mDNS、SkillCenter等）都需要实现此接口。</p>
 * 
 * <h3>核心职责：</h3>
 * <ul>
 *   <li>生命周期管理 - 初始化、启动、停止</li>
 *   <li>发现查询 - 根据查询条件执行发现</li>
 *   <li>优先级管理 - 提供优先级用于排序</li>
 *   <li>范围适配 - 判断是否适用于指定发现范围</li>
 * </ul>
 * 
 * <h3>实现示例：</h3>
 * <pre>
 * public class UdpDiscoveryProvider implements DiscoveryProvider {
 *     @Override
 *     public String getProviderName() {
 *         return "UDP-BROADCAST";
 *     }
 *     
 *     @Override
 *     public CompletableFuture<List<DiscoveredItem>> discover(DiscoveryQuery query) {
 *         // 执行UDP广播发现
 *     }
 * }
 * </pre>
 * 
 * @author Ooder Team
 * @version 2.3
 * @since 0.8.0
 * @see CapabilityDiscoveryService
 * @see DiscoveryQuery
 * @see DiscoveredItem
 */
public interface DiscoveryProvider {

    /**
     * 获取提供者名称
     * 
     * <p>名称用于标识和日志记录，应保证唯一性。</p>
     * 
     * @return 提供者名称
     */
    String getProviderName();

    /**
     * 初始化
     * 
     * <p>在启动前进行初始化配置，如设置连接参数、加载配置等。</p>
     * 
     * @param config 发现配置
     */
    void initialize(DiscoveryConfig config);

    /**
     * 启动
     * 
     * <p>启动发现服务，如打开端口、连接服务器等。</p>
     */
    void start();

    /**
     * 停止
     * 
     * <p>停止发现服务，释放资源，如关闭端口、断开连接等。</p>
     */
    void stop();

    /**
     * 是否运行中
     * 
     * @return true 运行中，false 已停止
     */
    boolean isRunning();

    /**
     * 执行发现查询
     * 
     * <p>根据查询条件执行发现，返回异步结果。</p>
     * 
     * @param query 发现查询条件
     * @return 异步发现结果
     */
    CompletableFuture<List<DiscoveredItem>> discover(DiscoveryQuery query);

    /**
     * 获取优先级
     * 
     * <p>优先级用于排序，数值越大优先级越高。高优先级的提供者会先被查询。</p>
     * 
     * @return 优先级（建议范围：0-100）
     */
    int getPriority();

    /**
     * 是否适用于指定范围
     * 
     * <p>判断此提供者是否适用于指定的发现范围。</p>
     * 
     * @param scope 发现范围
     * @return true 适用，false 不适用
     */
    boolean isApplicable(DiscoveryScope scope);
}
