package net.ooder.sdk.llm.collaboration;

import net.ooder.sdk.llm.model.CollaborationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 协作协议实现类
 */
public class CollaborationProtocolImpl implements CollaborationProtocol {

    private static final Logger log = LoggerFactory.getLogger(CollaborationProtocolImpl.class);

    private final Map<String, CollaborationSessionImpl> sessions;
    private final Map<String, CollaborationStatus> sessionStatuses;
    private final Map<String, List<CollaborationMessage>> sessionMessages;
    private final Map<String, CompletableFuture<CollaborationResult>> sessionFutures;
    private final ExecutorService executor;
    private ProtocolHandler protocolHandler;

    public CollaborationProtocolImpl() {
        this.sessions = new ConcurrentHashMap<>();
        this.sessionStatuses = new ConcurrentHashMap<>();
        this.sessionMessages = new ConcurrentHashMap<>();
        this.sessionFutures = new ConcurrentHashMap<>();
        this.executor = Executors.newCachedThreadPool();
    }

    @Override
    public CollaborationSession initiateCollaboration(
            String sessionId,
            String initiatorId,
            List<String> participants,
            String task) {

        if (sessionId == null || sessionId.trim().isEmpty()) {
            sessionId = "collab-" + UUID.randomUUID().toString().substring(0, 8);
        }

        if (sessions.containsKey(sessionId)) {
            log.warn("Collaboration session already exists: {}", sessionId);
            return sessions.get(sessionId);
        }

        CollaborationSessionImpl session = new CollaborationSessionImpl(
            sessionId, initiatorId, participants, task, System.currentTimeMillis()
        );

        sessions.put(sessionId, session);
        sessionStatuses.put(sessionId, CollaborationStatus.PENDING);
        sessionMessages.put(sessionId, new CopyOnWriteArrayList<>());
        sessionFutures.put(sessionId, new CompletableFuture<>());

        log.info("Initiated collaboration session: {} with {} participants", 
            sessionId, participants != null ? participants.size() : 0);

        // 发送邀请消息给所有参与者
        if (participants != null) {
            for (String participant : participants) {
                sendInvitation(sessionId, initiatorId, participant, task);
            }
        }

        // 激活会话
        sessionStatuses.put(sessionId, CollaborationStatus.ACTIVE);

        return session;
    }

    @Override
    public void sendCollaborationMessage(String sessionId, CollaborationMessage message) {
        if (sessionId == null || message == null) {
            return;
        }

        CollaborationSessionImpl session = sessions.get(sessionId);
        if (session == null) {
            log.warn("Cannot send message to non-existent session: {}", sessionId);
            return;
        }

        CollaborationStatus status = sessionStatuses.get(sessionId);
        if (status != CollaborationStatus.ACTIVE && status != CollaborationStatus.PENDING) {
            log.warn("Cannot send message to session {} with status {}", sessionId, status);
            return;
        }

        // 存储消息
        List<CollaborationMessage> messages = sessionMessages.get(sessionId);
        if (messages != null) {
            messages.add(message);
        }

        log.debug("Message sent in session {} from {}: {}", 
            sessionId, message.getFromAgentId(), message.getType());

        // 通知协议处理器
        if (protocolHandler != null) {
            try {
                protocolHandler.onMessage(sessionId, message);
            } catch (Exception e) {
                log.error("Error in protocol handler onMessage", e);
            }
        }

        // 检查是否是完成消息
        if (message.getType() == CollaborationMessage.CollaborationMessageType.TASK_RESULT) {
            checkCompletion(sessionId);
        }
    }

