package net.ooder.scene.llm.proxy.lifecycle;

import net.ooder.scene.llm.proxy.agent.AgentLlmSessionContext;
import net.ooder.scene.llm.proxy.agent.AgentLlmSessionHandle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志记录型生命周期监听器
 */
public class LoggingAgentLifecycleListener implements AgentLifecycleListener {

    private static final Logger log = LoggerFactory.getLogger(LoggingAgentLifecycleListener.class);

    @Override
    public void onAgentCreated(AgentLlmSessionHandle handle, AgentLlmSessionContext context) {
        log.info("[AgentLifecycle] Agent created: agentId={}, userId={}, type={}",
                handle.getAgentId(),
                handle.getUserId(),
                context.getAgentType());
    }

    @Override
    public void onAgentActivated(AgentLlmSessionHandle handle, AgentLlmSessionContext context) {
        log.debug("[AgentLifecycle] Agent activated: agentId={}", handle.getAgentId());
    }

    @Override
    public void onAgentIdleTimeout(AgentLlmSessionHandle handle, AgentLlmSessionContext context) {
        log.warn("[AgentLifecycle] Agent idle timeout: agentId={}, idleTime={}ms",
                handle.getAgentId(),
                System.currentTimeMillis() - context.getLastActiveAt());
    }

    @Override
    public void onAgentDestroyed(AgentLlmSessionHandle handle, AgentLlmSessionContext context) {
        log.info("[AgentLifecycle] Agent destroyed: agentId={}, userId={}, lifetime={}ms",
                handle.getAgentId(),
                handle.getUserId(),
                System.currentTimeMillis() - handle.getCreatedTime());
    }

    @Override
    public void onAgentQuotaExceeded(AgentLlmSessionHandle handle, AgentLlmSessionContext context, String quotaType) {
        log.error("[AgentLifecycle] Agent quota exceeded: agentId={}, quotaType={}, used={}",
                handle.getAgentId(),
                quotaType,
                context.getQuota().getDailyTokenUsed());
    }

    @Override
    public void onAgentConnectionFailed(AgentLlmSessionHandle handle, AgentLlmSessionContext context, Throwable error) {
        log.error("[AgentLifecycle] Agent connection failed: agentId={}, error={}",
                handle.getAgentId(),
                error.getMessage(),
                error);
    }
}
