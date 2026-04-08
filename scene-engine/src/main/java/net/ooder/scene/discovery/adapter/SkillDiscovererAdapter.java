package net.ooder.scene.discovery.adapter;

import net.ooder.scene.discovery.CapabilityDTO;
import net.ooder.scene.discovery.api.DiscoveryRequest;
import net.ooder.scene.discovery.api.DiscoveryResult;

import java.util.concurrent.CompletableFuture;

/**
 * 技能发现器适配器接口
 *
 * <p>统一的技能发现器接口，解决 skills-framework 和 skill-common 两套接口不兼容的问题。</p>
 *
 * <h3>设计目标：</h3>
 * <ul>
 *   <li>统一发现服务接口，消除冗余</li>
 *   <li>提供一致的发现服务 API</li>
 *   <li>支持多种发现方法（本地、Gitee、GitHub、P2P等）</li>
 *   <li>支持单个技能发现和批量发现</li>
 * </ul>
 *
 * <h3>实现示例：</h3>
 * <pre>
 * &#064;Component
 * public class GiteeSkillDiscovererAdapter implements SkillDiscovererAdapter {
 *     &#064;Override
 *     public CompletableFuture&lt;DiscoveryResult&gt; discover(DiscoveryRequest request) {
 *         // 实现 Gitee 发现逻辑
 *     }
 * }
 * </pre>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 * @see DiscoveryMethod
 * @see DiscoveryRequest
 * @see DiscoveryResult
 */
public interface SkillDiscovererAdapter {

    /**
     * 发现技能
     *
     * <p>根据请求参数执行技能发现，返回异步结果。</p>
     *
     * @param request 发现请求，包含来源、分类、标签等过滤条件
     * @return 发现结果，包含技能列表和元数据
     */
    CompletableFuture<DiscoveryResult> discover(DiscoveryRequest request);

    /**
     * 发现单个技能
     *
     * <p>根据技能ID查找特定技能的详细信息。</p>
     *
     * @param skillId 技能ID
     * @return 技能详情，如果未找到返回 null
     */
    CompletableFuture<CapabilityDTO> discoverOne(String skillId);

    /**
     * 获取发现方法
     *
     * <p>标识此发现器使用的发现方法类型。</p>
     *
     * @return 发现方法枚举
     */
    DiscoveryMethod getMethod();

    /**
     * 检查发现器是否可用
     *
     * <p>用于判断发现器是否已正确配置并可以执行发现操作。</p>
     *
     * <h3>检查条件示例：</h3>
     * <ul>
     *   <li>Gitee 发现器：检查 token 是否配置</li>
     *   <li>GitHub 发现器：检查 token 是否配置</li>
     *   <li>本地发现器：检查目录是否存在</li>
     *   <li>P2P 发现器：检查网络是否可用</li>
     * </ul>
     *
     * @return true 可用，false 不可用
     */
    boolean isAvailable();

    /**
     * 获取发现器优先级
     *
     * <p>用于多发现器场景下的排序，数值越大优先级越高。</p>
     *
     * @return 优先级（默认 0，建议范围 0-100）
     */
    default int getPriority() {
        return 0;
    }

    /**
     * 获取发现器名称
     *
     * <p>用于日志记录和监控。</p>
     *
     * @return 发现器名称
     */
    default String getName() {
        return getMethod().getDisplayName();
    }
}
