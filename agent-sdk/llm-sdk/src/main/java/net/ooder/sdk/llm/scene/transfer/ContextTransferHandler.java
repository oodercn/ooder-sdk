package net.ooder.sdk.llm.scene.transfer;

import net.ooder.sdk.llm.scene.LlmSceneContext;

import java.util.Set;

/**
 * 上下文传递处理器
 * 支持 A2A 协议的上下文传递
 */
public interface ContextTransferHandler {

    /**
     * 准备传递
     *
     * @param sourceContext 源上下文
     * @param mode          传递模式
     * @param includedParts 包含的部分
     * @return 上下文传递对象
     */
    ContextTransfer prepareTransfer(
            LlmSceneContext sourceContext,
            TransferMode mode,
            Set<LlmSceneContext.ContextPart> includedParts
    );

    /**
     * 接收传递
     *
     * @param transfer      上下文传递对象
     * @param targetSceneId 目标场景ID
     * @return 接收后的场景上下文
     */
    LlmSceneContext receiveTransfer(ContextTransfer transfer, String targetSceneId);

    /**
     * 合并上下文
     *
     * @param target   目标上下文
     * @param source   源上下文
     * @param strategy 合并策略
     */
    void mergeContext(LlmSceneContext target, LlmSceneContext source, MergeStrategy strategy);

    /**
     * 验证传递
     *
     * @param transfer 上下文传递对象
     * @return 是否有效
     */
    boolean validateTransfer(ContextTransfer transfer);

    /**
     * 传递模式枚举
     */
    enum TransferMode {
        FULL,       // 完整传递
        REFERENCE,  // 引用传递
        DELTA,      // 增量传递
        SELECTIVE   // 选择性传递
    }

    /**
     * 合并策略枚举
     */
    enum MergeStrategy {
        SOURCE_PRIORITY,    // 源优先
        TARGET_PRIORITY,    // 目标优先
        DEEP_MERGE          // 深度合并
    }
}
