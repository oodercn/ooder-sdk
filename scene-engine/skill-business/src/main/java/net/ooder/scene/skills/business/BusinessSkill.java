package net.ooder.scene.skills.business;

import java.util.List;
import java.util.Map;

/**
 * BusinessSkill 业务技能接口
 * 
 * <p>提供业务分类、业务场景管理能力。</p>
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
public interface BusinessSkill {

    String getSkillId();

    String getSkillName();

    String getSkillVersion();

    List<String> getCapabilities();

    // ==================== 业务分类管理 ====================

    /**
     * 获取业务分类列表
     * 
     * @return 分类列表
     */
    List<Map<String, Object>> getCategoryList();

    /**
     * 获取业务分类树
     * 
     * @return 分类树
     */
    List<Map<String, Object>> getCategoryTree();

    /**
     * 获取分类详情
     * 
     * @param categoryId 分类ID
     * @return 分类信息
     */
    Map<String, Object> getCategory(String categoryId);

    /**
     * 创建业务分类
     * 
     * @param params 分类参数
     * @return 创建的分类信息
     */
    Map<String, Object> createCategory(Map<String, Object> params);

    /**
     * 更新业务分类
     * 
     * @param categoryId 分类ID
     * @param params 更新参数
     * @return 更新后的分类信息
     */
    Map<String, Object> updateCategory(String categoryId, Map<String, Object> params);

    /**
     * 删除业务分类
     * 
     * @param categoryId 分类ID
     * @return 是否成功
     */
    boolean deleteCategory(String categoryId);

    // ==================== 业务场景管理 ====================

    /**
     * 获取场景列表
     * 
     * @param category 分类（可选）
     * @return 场景列表
     */
    List<Map<String, Object>> getSceneList(String category);

    /**
     * 获取场景详情
     * 
     * @param sceneId 场景ID
     * @return 场景信息
     */
    Map<String, Object> getScene(String sceneId);

    /**
     * 创建业务场景
     * 
     * @param params 场景参数
     * @return 创建的场景信息
     */
    Map<String, Object> createScene(Map<String, Object> params);

    /**
     * 更新业务场景
     * 
     * @param sceneId 场景ID
     * @param params 更新参数
     * @return 更新后的场景信息
     */
    Map<String, Object> updateScene(String sceneId, Map<String, Object> params);

    /**
     * 删除业务场景
     * 
     * @param sceneId 场景ID
     * @return 是否成功
     */
    boolean deleteScene(String sceneId);

    // ==================== 场景能力管理 ====================

    /**
     * 获取场景能力列表
     * 
     * @param sceneId 场景ID
     * @return 能力列表
     */
    List<Map<String, Object>> getSceneCapabilities(String sceneId);

    /**
     * 添加场景能力
     * 
     * @param sceneId 场景ID
     * @param capability 能力信息
     * @return 是否成功
     */
    boolean addSceneCapability(String sceneId, Map<String, Object> capability);

    /**
     * 移除场景能力
     * 
     * @param sceneId 场景ID
     * @param capabilityId 能力ID
     * @return 是否成功
     */
    boolean removeSceneCapability(String sceneId, String capabilityId);

    // ==================== 能力调用 ====================

    /**
     * 调用能力
     * 
     * @param sceneId 场景ID
     * @param capabilityId 能力ID
     * @param params 参数
     * @return 调用结果
     */
    Map<String, Object> invokeCapability(String sceneId, String capabilityId, Map<String, Object> params);

    /**
     * 批量调用能力
     * 
     * @param requests 请求列表
     * @return 结果列表
     */
    List<Map<String, Object>> batchInvoke(List<Map<String, Object>> requests);

    /**
     * 通用能力调用
     * 
     * @param capability 能力名称
     * @param params 参数
     * @return 调用结果
     */
    Object invoke(String capability, Map<String, Object> params);
}
