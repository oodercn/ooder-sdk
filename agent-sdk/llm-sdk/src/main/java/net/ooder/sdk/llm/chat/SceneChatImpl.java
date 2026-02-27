package net.ooder.sdk.llm.chat;

import net.ooder.sdk.llm.model.AgentInfo;
import net.ooder.sdk.llm.model.SceneMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 场景对话实现类
 */
public class SceneChatImpl implements SceneChat {
    
    private static final Logger log = LoggerFactory.getLogger(SceneChatImpl.class);
    
    private final String sceneId;
    private final List<AgentInfo> agents;
    private final List<SceneMessage> history;
    private final List<SceneMessageListener> listeners;
    private final ExecutorService executor;
    private final AtomicBoolean closed;
    private CollaborationSession activeCollaboration;
    
    public SceneChatImpl(String sceneId) {
        this.sceneId = sceneId;
        this.agents = new CopyOnWriteArrayList<>();
        this.history = new CopyOnWriteArrayList<>();
        this.listeners = new CopyOnWriteArrayList<>();
        this.executor = Executors.newCachedThreadPool();
        this.closed = new AtomicBoolean(false);
    }
    
    /**
     * 添加智能体到场景
     * @param agent 智能体信息
     */
    public void addAgent(AgentInfo agent) {
        if (agent == null || agent.getAgentId() == null) {
            throw new IllegalArgumentException("Agent and agentId cannot be null");
        }
        
        // 检查是否已存在
        for (AgentInfo existing : agents) {
            if (existing.getAgentId().equals(agent.getAgentId())) {
                log.warn("Agent {} already exists in scene {}", agent.getAgentId(), sceneId);
                return;
            }
        }
        
        agents.add(agent);
        log.info("Added agent {} to scene {}", agent.getAgentId(), sceneId);
        
        // 发送系统消息
        SceneMessage joinMessage = createSystemMessage(
            "Agent " + agent.getName() + " joined the scene"
        );
        addToHistory(joinMessage);
        notifyListeners(joinMessage);
    }
    
    /**
     * 从场景移除智能体
     * @param agentId 智能体ID
     */
    public void removeAgent(String agentId) {
        agents.removeIf(agent -> agent.getAgentId().equals(agentId));
        log.info("Removed agent {} from scene {}", agentId, sceneId);
    }
    
    @Override
    public String getSceneId() {
        return sceneId;
    }
    
    @Override
    public List<AgentInfo> getAgents() {
        return new ArrayList<>(agents);
    }
    
    @Override
    public String broadcastMessage(String message) {
        checkNotClosed();
        
        String messageId = generateMessageId();
        SceneMessage sceneMessage = new SceneMessage();
        sceneMessage.setMessageId(messageId);
        sceneMessage.setSceneId(sceneId);
        sceneMessage.setSenderId("user");
        sceneMessage.setSenderType(SceneMessage.SenderType.USER);
        sceneMessage.setContent(message);
        sceneMessage.setTimestamp(System.currentTimeMillis());
        sceneMessage.setType(SceneMessage.MessageType.TEXT);
        
        // 广播给所有智能体
        List<String> targetAgentIds = new ArrayList<>();
        for (AgentInfo agent : agents) {
            if (agent.isOnline()) {
                targetAgentIds.add(agent.getAgentId());
            }
        }
        sceneMessage.setTargetAgents(targetAgentIds);
        
        addToHistory(sceneMessage);
        notifyListeners(sceneMessage);
        
        log.debug("Broadcast message {} to {} agents in scene {}", 
            messageId, targetAgentIds.size(), sceneId);
        
        // 异步触发智能体响应
        executor.submit(() -> triggerAgentResponses(sceneMessage));
        
        return messageId;
    }
    
    @Override
    public String sendToAgent(String agentId, String message) {
        checkNotClosed();
        
        // 验证智能体存在
        final AgentInfo[] targetAgentHolder = new AgentInfo[1];
        for (AgentInfo agent : agents) {
            if (agent.getAgentId().equals(agentId)) {
                targetAgentHolder[0] = agent;
                break;
            }
        }
        
        if (targetAgentHolder[0] == null) {
            throw new IllegalArgumentException("Agent not found: " + agentId);
        }
        final AgentInfo targetAgent = targetAgentHolder[0];
        
        String messageId = generateMessageId();
        SceneMessage sceneMessage = new SceneMessage();
        sceneMessage.setMessageId(messageId);
        sceneMessage.setSceneId(sceneId);
        sceneMessage.setSenderId("user");
        sceneMessage.setSenderType(SceneMessage.SenderType.USER);
        sceneMessage.setContent(message);
        sceneMessage.setTimestamp(System.currentTimeMillis());
        sceneMessage.setType(SceneMessage.MessageType.TEXT);
        sceneMessage.setTargetAgents(Collections.singletonList(agentId));
        
        addToHistory(sceneMessage);
        notifyListeners(sceneMessage);
        
        log.debug("Sent message {} to agent {} in scene {}", messageId, agentId, sceneId);
        
        // 异步触发智能体响应
        executor.submit(() -> triggerAgentResponse(targetAgent, sceneMessage));
        
        return messageId;
    }
    
