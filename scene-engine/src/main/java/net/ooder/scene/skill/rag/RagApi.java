package net.ooder.scene.skill.rag;

import java.util.List;

/**
 * RAG (Retrieval-Augmented Generation) 接口
 * 提供检索增强生成功能
 *
 * <p>实现类需要提供文档检索、提示增强等功能</p>
 *
 * @author ooder
 * @since 2.3
 */
public interface RagApi {
    
    /**
     * 检索相关知识
     * @param context RAG 上下文
     * @return 检索结果
     */
    RagResult retrieve(RagContext context);
    
    /**
     * 增强提示
     * @param query 用户查询
     * @param result 检索结果
     * @return 增强后的提示
     */
    String augmentPrompt(String query, RagResult result);
    
    /**
     * 注册知识库
     * @param kbId 知识库ID
     * @param config 知识库配置
     */
    void registerKnowledgeBase(String kbId, KnowledgeBaseConfig config);
    
    /**
     * 注销知识库
     * @param kbId 知识库ID
     */
    void unregisterKnowledgeBase(String kbId);
    
    /**
     * 混合检索（多个知识库）
     * @param context RAG 上下文
     * @param kbIds 知识库ID列表
     * @return 检索结果
     */
    RagResult hybridRetrieve(RagContext context, List<String> kbIds);
    
    /**
     * 生成回答
     * @param query 用户查询
     * @param context RAG 上下文
     * @return 生成的回答
     */
    String generate(String query, RagContext context);
}
