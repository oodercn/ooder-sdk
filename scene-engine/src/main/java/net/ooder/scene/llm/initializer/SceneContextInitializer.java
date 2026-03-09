package net.ooder.scene.llm.initializer;

import net.ooder.scene.llm.context.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 场景上下文初始化器
 * 
 * <p>负责创建和管理 LlmSceneContext 的生命周期，构建包含5种子上下文的完整上下文。</p>
 * 
 * <p>设计原则：</p>
 * <ul>
 *   <li>支持多种初始化方式：新建、恢复、从模板创建</li>
 *   <li>支持上下文过期检测和自动清理</li>
 *   <li>线程安全</li>
 * </ul>
 *
 * @author Ooder Team
 * @since 2.3.1
 */
public interface SceneContextInitializer {
    
    /**
     * 初始化场景上下文
     * 
     * @param sceneId 场景ID
     * @param request 初始化请求
     * @return 初始化后的上下文
     */
    LlmSceneContext initialize(String sceneId, SceneContextInitializeRequest request);
    
    /**
     * 从现有上下文恢复
     * 
     * @param contextId 上下文ID
     * @return 恢复的上下文，如果不存在返回null
     */
    LlmSceneContext restore(String contextId);
    
    /**
     * 从序列化数据恢复
     * 
     * @param serialized 序列化数据
     * @return 恢复的上下文
     */
    LlmSceneContext restoreFromSerialized(String serialized);
    
    /**
     * 序列化上下文
     * 
     * @param context 上下文
     * @return 序列化字符串
     */
    String serialize(LlmSceneContext context);
    
    /**
     * 序列化部分上下文
     * 
     * @param context 上下文
     * @param parts 要序列化的部分
     * @return 序列化字符串
     */
    String serializePartial(LlmSceneContext context, Set<ContextPart> parts);
    
    /**
     * 销毁上下文
     * 
     * @param contextId 上下文ID
     */
    void destroy(String contextId);
    
    /**
     * 检查上下文是否过期
     * 
     * @param contextId 上下文ID
     * @return 是否过期
     */
    boolean isExpired(String contextId);
    
    /**
     * 获取上下文状态
     * 
     * @param contextId 上下文ID
     * @return 上下文状态
     */
    ContextStatus getStatus(String contextId);
    
    /**
     * 上下文部分枚举
     */
    enum ContextPart {
        SCENE_CONTEXT,
        NLP_CONTEXT,
        KNOWLEDGE_CONTEXT,
        TOOL_CONTEXT,
        SECURITY_CONTEXT
    }
    
    /**
     * 上下文状态
     */
    enum ContextStatus {
        ACTIVE,
        EXPIRED,
        DESTROYED,
        NOT_FOUND
    }
}
