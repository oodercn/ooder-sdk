package net.ooder.scene.skill;

import java.util.Map;

/**
 * 流式输出处理器
 *
 * <p>用于处理LLM流式输出的回调接口</p>
 *
 * @author Ooder Team
 * @since 2.3
 */
public interface StreamHandler {

    /**
     * 当接收到新的内容块时调用
     *
     * @param content 内容块
     */
    void onContent(String content);

    /**
     * 当流式输出完成时调用
     */
    void onComplete();

    /**
     * 当发生错误时调用
     *
     * @param error 错误信息
     */
    void onError(Throwable error);

    /**
     * 当接收到元数据时调用（可选）
     *
     * @param metadata 元数据
     */
    default void onMetadata(Map<String, Object> metadata) {
        // 默认空实现
    }
}
