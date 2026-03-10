package net.ooder.scene.skill.llm;

import java.util.Map;

/**
 * 流式处理回调接口
 * 用于处理 LLM 流式输出的数据块
 *
 * @author ooder
 * @since 2.3
 */
public interface StreamHandler {

    /**
     * 接收到内容时调用
     * @param content 内容
     */
    void onContent(String content);

    /**
     * 接收到数据块时调用
     * @param chunk 数据块内容
     */
    void onChunk(String chunk);

    /**
     * 流式输出完成时调用
     * @param metadata 元数据信息，包含 usage, model 等
     */
    void onComplete(Map<String, Object> metadata);

    /**
     * 发生错误时调用
     * @param error 错误异常
     */
    void onError(Throwable error);
}
