package net.ooder.scene.llm.proxy.lifecycle;

import net.ooder.scene.llm.proxy.agent.AgentLlmSessionContext;
import net.ooder.scene.llm.proxy.agent.AgentLlmSessionHandle;

/**
 * Agent生命周期监听器
 * 对应 JDSServer 的 SessionLifecycle
 */
public interface AgentLifecycleListener {
    
    /**
     * Agent创建时触发
     */
    void onAgentCreated(AgentLlmSessionHandle handle, AgentLlmSessionContext context);
    
    /**
     * Agent激活时触发（从空闲状态恢复）
     */
    void onAgentActivated(AgentLlmSessionHandle handle, AgentLlmSessionContext context);
    
    /**
     * Agent空闲超时时触发
     */
    void onAgentIdleTimeout(AgentLlmSessionHandle handle, AgentLlmSessionContext context);
    
    /**
     * Agent销毁时触发
     */
    void onAgentDestroyed(AgentLlmSessionHandle handle, AgentLlmSessionContext context);
    
    /**
     * Agent配额超限时触发
     */
    void onAgentQuotaExceeded(AgentLlmSessionHandle handle, AgentLlmSessionContext context, String quotaType);
    
    /**
     * Agent连接失败时触发
     */
    void onAgentConnectionFailed(AgentLlmSessionHandle handle, AgentLlmSessionContext context, Throwable error);
}
