package net.ooder.scene.discovery;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 能力发现服务接口
 * 
 * <p>提供统一的能力发现入口，屏蔽底层多种发现方式的差异，支持场景、能力、技能的统一发现。</p>
 * 
 * <h3>核心功能：</h3>
 * <ul>
 *   <li>场景发现 - 按分类或关键词搜索场景</li>
 *   <li>能力发现 - 按分类或关键词搜索能力</li>
 *   <li>技能发现 - 获取场景/能力对应的可用技能</li>
 *   <li>发现源管理 - 注册/注销发现提供者</li>
 * </ul>
 * 
 * <h3>发现范围：</h3>
 * <ul>
 *   <li>PERSONAL - 个人网络</li>
 *   <li>DEPARTMENT - 部门分享</li>
 *   <li>COMPANY - 公司管理</li>
 *   <li>PUBLIC - 公共社区</li>
 * </ul>
 * 
 * @author Ooder Team
 * @version 2.3
 * @since 0.8.0
 */
public interface CapabilityDiscoveryService {

    /**
     * 同步所有索引
     * 
     * @return 同步结果，包含场景数、能力数、技能数
     */
    CompletableFuture<SyncResult> syncAllIndexes();

    /**
     * 按分类列出场景
     * 
     * @param category 场景分类，null表示全部
     * @return 场景列表
     */
    CompletableFuture<List<DiscoveredItem>> listScenes(String category);

    /**
     * 搜索场景
     * 
     * @param query 搜索关键词
     * @return 匹配的场景列表
     */
    CompletableFuture<List<DiscoveredItem>> searchScenes(String query);

    /**
     * 获取场景详情
     * 
     * @param sceneId 场景ID
     * @return 场景详情
     */
    CompletableFuture<SceneDetail> getSceneDetail(String sceneId);

    /**
     * 获取场景可用技能
     * 
     * @param sceneId 场景ID
     * @return 可用技能列表
     */
    CompletableFuture<List<DiscoveredItem>> getAvailableSkills(String sceneId);

    /**
     * 按分类列出能力
     * 
     * @param category 能力分类，null表示全部
     * @return 能力列表
     */
    CompletableFuture<List<DiscoveredItem>> listCapabilities(String category);

    /**
     * 搜索能力
     * 
     * @param query 搜索关键词
     * @return 匹配的能力列表
     */
    CompletableFuture<List<DiscoveredItem>> searchCapabilities(String query);

    /**
     * 获取能力详情
     * 
     * @param capId 能力ID
     * @return 能力详情
     */
    CompletableFuture<CapabilityDetail> getCapabilityDetail(String capId);

    /**
     * 获取能力的可用技能
     * 
     * @param capId 能力ID
     * @return 可用技能列表
     */
    CompletableFuture<List<DiscoveredItem>> getAvailableSkillsForCapability(String capId);

    /**
     * 注册发现提供者
     * 
     * @param provider 发现提供者
     */
    void registerProvider(DiscoveryProvider provider);

    /**
     * 注销发现提供者
     * 
     * @param providerName 提供者名称
     */
    void unregisterProvider(String providerName);

    /**
     * 设置发现范围
     * 
     * @param scope 发现范围
     */
    void setDiscoveryScope(DiscoveryScope scope);

    /**
     * 获取发现范围
     * 
     * @return 当前发现范围
     */
    DiscoveryScope getDiscoveryScope();
}