    @Override
    public CompletableFuture<CollaborationResult> awaitCompletion(String sessionId, long timeoutMs) {
        CompletableFuture<CollaborationResult> future = sessionFutures.get(sessionId);
        if (future == null) {
            return CompletableFuture.completedFuture(createFailedResult(sessionId, "Session not found"));
        }

        // Java 8 compatible timeout handling
        CompletableFuture<CollaborationResult> timeoutFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return future.get(timeoutMs, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                log.warn("Collaboration session {} timed out after {}ms", sessionId, timeoutMs);
                sessionStatuses.put(sessionId, CollaborationStatus.TIMEOUT);
                return createTimeoutResult(sessionId, timeoutMs);
            }
        });
        
        return timeoutFuture;
    }

    @Override
    public void endCollaboration(String sessionId) {
        CollaborationSessionImpl session = sessions.get(sessionId);
        if (session == null) {
            return;
        }

        CollaborationStatus currentStatus = sessionStatuses.get(sessionId);
        if (currentStatus == CollaborationStatus.COMPLETED || 
            currentStatus == CollaborationStatus.FAILED ||
            currentStatus == CollaborationStatus.TIMEOUT) {
            return;
        }

        log.info("Ending collaboration session: {}", sessionId);

        // 标记为完成
        sessionStatuses.put(sessionId, CollaborationStatus.COMPLETED);

        // 创建结果
        CollaborationResult result = createSuccessResult(sessionId);

        // 完成 future
        CompletableFuture<CollaborationResult> future = sessionFutures.get(sessionId);
        if (future != null && !future.isDone()) {
            future.complete(result);
        }

        // 通知处理器
        if (protocolHandler != null) {
            try {
                protocolHandler.onCompleted(sessionId, result);
            } catch (Exception e) {
                log.error("Error in protocol handler onCompleted", e);
            }
        }

        log.info("Collaboration session {} ended successfully", sessionId);
    }

    @Override
    public CollaborationStatus getStatus(String sessionId) {
        return sessionStatuses.getOrDefault(sessionId, CollaborationStatus.FAILED);
    }

    @Override
    public void setProtocolHandler(ProtocolHandler handler) {
        this.protocolHandler = handler;
        log.debug("Protocol handler set");
    }

    /**
     * 发送邀请
     */
    private void sendInvitation(String sessionId, String initiatorId, String participantId, String task) {
        CollaborationMessage invitation = new CollaborationMessage();
        invitation.setSessionId(sessionId);
        invitation.setFromAgentId(initiatorId);
        invitation.setToAgentId(participantId);
        invitation.setType(CollaborationMessage.CollaborationMessageType.SUGGESTION);
        invitation.setContent("Invitation to collaborate on: " + task);
        invitation.setTimestamp(System.currentTimeMillis());

        sendCollaborationMessage(sessionId, invitation);
    }

    /**
     * 检查是否所有参与者都完成了
     */
    private void checkCompletion(String sessionId) {
        CollaborationSessionImpl session = sessions.get(sessionId);
        if (session == null) {
            return;
        }

        List<CollaborationMessage> messages = sessionMessages.get(sessionId);
        if (messages == null) {
            return;
        }

        // 统计完成消息
        Set<String> completedParticipants = new HashSet<>();
        for (CollaborationMessage message : messages) {
            if (message.getType() == CollaborationMessage.CollaborationMessageType.TASK_RESULT) {
                completedParticipants.add(message.getFromAgentId());
            }
        }

        // 检查是否所有参与者都完成了
        List<String> participants = session.getParticipants();
        if (participants != null && completedParticipants.containsAll(participants)) {
            log.info("All participants completed in session {}", sessionId);
            endCollaboration(sessionId);
        }
    }

    /**
     * 创建成功结果
     */
    private CollaborationResult createSuccessResult(String sessionId) {
        CollaborationSessionImpl session = sessions.get(sessionId);
        List<CollaborationMessage> messages = sessionMessages.get(sessionId);

        CollaborationResult result = new CollaborationResult();
        result.setSessionId(sessionId);
        result.setSuccess(true);
        result.setSummary("Collaboration completed successfully");
        result.setContributions(extractContributions(messages));

        if (session != null) {
            result.setDuration(System.currentTimeMillis() - session.getStartTime());
        }

        return result;
    }

    /**
     * 创建失败结果
     */
    private CollaborationResult createFailedResult(String sessionId, String reason) {
        CollaborationResult result = new CollaborationResult();
        result.setSessionId(sessionId);
        result.setSuccess(false);
        result.setSummary("Collaboration failed: " + reason);
        result.setContributions(new ArrayList<>());
        result.setDuration(0);
        return result;
    }

    /**
     * 创建超时结果
     */
    private CollaborationResult createTimeoutResult(String sessionId, long timeoutMs) {
        CollaborationResult result = new CollaborationResult();
        result.setSessionId(sessionId);
        result.setSuccess(false);
        result.setSummary("Collaboration timed out after " + timeoutMs + "ms");
        result.setContributions(new ArrayList<>());
        result.setDuration(timeoutMs);
        return result;
    }

    /**
     * 从消息中提取贡献
     */
    private List<AgentContribution> extractContributions(List<CollaborationMessage> messages) {
        List<AgentContribution> contributions = new ArrayList<>();

        if (messages == null) {
            return contributions;
        }

        for (CollaborationMessage message : messages) {
            if (message.getType() == CollaborationMessage.CollaborationMessageType.TASK_RESULT ||
                message.getType() == CollaborationMessage.CollaborationMessageType.SUMMARY) {

                AgentContribution contribution = new AgentContribution();
                contribution.setAgentId(message.getFromAgentId());
                contribution.setAgentName("Agent-" + message.getFromAgentId());
                contribution.setContent(message.getContent());
                contribution.setTimestamp(message.getTimestamp());

                contributions.add(contribution);
            }
        }

        return contributions;
    }

    /**
     * 获取会话消息历史
     */
    public List<CollaborationMessage> getSessionMessages(String sessionId) {
        return new ArrayList<>(sessionMessages.getOrDefault(sessionId, Collections.emptyList()));
    }

    /**
     * 获取所有活跃会话
     */
    public Map<String, CollaborationSession> getActiveSessions() {
        Map<String, CollaborationSession> activeSessions = new HashMap<>();
        for (Map.Entry<String, CollaborationSessionImpl> entry : sessions.entrySet()) {
            CollaborationStatus status = sessionStatuses.get(entry.getKey());
            if (status == CollaborationStatus.ACTIVE || status == CollaborationStatus.PENDING) {
                activeSessions.put(entry.getKey(), entry.getValue());
            }
        }
        return activeSessions;
    }

    /**
     * 关闭协议实现
     */
    public void shutdown() {
        log.info("Shutting down CollaborationProtocol");

        // 结束所有活跃会话
        for (String sessionId : new ArrayList<>(sessions.keySet())) {
            endCollaboration(sessionId);
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 协作会话实现
     */
    private static class CollaborationSessionImpl implements CollaborationSession {
        private final String sessionId;
        private final String initiatorId;
        private final List<String> participants;
        private final String task;
        private final long startTime;

        public CollaborationSessionImpl(String sessionId, String initiatorId, 
                                        List<String> participants, String task, long startTime) {
            this.sessionId = sessionId;
            this.initiatorId = initiatorId;
            this.participants = new ArrayList<>(participants != null ? participants : Collections.emptyList());
            this.task = task;
            this.startTime = startTime;
        }

        @Override
        public String getSessionId() {
            return sessionId;
        }

        @Override
        public List<String> getParticipants() {
            return new ArrayList<>(participants);
        }

        @Override
        public String getTask() {
            return task;
        }

        @Override
        public long getStartTime() {
            return startTime;
        }

        public String getInitiatorId() {
            return initiatorId;
        }
    }
}
