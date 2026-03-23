package net.ooder.sdk.llm.scene.transfer.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.llm.scene.*;
import net.ooder.sdk.llm.scene.transfer.ContextTransfer;
import net.ooder.sdk.llm.scene.transfer.ContextTransferHandler;

import java.util.*;

/**
 * 上下文传递处理器实现
 */
@Slf4j
public class ContextTransferHandlerImpl implements ContextTransferHandler {

    private final SceneContextInitializer contextInitializer;

    public ContextTransferHandlerImpl(SceneContextInitializer contextInitializer) {
        this.contextInitializer = contextInitializer;
    }

    @Override
    public ContextTransfer prepareTransfer(
            LlmSceneContext sourceContext,
            TransferMode mode,
            Set<LlmSceneContext.ContextPart> includedParts) {

        String transferId = generateTransferId();

        ContextTransfer.ContextTransferBuilder builder = ContextTransfer.builder()
                .transferId(transferId)
                .sourceContextId(sourceContext.getContextId())
                .sourceSceneId(sourceContext.getSceneId())
                .transferMode(mode)
                .createdAt(System.currentTimeMillis())
                .expiresAt(System.currentTimeMillis() + 5 * 60 * 1000) // 5分钟过期
                .status(ContextTransfer.TransferStatus.PENDING);

        // 根据传递模式准备数据
        switch (mode) {
            case FULL:
                builder.serializedContext(contextInitializer.serialize(sourceContext));
                break;

            case REFERENCE:
                builder.contextReference(ContextTransfer.ContextReference.builder()
                        .contextId(sourceContext.getContextId())
                        .validUntil(System.currentTimeMillis() + 10 * 60 * 1000) // 10分钟有效
                        .build());
                break;

            case DELTA:
                // FIXME: 需要实现差异计算
                builder.contextDelta(new HashMap<>());
                break;

            case SELECTIVE:
                builder.includedParts(convertPartsToStrings(includedParts));
                builder.serializedContext(contextInitializer.serializePartial(sourceContext, includedParts));
                break;
        }

        ContextTransfer transfer = builder.build();
        log.info("Context transfer prepared: {} from context: {} with mode: {}",
                transferId, sourceContext.getContextId(), mode);
        return transfer;
    }

    @Override
    public LlmSceneContext receiveTransfer(ContextTransfer transfer, String targetSceneId) {
        // 验证传递
        if (!validateTransfer(transfer)) {
            throw new IllegalArgumentException("Invalid context transfer: " + transfer.getTransferId());
        }

        transfer.setTargetSceneId(targetSceneId);
        transfer.setStatus(ContextTransfer.TransferStatus.IN_TRANSIT);

        LlmSceneContext receivedContext;

        switch (transfer.getTransferMode()) {
            case FULL:
            case SELECTIVE:
                receivedContext = contextInitializer.deserialize(transfer.getSerializedContext());
                break;

            case REFERENCE:
                // FIXME: 需要通过引用获取上下文
                receivedContext = restoreFromReference(transfer.getContextReference());
                break;

            case DELTA:
                // FIXME: 需要应用差异
                receivedContext = applyDelta(transfer.getSourceContextId(), transfer.getContextDelta());
                break;

            default:
                throw new IllegalStateException("Unknown transfer mode: " + transfer.getTransferMode());
        }

        if (receivedContext != null) {
            receivedContext.setSceneId(targetSceneId);
            receivedContext.touch();
            transfer.setStatus(ContextTransfer.TransferStatus.COMPLETED);
            log.info("Context transfer received: {} for scene: {}",
                    transfer.getTransferId(), targetSceneId);
        } else {
            transfer.setStatus(ContextTransfer.TransferStatus.FAILED);
            log.error("Failed to receive context transfer: {}", transfer.getTransferId());
        }

        return receivedContext;
    }

    @Override
    public void mergeContext(LlmSceneContext target, LlmSceneContext source, MergeStrategy strategy) {
        if (target == null || source == null) {
            return;
        }

        switch (strategy) {
            case SOURCE_PRIORITY:
                // 源优先：用源的子上下文覆盖目标
                mergeWithSourcePriority(target, source);
                break;

            case TARGET_PRIORITY:
                // 目标优先：只合并目标不存在的部分
                mergeWithTargetPriority(target, source);
                break;

            case DEEP_MERGE:
                // 深度合并：递归合并
                deepMerge(target, source);
                break;
        }

        target.touch();
        log.debug("Context merged: target={}, source={}, strategy={}",
                target.getContextId(), source.getContextId(), strategy);
    }

