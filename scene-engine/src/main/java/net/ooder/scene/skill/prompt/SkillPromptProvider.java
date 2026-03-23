package net.ooder.scene.skill.prompt;

import java.util.Map;

/**
 * 技能提示词提供者接口
 *
 * <p>提供从多种来源加载 SystemPrompt 的能力：</p>
 * <ul>
 *   <li>skill.yaml 中的 spec.llmConfig.systemPrompt 字段</li>
 *   <li>技能目录下的 system-prompt.md 文件</li>
 *   <li>技能目录下的 prompts/system.md 文件</li>
 *   <li>技能目录下的 prompts/{roleId}.md 角色提示词</li>
 * </ul>
 *
 * <p>支持变量替换和继承机制</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public interface SkillPromptProvider {

    /**
     * 从配置文件加载 SystemPrompt
     *
     * @param skillId 技能ID
     * @param promptFile 提示词文件路径（相对于技能根目录）
     * @return 提示词内容
     */
    String loadFromFile(String skillId, String promptFile);

    /**
     * 从 skill.yaml 配置加载 SystemPrompt
     *
     * @param skillId 技能ID
     * @return 提示词内容
     */
    String loadFromConfig(String skillId);

    /**
     * 变量替换
     *
     * @param template 模板内容
     * @param variables 变量映射
     * @return 替换后的内容
     */
    String interpolate(String template, Map<String, Object> variables);

    /**
     * 多来源优先级加载 SystemPrompt
     *
     * @param skillId 技能ID
     * @param options 加载选项
     * @return 提示词内容
     */
    String getSystemPrompt(String skillId, PromptLoadOptions options);

    /**
     * 获取角色提示词
     *
     * @param skillId 技能ID
     * @param roleId 角色ID
     * @return 角色提示词内容
     */
    String getRolePrompt(String skillId, String roleId);

    /**
     * 获取技能默认提示词（降级使用）
     *
     * @param skillId 技能ID
     * @return 默认提示词
     */
    String getDefaultPrompt(String skillId);
}
