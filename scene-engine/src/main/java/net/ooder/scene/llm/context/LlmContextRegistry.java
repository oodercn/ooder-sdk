package net.ooder.scene.llm.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LLM 上下文注册中心
 * 
 * <p>管理所有 LLM 场景上下文的生命周期。</p>
 * 
 * <p>设计原则：</p>
 * <ul>
 *   <li>复用现有 AgentSessionManager 的四级缓存设计</li>
 *   <li>支持过期清理</li>
 *   <li>线程安全</li>
 * </ul>
 *
 * @author Ooder Team
 * @since 2.4.0
 */
public class LlmContextRegistry {

    private static final Logger log = LoggerFactory.getLogger(LlmContextRegistry.class);

    private static final long DEFAULT_TTL = 30 * 60 * 1000;
    private static final long DEFAULT_IDLE_TIMEOUT = 10 * 60 * 1000;

    private final Map<String, LlmSceneContext> contextCache;
    private final Map<String, String> sceneToContextMap;
    private final Map<String, String> agentToContextMap;
    private final Object lock = new Object();

    public LlmContextRegistry() {
        this.contextCache = new ConcurrentHashMap<>();
        this.sceneToContextMap = new ConcurrentHashMap<>();
        this.agentToContextMap = new ConcurrentHashMap<>();
    }

    public void register(LlmSceneContext context) {
        if (context == null || context.getContextId() == null) {
            throw new IllegalArgumentException("Context and contextId must not be null");
        }
        
        synchronized (lock) {
            contextCache.put(context.getContextId(), context);
            
            if (context.getSceneId() != null) {
                sceneToContextMap.put(context.getSceneId(), context.getContextId());
            }
            
            if (context.getAgentId() != null) {
                agentToContextMap.put(context.getAgentId(), context.getContextId());
            }
        }
        
        log.debug("Registered context: contextId={}, sceneId={}, agentId={}", 
            context.getContextId(), context.getSceneId(), context.getAgentId());
    }

    public LlmSceneContext get(String contextId) {
        if (contextId == null) {
            return null;
        }
        
        LlmSceneContext context = contextCache.get(contextId);
        if (context != null) {
            context.touch();
        }
        return context;
    }

    public LlmSceneContext getBySceneId(String sceneId) {
        if (sceneId == null) {
            return null;
        }
        
        String contextId = sceneToContextMap.get(sceneId);
        return contextId != null ? get(contextId) : null;
    }

    public LlmSceneContext getByAgentId(String agentId) {
        if (agentId == null) {
            return null;
        }
        
        String contextId = agentToContextMap.get(agentId);
        return contextId != null ? get(contextId) : null;
    }

    public void remove(String contextId) {
        if (contextId == null) {
            return;
        }
        
        synchronized (lock) {
            LlmSceneContext context = contextCache.remove(contextId);
            if (context != null) {
                if (context.getSceneId() != null) {
                    sceneToContextMap.remove(context.getSceneId());
                }
                if (context.getAgentId() != null) {
                    agentToContextMap.remove(context.getAgentId());
                }
                log.debug("Removed context: contextId={}", contextId);
            }
        }
    }

    public void removeBySceneId(String sceneId) {
        if (sceneId == null) {
            return;
        }
        
        String contextId = sceneToContextMap.get(sceneId);
        if (contextId != null) {
            remove(contextId);
        }
    }

    public void removeByAgentId(String agentId) {
        if (agentId == null) {
            return;
        }
        
        String contextId = agentToContextMap.get(agentId);
        if (contextId != null) {
            remove(contextId);
        }
    }

    public void update(LlmSceneContext context) {
        if (context == null || context.getContextId() == null) {
            return;
        }
        
        contextCache.put(context.getContextId(), context);
    }

    public List<LlmSceneContext> getAllActive() {
        List<LlmSceneContext> activeContexts = new ArrayList<>();
        for (LlmSceneContext context : contextCache.values()) {
            if (!isExpired(context)) {
                activeContexts.add(context);
            }
        }
        return activeContexts;
    }

    public int evictExpired() {
        int evictedCount = 0;
        
        List<String> expiredIds = new ArrayList<>();
        for (Map.Entry<String, LlmSceneContext> entry : contextCache.entrySet()) {
            if (isExpired(entry.getValue())) {
                expiredIds.add(entry.getKey());
            }
        }
        
        for (String contextId : expiredIds) {
            remove(contextId);
            evictedCount++;
        }
        
        if (evictedCount > 0) {
            log.info("Evicted {} expired contexts", evictedCount);
        }
        
        return evictedCount;
    }

    public boolean isExpired(LlmSceneContext context) {
        if (context == null) {
            return true;
        }
        
        long now = System.currentTimeMillis();
        long age = now - context.getCreatedAt();
        long idle = now - context.getLastAccessedAt();
        
        return age > DEFAULT_TTL || idle > DEFAULT_IDLE_TIMEOUT;
    }

    public int size() {
        return contextCache.size();
    }

    public boolean contains(String contextId) {
        return contextId != null && contextCache.containsKey(contextId);
    }

    public void clear() {
        synchronized (lock) {
            contextCache.clear();
            sceneToContextMap.clear();
            agentToContextMap.clear();
        }
        log.info("Cleared all contexts");
    }
    
    public ContextStats getStats() {
        ContextStats stats = new ContextStats();
        stats.setTotalContexts(contextCache.size());
        stats.setTotalScenes(sceneToContextMap.size());
        stats.setTotalAgents(agentToContextMap.size());
        
        int activeCount = 0;
        int expiredCount = 0;
        for (LlmSceneContext context : contextCache.values()) {
            if (isExpired(context)) {
                expiredCount++;
            } else {
                activeCount++;
            }
        }
        stats.setActiveContexts(activeCount);
        stats.setExpiredContexts(expiredCount);
        
        return stats;
    }
    
    public static class ContextStats {
        private int totalContexts;
        private int activeContexts;
        private int expiredContexts;
        private int totalScenes;
        private int totalAgents;
        
        public int getTotalContexts() { return totalContexts; }
        public void setTotalContexts(int totalContexts) { this.totalContexts = totalContexts; }
        
        public int getActiveContexts() { return activeContexts; }
        public void setActiveContexts(int activeContexts) { this.activeContexts = activeContexts; }
        
        public int getExpiredContexts() { return expiredContexts; }
        public void setExpiredContexts(int expiredContexts) { this.expiredContexts = expiredContexts; }
        
        public int getTotalScenes() { return totalScenes; }
        public void setTotalScenes(int totalScenes) { this.totalScenes = totalScenes; }
        
        public int getTotalAgents() { return totalAgents; }
        public void setTotalAgents(int totalAgents) { this.totalAgents = totalAgents; }
        
        @Override
        public String toString() {
            return "ContextStats{" +
                    "totalContexts=" + totalContexts +
                    ", activeContexts=" + activeContexts +
                    ", expiredContexts=" + expiredContexts +
                    ", totalScenes=" + totalScenes +
                    ", totalAgents=" + totalAgents +
                    '}';
        }
    }
}
