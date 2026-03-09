package net.ooder.sdk.agent.llm.impl;

import net.ooder.sdk.agent.llm.LLMCollaborationApi;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LLMCollaborationApi 实现类（简化版）
 */
public class LLMCollaborationApiImpl implements LLMCollaborationApi {

    private final Map<String, List<LlmCapability>> allocatedCapabilities = new ConcurrentHashMap<>();
    private final Map<String, CollaborationSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, List<CollaborationMessage>> sessionMessages = new ConcurrentHashMap<>();

    @Override
    public CompletableFuture<LlmAllocationResult> allocateLlmCapabilities(String sceneId, List<LlmCapability> llmCapabilities) {
        return CompletableFuture.supplyAsync(() -> {
            allocatedCapabilities.put(sceneId, llmCapabilities);
            LlmAllocationResult result = new LlmAllocationResult();
            result.setSuccess(true);
            List<String> ids = new ArrayList<>();
            for (LlmCapability cap : llmCapabilities) {
                ids.add(cap.getId());
            }
            result.setAllocatedCapabilityIds(ids);
            return result;
        });
    }

    @Override
    public CompletableFuture<Boolean> releaseLlmCapability(String sceneId, String capabilityId) {
        return CompletableFuture.supplyAsync(() -> {
            List<LlmCapability> caps = allocatedCapabilities.get(sceneId);
            if (caps != null) {
                caps.removeIf(c -> c.getId().equals(capabilityId));
            }
            return true;
        });
    }

    @Override
    public CompletableFuture<CollaborationSession> createCollaborationSession(String sceneId, List<String> participants, Map<String, Object> context) {
        return CompletableFuture.supplyAsync(() -> {
            String sessionId = "session_" + System.currentTimeMillis();
            CollaborationSession session = new CollaborationSession();
            session.setSessionId(sessionId);
            session.setSceneId(sceneId);
            session.setParticipants(participants);
            session.setStatus("ACTIVE");
            session.setCreatedAt(System.currentTimeMillis());
            sessions.put(sessionId, session);
            sessionMessages.put(sessionId, new ArrayList<>());
            return session;
        });
    }

    @Override
    public CompletableFuture<CollaborationMessage> sendCollaborationMessage(String sessionId, CollaborationMessage message) {
        return CompletableFuture.supplyAsync(() -> {
            message.setTimestamp(System.currentTimeMillis());
            sessionMessages.computeIfAbsent(sessionId, k -> new ArrayList<>()).add(message);
            return message;
        });
    }

    @Override
    public CompletableFuture<List<CollaborationMessage>> getSessionHistory(String sessionId) {
        return CompletableFuture.supplyAsync(() -> {
            List<CollaborationMessage> messages = sessionMessages.get(sessionId);
            return messages != null ? new ArrayList<>(messages) : new ArrayList<>();
        });
    }

    @Override
    public CompletableFuture<Boolean> closeCollaborationSession(String sessionId) {
        return CompletableFuture.supplyAsync(() -> {
            CollaborationSession session = sessions.get(sessionId);
            if (session != null) {
                session.setStatus("CLOSED");
            }
            return true;
        });
    }

    @Override
    public CompletableFuture<List<LlmCapabilityStatus>> getLlmCapabilityStatus(String sceneId) {
        return CompletableFuture.supplyAsync(() -> {
            List<LlmCapabilityStatus> statuses = new ArrayList<>();
            List<LlmCapability> caps = allocatedCapabilities.get(sceneId);
            if (caps != null) {
                for (LlmCapability cap : caps) {
                    LlmCapabilityStatus status = new LlmCapabilityStatus();
                    status.setCapabilityId(cap.getId());
                    status.setState("ACTIVE");
                    status.setActiveSessions(1);
                    statuses.add(status);
                }
            }
            return statuses;
        });
    }
}
