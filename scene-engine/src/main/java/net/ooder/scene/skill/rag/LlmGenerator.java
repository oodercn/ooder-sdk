package net.ooder.scene.skill.rag;

/**
 * LLM 生成器接口
 *
 * <p>用于生成回答的 LLM 调用接口</p>
 *
 * @author ooder
 * @since 2.3
 */
public interface LlmGenerator {
    
    /**
     * 生成回答
     *
     * @param prompt 增强后的提示
     * @return 生成的回答
     */
    String generate(String prompt);
    
    /**
     * 流式生成回答
     *
     * @param prompt 增强后的提示
     * @param handler 流式处理器
     */
    void generateStream(String prompt, StreamHandler handler);
    
    /**
     * 流式处理器
     */
    interface StreamHandler {
        void onChunk(String chunk);
        void onComplete(String fullResponse);
        void onError(Exception e);
    }
}
