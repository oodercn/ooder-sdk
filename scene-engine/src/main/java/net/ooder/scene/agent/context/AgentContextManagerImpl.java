package net.ooder.scene.agent.context;

import net.ooder.scene.agent.AgentSessionManager;
import net.ooder.scene.agent.AgentRegistration;
import net.ooder.scene.agent.AgentSession;
import net.ooder.scene.event.SceneEventPublisher;
import net.ooder.scene.event.agent.AgentEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Agent 上下文管理器实现
 *
 * <p>整合现有的 AgentSessionManager 能力，提供统一的 Agent 上下文管理。</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
@Component
@ConditionalOnMissingBean(AgentContextManager.class)
public class AgentContextManagerImpl implements AgentContextManager {

    private static final Logger log = LoggerFactory.getLogger(AgentContextManagerImpl.class);

    private final Map<String, AgentProfile> profiles = new ConcurrentHashMap<>();
    private final Map<String, AgentContext> contexts = new ConcurrentHashMap<>();
    private final Map<String, ConversationContext> conversations = new ConcurrentHashMap<>();
    
    private AgentSessionManager legacySessionManager;
    private SceneEventPublisher eventPublisher;

    public AgentContextManagerImpl() {
    }

    public AgentContextManagerImpl(AgentSessionManager legacySessionManager) {
        this.legacySessionManager = legacySessionManager;
    }