    @Override
    public List<SceneMessage> getHistory() {
        return new ArrayList<>(history);
    }
    
    @Override
    public void subscribe(SceneMessageListener listener) {
        if (listener != null) {
            listeners.add(listener);
            log.debug("Added message listener to scene {}", sceneId);
        }
    }
    
    @Override
    public void unsubscribe(SceneMessageListener listener) {
        listeners.remove(listener);
        log.debug("Removed message listener from scene {}", sceneId);
    }
    
    @Override
    public CollaborationSession startCollaboration(String task) {
        checkNotClosed();
        
        if (activeCollaboration != null) {
            log.warn("Active collaboration already exists in scene {}, ending it first", sceneId);
            activeCollaboration.end();
        }
        
        CollaborationSessionImpl session = new CollaborationSessionImpl(
            generateSessionId(), this, task, executor
        );
        activeCollaboration = session;
        
        // 发送协作开始消息
        SceneMessage startMessage = createSystemMessage(
            "Collaboration started: " + task
        );
        startMessage.setType(SceneMessage.MessageType.COLLABORATION);
        addToHistory(startMessage);
        notifyListeners(startMessage);
        
        log.info("Started collaboration session {} in scene {}", session.getSessionId(), sceneId);
        
        return session;
    }
    
    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            log.info("Closing scene chat: {}", sceneId);
            
            // 结束活跃协作
            if (activeCollaboration != null) {
                activeCollaboration.end();
                activeCollaboration = null;
            }
            
            // 发送关闭消息
            SceneMessage closeMessage = createSystemMessage("Scene closed");
            addToHistory(closeMessage);
            notifyListeners(closeMessage);
            
            // 清空监听器
            listeners.clear();
            
            // 关闭线程池
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
            
            log.info("Scene chat closed: {}", sceneId);
        }
    }
    
    /**
     * 内部方法：添加消息到历史记录
     */
    void addToHistory(SceneMessage message) {
        history.add(message);
        
        // 限制历史记录大小
        if (history.size() > 1000) {
            history.remove(0);
        }
    }
    
    /**
     * 内部方法：通知所有监听器
     */
    void notifyListeners(SceneMessage message) {
        for (SceneMessageListener listener : listeners) {
            try {
                listener.onMessage(message);
            } catch (Exception e) {
                log.error("Error notifying listener", e);
            }
        }
    }
    
    /**
     * 内部方法：触发所有智能体响应
     */
    private void triggerAgentResponses(SceneMessage userMessage) {
        for (AgentInfo agent : agents) {
            if (agent.isOnline()) {
                triggerAgentResponse(agent, userMessage);
            }
        }
    }
    
    /**
     * 内部方法：触发单个智能体响应
     */
    private void triggerAgentResponse(AgentInfo agent, SceneMessage userMessage) {
        // 模拟智能体响应（实际实现中应调用LLM或Agent SDK）
        executor.submit(() -> {
            try {
                // 模拟处理延迟
                Thread.sleep(500 + (long)(Math.random() * 1000));
                
                String responseContent = generateAgentResponse(agent, userMessage);
                
                SceneMessage responseMessage = new SceneMessage();
                responseMessage.setMessageId(generateMessageId());
                responseMessage.setSceneId(sceneId);
                responseMessage.setSenderId(agent.getAgentId());
                responseMessage.setSenderType(SceneMessage.SenderType.AGENT);
                responseMessage.setContent(responseContent);
                responseMessage.setTimestamp(System.currentTimeMillis());
                responseMessage.setType(SceneMessage.MessageType.TEXT);
                responseMessage.setReplyTo(userMessage.getMessageId());
                
                addToHistory(responseMessage);
                notifyListeners(responseMessage);
                
                log.debug("Agent {} responded in scene {}", agent.getAgentId(), sceneId);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
    
    /**
     * 生成智能体响应（模拟）
     */
    private String generateAgentResponse(AgentInfo agent, SceneMessage userMessage) {
        String userContent = userMessage.getContent();
        
        // 简单的响应逻辑（实际应调用LLM）
        if (userContent.contains("?") || userContent.contains("？")) {
            return agent.getName() + ": 这是一个很好的问题。基于我的能力 " + 
                   agent.getCapabilities() + "，我建议...";
        } else {
            return agent.getName() + ": 收到，我将基于 " + agent.getCapabilities() + 
                   " 来处理这个任务。";
        }
    }
    
    /**
     * 创建系统消息
     */
    private SceneMessage createSystemMessage(String content) {
        SceneMessage message = new SceneMessage();
        message.setMessageId(generateMessageId());
        message.setSceneId(sceneId);
        message.setSenderId("system");
        message.setSenderType(SceneMessage.SenderType.COLLABORATION);
        message.setContent(content);
        message.setTimestamp(System.currentTimeMillis());
        message.setType(SceneMessage.MessageType.EVENT);
        return message;
    }
    
    private String generateMessageId() {
        return "msg-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    private String generateSessionId() {
        return "collab-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    private void checkNotClosed() {
        if (closed.get()) {
            throw new IllegalStateException("Scene chat is closed");
        }
    }
}
