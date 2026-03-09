package net.ooder.sdk.llm;

import net.ooder.sdk.llm.adapter.MultiLlmAdapterApi;
import net.ooder.sdk.llm.capability.CapabilityRequestApi;
import net.ooder.sdk.llm.context.ContextTemplateApi;
import net.ooder.sdk.llm.degradation.DegradationApi;
import net.ooder.sdk.llm.installation.InstallationContextManager;
import net.ooder.sdk.llm.memory.MemoryBridgeApi;
import net.ooder.sdk.llm.monitoring.MonitoringApi;
import net.ooder.sdk.llm.nlp.NlpInteractionApi;
import net.ooder.sdk.llm.output.StructuredOutputApi;
import net.ooder.sdk.llm.pool.LlmPoolManager;
import net.ooder.sdk.llm.pool.LlmRouter;
import net.ooder.sdk.llm.rag.RagService;
import net.ooder.sdk.llm.scene.NlpContextManager;
import net.ooder.sdk.llm.scene.SceneContextInitializer;
import net.ooder.sdk.llm.scene.transfer.ContextTransferHandler;
import net.ooder.sdk.llm.scheduling.SchedulingApi;
import net.ooder.sdk.llm.security.SecurityApi;
import net.ooder.sdk.llm.tool.ToolCallingApi;

public interface LlmSdk {

    CapabilityRequestApi getCapabilityRequestApi();

    NlpInteractionApi getNlpInteractionApi();

    SchedulingApi getSchedulingApi();

    MemoryBridgeApi getMemoryBridgeApi();

    MultiLlmAdapterApi getMultiLlmAdapterApi();

    SecurityApi getSecurityApi();

    MonitoringApi getMonitoringApi();

    // === Scene-Engine 协作文档新增 API ===

    /**
     * 获取工具调用 API (LLM-SDK-001)
     */
    ToolCallingApi getToolCallingApi();

    /**
     * 获取结构化输出 API (LLM-SDK-002)
     */
    StructuredOutputApi getStructuredOutputApi();

    /**
     * 获取上下文模板 API (LLM-SDK-003)
     */
    ContextTemplateApi getContextTemplateApi();

    /**
     * 获取降级策略 API (LLM-SDK-004)
     */
    DegradationApi getDegradationApi();

    /**
     * 获取安装上下文管理器 (LLM-SDK-005)
     */
    InstallationContextManager getInstallationContextManager();

    // === V2.3.1 Context-Core 新增 API ===

    /**
     * 获取场景上下文初始化器 (LLM-SDK-001)
     */
    SceneContextInitializer getSceneContextInitializer();

    /**
     * 获取NLP上下文管理器 (LLM-SDK-002)
     */
    NlpContextManager getNlpContextManager();

    /**
     * 获取上下文传递处理器 (LLM-SDK-003)
     */
    ContextTransferHandler getContextTransferHandler();

    /**
     * 获取RAG服务 (LLM-SDK-004)
     */
    RagService getRagService();

    /**
     * 获取LLM Pool管理器 (LLM-SDK-005)
     */
    LlmPoolManager getLlmPoolManager();

    /**
     * 获取LLM路由器 (LLM-SDK-005)
     */
    LlmRouter getLlmRouter();

    String getVersion();

    void shutdown();
}
