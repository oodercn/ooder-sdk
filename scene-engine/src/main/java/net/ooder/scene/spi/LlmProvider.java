package net.ooder.scene.spi;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * LLM 提供者 SPI
 *
 * <p>大语言模型调用接口，所有驱动必须实现</p>
 *
 * <p>实现要求：</p>
 * <ul>
 *   <li>Tiny: Ollama 本地调用</li>
 *   <li>Small: 远程 API 调用</li>
 *   <li>Enterprise: 多模型路由</li>
 * </ul>
 *
 * @author Ooder Team
 * @since 3.0.0
 */
public interface LlmProvider {

    /**
     * 同步调用
     *
     * @param prompt 提示词
     * @param params 参数
     * @return 响应
     */
    String chat(String prompt, Map<String, Object> params);

    /**
     * 异步调用
     *
     * @param prompt 提示词
     * @param params 参数
     * @return 响应 Future
     */
    CompletableFuture<String> chatAsync(String prompt, Map<String, Object> params);

    /**
     * 流式调用
     *
     * @param prompt 提示词
     * @param params 参数
     * @param callback 回调
     */
    void chatStream(String prompt, Map<String, Object> params, StreamCallback callback);

    /**
     * 带上下文调用
     *
     * @param prompt 提示词
     * @param context 上下文
     * @param params 参数
     * @return 响应
     */
    String chatWithContext(String prompt, List<Map<String, String>> context, Map<String, Object> params);

    /**
     * 获取模型名称
     *
     * @return 模型名称
     */
    String getModelName();

    /**
     * 获取提供者类型
     *
     * @return 类型: tiny, small, enterprise
     */
    String getProviderType();

    /**
     * 检查是否可用
     *
     * @return 是否可用
     */
    boolean isAvailable();

    /**
     * 流式回调接口
     */
    interface StreamCallback {
        void onNext(String token);
        void onComplete(String fullResponse);
        void onError(Throwable error);
    }
}