    public void setEventPublisher(SceneEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public AgentProfile registerVirtualAgent(VirtualAgentConfig config) {
        if (config == null || config.getAgentId() == null) {
            throw new IllegalArgumentException("AgentId is required");
        }

        String agentId = config.getAgentId();
        
        AgentProfile profile = AgentProfile.builder()
                .agentId(agentId)
                .name(config.getName())
                .type(AgentType.VIRTUAL)
                .sceneGroupId(config.getSceneGroupId())
                .role(config.getRole())
                .description(config.getDescription())
                .status(AgentStatus.ONLINE)
                .build();
        
        profile.setVirtual(true);
        profile.setCapabilities(config.getCapabilities() != null ? 
                new HashMap<>(config.getCapabilities().stream().collect(Collectors.toMap(c -> c, c -> Boolean.TRUE))) : 
                new HashMap<>());
        profile.setMetadata(config.getMetadata());
        
        profiles.put(agentId, profile);
        
        AgentContext context = new AgentContext(agentId, config.getSceneGroupId());
        context.setMaxHistoryLength(config.getMaxHistoryLength());
        if (config.getSystemPrompt() != null) {
            context.getSystemContext().put("systemPrompt", config.getSystemPrompt());
        }
        if (config.getLlmProvider() != null) {
            context.getSystemContext().put("llmProvider", config.getLlmProvider());
        }
        if (config.getLlmModel() != null) {
            context.getSystemContext().put("llmModel", config.getLlmModel());
        }
        contexts.put(agentId, context);
        
        syncWithLegacySessionManager(profile);
        
        publishAgentEvent(AgentEvent.registered(this, agentId, config.getName()));
        
        log.info("Virtual Agent registered: agentId={}, name={}", agentId, config.getName());
        return profile;
    }

    @Override
    public AgentProfile registerPhysicalAgent(PhysicalAgentConfig config) {
        if (config == null || config.getAgentId() == null) {
            throw new IllegalArgumentException("AgentId is required");
        }

        String agentId = config.getAgentId();
        
        AgentProfile profile = AgentProfile.builder()
                .agentId(agentId)
                .name(config.getName())
                .type(AgentType.PHYSICAL)
                .sceneGroupId(config.getSceneGroupId())
                .role(config.getRole())
                .description(config.getDescription())
                .status(AgentStatus.ONLINE)
                .heartbeatInterval(config.getHeartbeatInterval())
                .heartbeatTimeout(config.getHeartbeatTimeout())
                .build();
        
        profile.setVirtual(false);
        profile.setMetadata(config.getMetadata());
        
        profiles.put(agentId, profile);
        
        AgentContext context = new AgentContext(agentId, config.getSceneGroupId());
        context.getSystemContext().put("endpoint", config.getEndpoint());
        contexts.put(agentId, context);
        
        syncWithLegacySessionManager(profile);
        
        publishAgentEvent(AgentEvent.registered(this, agentId, config.getName()));
        
        log.info("Physical Agent registered: agentId={}, name={}, endpoint={}", 
                agentId, config.getName(), config.getEndpoint());
        return profile;
    }

    private void syncWithLegacySessionManager(AgentProfile profile) {
        if (legacySessionManager != null) {
            AgentRegistration registration = new AgentRegistration();
            registration.setAgentId(profile.getAgentId());
            registration.setCapabilities(new ArrayList<>(profile.getCapabilities().keySet()));
            registration.setMetadata(profile.getMetadata());
            legacySessionManager.register(registration);
        }
    }

    @Override
    public void unregisterAgent(String agentId) {
        if (agentId == null) {
            return;
        }

        AgentProfile profile = profiles.remove(agentId);
        contexts.remove(agentId);
        
        if (legacySessionManager != null) {
            legacySessionManager.invalidate(agentId);
        }
        
        if (profile != null) {
            publishAgentEvent(AgentEvent.unregistered(this, agentId, profile.getName()));
            log.info("Agent unregistered: agentId={}", agentId);
        }
    }

    @Override
    public AgentProfile getAgentProfile(String agentId) {
        if (agentId == null) {
            return null;
        }
        return profiles.get(agentId);
    }

    @Override
    public AgentContext getAgentContext(String agentId) {
        if (agentId == null) {
            return null;
        }
        return contexts.computeIfAbsent(agentId, id -> new AgentContext(id));
    }

    @Override
    public void updateAgentContext(String agentId, Map<String, Object> context) {
        updateAgentContext(agentId, "conversation", context);
    }

    @Override
    public void updateAgentContext(String agentId, String level, Map<String, Object> context) {
        AgentContext agentContext = getAgentContext(agentId);
        if (agentContext != null && context != null) {
            agentContext.updateContext(level, context);
            log.debug("Agent context updated: agentId={}, level={}", agentId, level);
        }
    }

    @Override
    public ConversationContext getConversationContext(String conversationId) {
        if (conversationId == null) {
            return null;
        }
        return conversations.get(conversationId);
    }

    @Override
    public ConversationContext createIsolatedContext(String agentId, String sceneGroupId) {
        String conversationId = "conv_" + agentId + "_" + UUID.randomUUID().toString().substring(0, 8);
        
        ConversationContext context = new ConversationContext(conversationId, sceneGroupId);
        context.addParticipant(agentId);
        
        conversations.put(conversationId, context);
        
        log.debug("Isolated conversation context created: conversationId={}, agentId={}", 
                conversationId, agentId);
        return context;
    }

    @Override
    public AgentStatus getAgentStatus(String agentId) {
        AgentProfile profile = getAgentProfile(agentId);
        return profile != null ? profile.getStatus() : AgentStatus.OFFLINE;
    }

    @Override
    public void updateAgentStatus(String agentId, AgentStatus status) {
        AgentProfile profile = getAgentProfile(agentId);
        if (profile != null) {
            profile.setStatus(status);
            log.debug("Agent status updated: agentId={}, status={}", agentId, status);
        }
    }

    @Override
    public void heartbeat(String agentId) {
        AgentProfile profile = getAgentProfile(agentId);
        if (profile != null) {
            profile.heartbeat();
            
            if (legacySessionManager != null) {
                AgentSession session = legacySessionManager.getSession(agentId);
                if (session != null) {
                    session.touch();
                }
            }
            
            log.debug("Agent heartbeat received: agentId={}", agentId);
        }
    }

    @Override
    public List<AgentProfile> getAgentsByScene(String sceneGroupId) {
        if (sceneGroupId == null) {
            return new ArrayList<>();
        }
        
        return profiles.values().stream()
                .filter(p -> sceneGroupId.equals(p.getSceneGroupId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<AgentProfile> getOnlineAgents(String sceneGroupId) {
        return getAgentsByScene(sceneGroupId).stream()
                .filter(AgentProfile::isOnline)
                .collect(Collectors.toList());
    }

    @Override
    public List<AgentProfile> getVirtualAgents(String sceneGroupId) {
        return getAgentsByScene(sceneGroupId).stream()
                .filter(AgentProfile::isVirtual)
                .collect(Collectors.toList());
    }

    @Override
    public List<AgentProfile> getPhysicalAgents(String sceneGroupId) {
        return getAgentsByScene(sceneGroupId).stream()
                .filter(p -> !p.isVirtual())
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAgentOnline(String agentId) {
        AgentProfile profile = getAgentProfile(agentId);
        return profile != null && profile.isOnline();
    }

    @Override
    public int getAgentCount() {
        return profiles.size();
    }

    @Override
    public int getOnlineAgentCount() {
        return (int) profiles.values().stream()
                .filter(AgentProfile::isOnline)
                .count();
    }

    @Override
    public void cleanupOfflineAgents(long timeoutMs) {
        long now = System.currentTimeMillis();
        
        List<String> toRemove = profiles.values().stream()
                .filter(p -> !p.isVirtual())
                .filter(p -> p.getLastHeartbeatAt() > 0)
                .filter(p -> (now - p.getLastHeartbeatAt()) > timeoutMs)
                .map(AgentProfile::getAgentId)
                .collect(Collectors.toList());
        
        for (String agentId : toRemove) {
            unregisterAgent(agentId);
        }
        
        if (!toRemove.isEmpty()) {
            log.info("Cleaned up {} offline agents", toRemove.size());
        }
    }

    private void publishAgentEvent(AgentEvent event) {
        if (eventPublisher != null) {
            eventPublisher.publishAgentEvent(event);
        }
    }
}
