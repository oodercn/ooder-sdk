package net.ooder.scene.skill.prompt.rag;

import net.ooder.scene.skill.prompt.model.PromptDocument;
import net.ooder.scene.skill.prompt.model.PromptFragment;

import java.util.List;
import java.util.Map;

/**
 * 技能提示词 RAG 提供者接口
 *
 * <p>提供从知识库检索提示词的能力：</p>
 * <ul>
 *   <li>技能安装时将提示语文档入库到知识库</li>
 *   <li>LLM 调用时通过 RAG 接口检索相关提示语内容</li>
 *   <li>支持提示语的版本管理和增量更新</li>
 * </ul>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public interface SkillPromptRagProvider {

    /**
     * 技能安装时：将提示语文档入库
     *
     * @param skillId 技能ID
     * @param documents 提示语文档列表
     * @return 入库成功的文档数量
     */
    int indexPromptDocuments(String skillId, List<PromptDocument> documents);

    /**
     * LLM 调用时：检索系统提示词
     *
     * @param skillId 技能ID
     * @param context 上下文信息（用户问题、会话历史等）
     * @return 组装后的系统提示词
     */
    String retrieveSystemPrompt(String skillId, String context);

    /**
     * LLM 调用时：检索角色提示词
     *
     * @param skillId 技能ID
     * @param roleId 角色ID
     * @param context 上下文信息
     * @return 角色提示词
     */
    String retrieveRolePrompt(String skillId, String roleId, String context);

    /**
     * 通用提示语检索
     *
     * @param skillId 技能ID
     * @param query 查询内容
     * @param topK 返回数量
     * @return 提示语片段列表
     */
    List<PromptFragment> searchPrompts(String skillId, String query, int topK);

    /**
     * 混合检索（静态配置 + RAG 检索）
     *
     * @param skillId 技能ID
     * @param staticPrompt 静态配置的提示词
     * @param context 上下文信息
     * @param options 检索选项
     * @return 混合组装后的提示词
     */
    String hybridRetrieve(String skillId, String staticPrompt, String context, Map<String, Object> options);

    /**
     * 删除技能的提示语索引
     *
     * @param skillId 技能ID
     * @return 删除的文档数量
     */
    int deletePromptIndex(String skillId);

    /**
     * 检查技能提示语是否已索引
     *
     * @param skillId 技能ID
     * @return 是否已索引
     */
    boolean isPromptIndexed(String skillId);

    /**
     * 获取提示语索引统计
     *
     * @param skillId 技能ID
     * @return 统计信息
     */
    Map<String, Object> getPromptIndexStats(String skillId);
}