    @Override
    public boolean validateTransfer(ContextTransfer transfer) {
        if (transfer == null) {
            return false;
        }

        // 检查是否过期
        if (transfer.getExpiresAt() > 0 && System.currentTimeMillis() > transfer.getExpiresAt()) {
            transfer.setStatus(ContextTransfer.TransferStatus.EXPIRED);
            log.warn("Context transfer expired: {}", transfer.getTransferId());
            return false;
        }

        // 检查必要字段
        if (transfer.getTransferId() == null || transfer.getSourceContextId() == null) {
            return false;
        }

        // 根据模式检查特定字段
        switch (transfer.getTransferMode()) {
            case FULL:
            case SELECTIVE:
                return transfer.getSerializedContext() != null;

            case REFERENCE:
                return transfer.getContextReference() != null;

            case DELTA:
                return transfer.getContextDelta() != null;

            default:
                return false;
        }
    }

    /**
     * 从引用恢复上下文
     */
    private LlmSceneContext restoreFromReference(ContextTransfer.ContextReference reference) {
        /**
         * FIXME: 伪实现 - 需要通过 AGENT-SDK 获取上下文
         *
         * 预期实现：
         * 1. 调用 AGENT-SDK 的 A2AService 获取远程上下文
         * 2. 验证访问令牌
         * 3. 返回上下文数据
         */
        log.warn("[STUB] restoreFromReference() not implemented. Reference: {}", reference);
        return null;
    }

    /**
     * 应用差异
     */
    private LlmSceneContext applyDelta(String baseContextId, Map<String, Object> delta) {
        /**
         * FIXME: 伪实现 - 需要实现差异应用逻辑
         *
         * 预期实现：
         * 1. 获取基础上下文
         * 2. 应用差异数据
         * 3. 返回更新后的上下文
         */
        log.warn("[STUB] applyDelta() not implemented. BaseContextId: {}", baseContextId);
        return null;
    }

    /**
     * 源优先合并
     */
    private void mergeWithSourcePriority(LlmSceneContext target, LlmSceneContext source) {
        if (source.getSceneContext() != null) {
            target.setSceneContext(source.getSceneContext());
        }
        if (source.getNlpContext() != null) {
            target.setNlpContext(source.getNlpContext());
        }
        if (source.getKnowledgeContext() != null) {
            target.setKnowledgeContext(source.getKnowledgeContext());
        }
        if (source.getToolContext() != null) {
            target.setToolContext(source.getToolContext());
        }
        if (source.getSecurityContext() != null) {
            target.setSecurityContext(source.getSecurityContext());
        }
        if (source.getMetadata() != null) {
            target.getMetadata().putAll(source.getMetadata());
        }
    }

    /**
     * 目标优先合并
     */
    private void mergeWithTargetPriority(LlmSceneContext target, LlmSceneContext source) {
        if (target.getSceneContext() == null && source.getSceneContext() != null) {
            target.setSceneContext(source.getSceneContext());
        }
        if (target.getNlpContext() == null && source.getNlpContext() != null) {
            target.setNlpContext(source.getNlpContext());
        }
        if (target.getKnowledgeContext() == null && source.getKnowledgeContext() != null) {
            target.setKnowledgeContext(source.getKnowledgeContext());
        }
        if (target.getToolContext() == null && source.getToolContext() != null) {
            target.setToolContext(source.getToolContext());
        }
        if (target.getSecurityContext() == null && source.getSecurityContext() != null) {
            target.setSecurityContext(source.getSecurityContext());
        }
        // 合并元数据（目标优先）
        if (source.getMetadata() != null) {
            for (Map.Entry<String, Object> entry : source.getMetadata().entrySet()) {
                target.getMetadata().putIfAbsent(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 深度合并
     */
    private void deepMerge(LlmSceneContext target, LlmSceneContext source) {
        // FIXME: 实现深度合并逻辑
        // 目前使用源优先作为默认实现
        mergeWithSourcePriority(target, source);
    }

    /**
     * 转换部分枚举为字符串
     */
    private Set<String> convertPartsToStrings(Set<LlmSceneContext.ContextPart> parts) {
        Set<String> result = new HashSet<>();
        for (LlmSceneContext.ContextPart part : parts) {
            result.add(part.name());
        }
        return result;
    }

    /**
     * 生成传递ID
     */
    private String generateTransferId() {
        return "xfer_" + UUID.randomUUID().toString().replace("-", "");
    }
}
