package net.ooder.sdk.llm.context;

import java.util.List;
import java.util.Map;

/**
 * 上下文模板 API
 */
public interface ContextTemplateApi {

    /**
     * 注册上下文模板
     * @param templateId 模板ID
     * @param template 模板内容
     */
    void registerTemplate(String templateId, ContextTemplate template);

    /**
     * 注销上下文模板
     * @param templateId 模板ID
     */
    void unregisterTemplate(String templateId);

    /**
     * 获取上下文模板
     * @param templateId 模板ID
     * @return 模板内容
     */
    ContextTemplate getTemplate(String templateId);

    /**
     * 检查模板是否存在
     * @param templateId 模板ID
     * @return 是否存在
     */
    boolean hasTemplate(String templateId);

    /**
     * 列出所有模板
     * @return 模板列表
     */
    List<ContextTemplate> listTemplates();

    /**
     * 渲染上下文
     * @param templateId 模板ID
     * @param variables 变量
     * @return 渲染后的上下文
     */
    String renderContext(String templateId, Map<String, Object> variables);

    /**
     * 渲染系统提示词
     * @param templateId 模板ID
     * @param variables 变量
     * @return 渲染后的系统提示词
     */
    String renderSystemPrompt(String templateId, Map<String, Object> variables);

    /**
     * 创建上下文实例
     * @param templateId 模板ID
     * @param sessionId 会话ID
     * @return 上下文实例
     */
    ContextInstance createContext(String templateId, String sessionId);

    /**
     * 创建上下文实例（带变量）
     * @param templateId 模板ID
     * @param sessionId 会话ID
     * @param variables 初始变量
     * @return 上下文实例
     */
    ContextInstance createContext(String templateId, String sessionId, Map<String, Object> variables);

    /**
     * 获取上下文实例
     * @param instanceId 实例ID
     * @return 上下文实例
     */
    ContextInstance getContextInstance(String instanceId);

    /**
     * 更新上下文实例
     * @param instanceId 实例ID
     * @param instance 上下文实例
     */
    void updateContextInstance(String instanceId, ContextInstance instance);

    /**
     * 删除上下文实例
     * @param instanceId 实例ID
     */
    void deleteContextInstance(String instanceId);

    /**
     * 克隆上下文实例
     * @param sourceInstanceId 源实例ID
     * @param newSessionId 新会话ID
     * @return 新的上下文实例
     */
    ContextInstance cloneContextInstance(String sourceInstanceId, String newSessionId);
}
