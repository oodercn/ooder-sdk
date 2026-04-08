package net.ooder.scene.group.template;

import net.ooder.scene.core.template.SceneTemplate;

import java.util.List;
import java.util.Map;

/**
 * 场景组模板管理器接口
 *
 * <p>管理场景组模板的创建、查询和应用，支持从模板快速创建场景组。</p>
 *
 * <h3>核心功能：</h3>
 * <ul>
 *   <li>模板 CRUD 操作</li>
 *   <li>模板分类管理</li>
 *   <li>从模板创建场景组</li>
 *   <li>模板版本管理</li>
 * </ul>
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
public interface SceneGroupTemplateManager {

    /**
     * 创建模板
     *
     * @param templateId 模板ID
     * @param name 模板名称
     * @param description 模板描述
     * @param category 模板分类
     * @return 创建的模板
     */
    SceneTemplate createTemplate(String templateId, String name, String description, String category);

    /**
     * 获取模板
     *
     * @param templateId 模板ID
     * @return 模板，不存在返回 null
     */
    SceneTemplate getTemplate(String templateId);

    /**
     * 获取所有模板
     *
     * @return 模板列表
     */
    List<SceneTemplate> getAllTemplates();

    /**
     * 获取指定分类的模板
     *
     * @param category 分类
     * @return 模板列表
     */
    List<SceneTemplate> getTemplatesByCategory(String category);

    /**
     * 更新模板
     *
     * @param templateId 模板ID
     * @param name 名称
     * @param description 描述
     * @return 是否成功
     */
    boolean updateTemplate(String templateId, String name, String description);

    /**
     * 删除模板
     *
     * @param templateId 模板ID
     * @return 是否成功
     */
    boolean deleteTemplate(String templateId);

    /**
     * 设置模板配置
     *
     * @param templateId 模板ID
     * @param key 配置键
     * @param value 配置值
     * @return 是否成功
     */
    boolean setTemplateConfig(String templateId, String key, Object value);

    /**
     * 获取模板配置
     *
     * @param templateId 模板ID
     * @param key 配置键
     * @return 配置值
     */
    Object getTemplateConfig(String templateId, String key);

    /**
     * 从模板创建场景组
     *
     * @param sceneGroupId 场景组ID
     * @param templateId 模板ID
     * @param creatorId 创建者ID
     * @param name 场景组名称
     * @return 创建的场景组ID
     */
    String createSceneGroupFromTemplate(String sceneGroupId, String templateId, String creatorId, String name);

    /**
     * 获取模板使用次数
     *
     * @param templateId 模板ID
     * @return 使用次数
     */
    int getTemplateUsageCount(String templateId);

    /**
     * 获取所有分类
     *
     * @return 分类列表
     */
    List<String> getAllCategories();

    /**
     * 检查模板是否存在
     *
     * @param templateId 模板ID
     * @return 是否存在
     */
    boolean exists(String templateId);

    /**
     * 克隆模板
     *
     * @param sourceTemplateId 源模板ID
     * @param newTemplateId 新模板ID
     * @param newName 新模板名称
     * @return 新模板ID
     */
    String cloneTemplate(String sourceTemplateId, String newTemplateId, String newName);

    /**
     * 导出模板为 JSON
     *
     * @param templateId 模板ID
     * @return JSON 字符串
     */
    String exportTemplate(String templateId);

    /**
     * 从 JSON 导入模板
     *
     * @param json JSON 字符串
     * @param templateId 模板ID
     * @return 导入的模板ID
     */
    String importTemplate(String json, String templateId);
}
