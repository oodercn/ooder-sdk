package net.ooder.sdk.llm.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 协作会话实现类
 */
public class CollaborationSessionImpl implements CollaborationSession {
    
    private static final Logger log = LoggerFactory.getLogger(CollaborationSessionImpl.class);
    
    private final String sessionId;
    private final SceneChatImpl sceneChat;
    private final String task;
    private final ExecutorService executor;
    private final List<TaskAssignment> assignments;
    private final Map<String, AgentContribution> contributions;
    private final AtomicBoolean active;
    private final AtomicInteger completedTasks;
    private final CountDownLatch completionLatch;
    private final long startTime;
    
    public CollaborationSessionImpl(String sessionId, SceneChatImpl sceneChat, 
                                     String task, ExecutorService executor) {
        this.sessionId = sessionId;
        this.sceneChat = sceneChat;
        this.task = task;
        this.executor = executor;
        this.assignments = new CopyOnWriteArrayList<>();
        this.contributions = new ConcurrentHashMap<>();
        this.active = new AtomicBoolean(true);
        this.completedTasks = new AtomicInteger(0);
        this.completionLatch = new CountDownLatch(1);
        this.startTime = System.currentTimeMillis();
    }
    
    @Override
    public String getSessionId() {
        return sessionId;
    }
    
    @Override
    public void assignTasks(List<TaskAssignment> newAssignments) {
        if (!active.get()) {
            throw new IllegalStateException("Collaboration session is not active");
        }
        
        if (newAssignments == null || newAssignments.isEmpty()) {
            return;
        }
        
        assignments.addAll(newAssignments);
        log.info("Assigned {} tasks in session {}", newAssignments.size(), sessionId);
        
        // 启动任务执行
        for (TaskAssignment assignment : newAssignments) {
            executeTask(assignment);
        }
    }
    
    @Override
    public CompletableFuture<CollaborationResult> waitForCompletion(long timeoutMs) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                boolean completed = completionLatch.await(timeoutMs, TimeUnit.MILLISECONDS);
                
                CollaborationResult result = new CollaborationResult();
                result.setSessionId(sessionId);
                result.setSuccess(completed);
                result.setSummary(generateSummary());
                result.setContributions(new ArrayList<>(contributions.values()));
                
                return result;
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                CollaborationResult result = new CollaborationResult();
                result.setSessionId(sessionId);
                result.setSuccess(false);
                result.setSummary("Collaboration interrupted");
                result.setContributions(new ArrayList<>(contributions.values()));
                return result;
            }
        }, executor);
    }
    
    @Override
    public void end() {
        if (active.compareAndSet(true, false)) {
            log.info("Ending collaboration session: {}", sessionId);
            
            // 发送协作结束消息
            net.ooder.sdk.llm.model.SceneMessage endMessage = new net.ooder.sdk.llm.model.SceneMessage();
            endMessage.setMessageId("msg-" + UUID.randomUUID().toString().substring(0, 8));
            endMessage.setSceneId(sceneChat.getSceneId());
            endMessage.setSenderId("system");
            endMessage.setSenderType(net.ooder.sdk.llm.model.SceneMessage.SenderType.COLLABORATION);
            endMessage.setContent("Collaboration ended: " + task);
            endMessage.setTimestamp(System.currentTimeMillis());
            endMessage.setType(net.ooder.sdk.llm.model.SceneMessage.MessageType.EVENT);
            
            sceneChat.addToHistory(endMessage);
            sceneChat.notifyListeners(endMessage);
            
            // 触发完成
            completionLatch.countDown();
            
            log.info("Collaboration session {} ended", sessionId);
        }
    }
    
    /**
     * 执行任务
     */
    private void executeTask(TaskAssignment assignment) {
        executor.submit(() -> {
            try {
                log.debug("Executing task for agent {} in session {}", 
                    assignment.getAgentId(), sessionId);
                
                // 模拟任务执行时间
                long executionTime = 1000 + (long)(Math.random() * 2000);
                Thread.sleep(executionTime);
                
                // 记录贡献
                AgentContribution contribution = new AgentContribution();
                contribution.setAgentId(assignment.getAgentId());
                contribution.setAgentName("Agent-" + assignment.getAgentId());
                contribution.setContent("Completed subtask: " + assignment.getSubTask());
                contribution.setContributionTime(System.currentTimeMillis());
                
                contributions.put(assignment.getAgentId(), contribution);
                
                // 发送贡献消息
                net.ooder.sdk.llm.model.SceneMessage contributionMessage = new net.ooder.sdk.llm.model.SceneMessage();
                contributionMessage.setMessageId("msg-" + UUID.randomUUID().toString().substring(0, 8));
                contributionMessage.setSceneId(sceneChat.getSceneId());
                contributionMessage.setSenderId(assignment.getAgentId());
                contributionMessage.setSenderType(net.ooder.sdk.llm.model.SceneMessage.SenderType.AGENT);
                contributionMessage.setContent(contribution.getContent());
                contributionMessage.setTimestamp(contribution.getContributionTime());
                contributionMessage.setType(net.ooder.sdk.llm.model.SceneMessage.MessageType.COLLABORATION);
                
                sceneChat.addToHistory(contributionMessage);
                sceneChat.notifyListeners(contributionMessage);
                
                int completed = completedTasks.incrementAndGet();
                log.debug("Task completed for agent {} ({} of {})", 
                    assignment.getAgentId(), completed, assignments.size());
                
                // 检查是否所有任务完成
                if (completed >= assignments.size()) {
                    log.info("All tasks completed in session {}", sessionId);
                    completionLatch.countDown();
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Task execution interrupted for agent {} in session {}", 
                    assignment.getAgentId(), sessionId);
            }
        });
    }
    
    /**
     * 生成协作摘要
     */
    private String generateSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Collaboration Task: ").append(task).append("\n");
        summary.append("Duration: ").append(System.currentTimeMillis() - startTime).append("ms\n");
        summary.append("Total Assignments: ").append(assignments.size()).append("\n");
        summary.append("Completed Contributions: ").append(contributions.size()).append("\n");
        summary.append("Contributions:\n");
        
        for (AgentContribution contribution : contributions.values()) {
            summary.append("  - ").append(contribution.getAgentName())
                   .append(": ").append(contribution.getContent()).append("\n");
        }
        
        return summary.toString();
    }
}
