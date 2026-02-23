package net.ooder.sdk.msg.impl;

import net.ooder.sdk.msg.MsgClientConfig;
import net.ooder.sdk.msg.MsgClientProxy;
import net.ooder.sdk.msg.MsgListener;
import net.ooder.sdk.msg.MsgRecord;
import net.ooder.sdk.msg.MsgResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultMsgClientProxy implements MsgClientProxy {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultMsgClientProxy.class);
    
    private final String clientId;
    private final MsgClientConfig config;
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final AtomicLong msgIdCounter = new AtomicLong(0);
    
    private final Map<String, MsgListener> subscriptions = new ConcurrentHashMap<>();
    private final Map<String, List<MsgRecord>> offlineMessages = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> heartbeatTask;
    
    public DefaultMsgClientProxy() {
        this.config = new MsgClientConfig();
        this.clientId = "msg-" + UUID.randomUUID().toString().substring(0, 8);
        this.scheduler = Executors.newScheduledThreadPool(2);
    }
    
    public DefaultMsgClientProxy(MsgClientConfig config) {
        this.config = config != null ? config : new MsgClientConfig();
        this.clientId = this.config.getClientId() != null ? 
            this.config.getClientId() : "msg-" + UUID.randomUUID().toString().substring(0, 8);
        this.scheduler = Executors.newScheduledThreadPool(2);
    }
    
    @Override
    public void init(MsgClientConfig config) {
        log.info("Initializing MsgClientProxy: {}", clientId);
        
        if (config != null) {
            this.config.setBrokerUrl(config.getBrokerUrl());
            this.config.setUsername(config.getUsername());
            this.config.setPassword(config.getPassword());
            this.config.setReconnectInterval(config.getReconnectInterval());
            this.config.setMaxReconnectAttempts(config.getMaxReconnectAttempts());
        }
        
        connect();
        
        startHeartbeat();
        
        log.info("MsgClientProxy initialized: {}", clientId);
    }
    
    private void connect() {
        connected.set(true);
        log.info("Connected: {}", clientId);
    }
    
    private void startHeartbeat() {
        heartbeatTask = scheduler.scheduleAtFixedRate(() -> {
            if (connected.get()) {
                log.debug("Heartbeat: {}", clientId);
            }
        }, 30, 30, TimeUnit.SECONDS);
    }
    
    @Override
    public MsgResult sendToUser(String userId, String message) {
        if (!connected.get()) {
            return MsgResult.failure("NOT_CONNECTED", "Client not connected");
        }
        
        String msgId = generateMsgId();
        
        MsgRecord record = new MsgRecord();
        record.setMsgId(msgId);
        record.setToUserId(userId);
        record.setContent(message);
        record.setTimestamp(System.currentTimeMillis());
        
        log.debug("Message sent to user {}: {}", userId, msgId);
        
        return MsgResult.success(msgId);
    }
    
    @Override
    public MsgResult sendToGroup(String groupId, String message) {
        if (!connected.get()) {
            return MsgResult.failure("NOT_CONNECTED", "Client not connected");
        }
        
        String msgId = generateMsgId();
        
        MsgRecord record = new MsgRecord();
        record.setMsgId(msgId);
        record.setGroupId(groupId);
        record.setContent(message);
        record.setTimestamp(System.currentTimeMillis());
        
        log.debug("Message sent to group {}: {}", groupId, msgId);
        
        return MsgResult.success(msgId);
    }
    
    @Override
    public void subscribe(String topic, MsgListener listener) {
        if (topic == null || listener == null) {
            throw new IllegalArgumentException("Topic and listener cannot be null");
        }
        
        subscriptions.put(topic, listener);
        log.info("Subscribed to topic: {}", topic);
    }
    
    @Override
    public void unsubscribe(String topic) {
        if (topic == null) return;
        
        MsgListener removed = subscriptions.remove(topic);
        if (removed != null) {
            log.info("Unsubscribed from topic: {}", topic);
        }
    }
    
    @Override
    public MsgResult publish(String topic, String message) {
        if (!connected.get()) {
            return MsgResult.failure("NOT_CONNECTED", "Client not connected");
        }
        
        if (topic == null) {
            return MsgResult.failure("INVALID_TOPIC", "Topic cannot be null");
        }
        
        String msgId = generateMsgId();
        
        MsgRecord record = new MsgRecord();
        record.setMsgId(msgId);
        record.setTopic(topic);
        record.setContent(message);
        record.setTimestamp(System.currentTimeMillis());
        
        MsgListener listener = subscriptions.get(topic);
        if (listener != null) {
            scheduler.submit(() -> {
                try {
                    listener.onMessage(record);
                } catch (Exception e) {
                    log.warn("Listener error for topic: {}", topic, e);
                }
            });
        }
        
        log.debug("Published to topic {}: {}", topic, msgId);
        
        return MsgResult.success(msgId);
    }
    
    @Override
    public List<MsgRecord> getOfflineMessages(String userId) {
        List<MsgRecord> messages = offlineMessages.get(userId);
        if (messages != null) {
            offlineMessages.remove(userId);
            return messages;
        }
        return Collections.emptyList();
    }
    
    @Override
    public void ackMessage(String msgId) {
        log.debug("Message acknowledged: {}", msgId);
    }
    
    @Override
    public boolean recallMessage(String msgId) {
        log.debug("Message recalled: {}", msgId);
        return true;
    }
    
    @Override
    public void shutdown() {
        log.info("Shutting down MsgClientProxy: {}", clientId);
        
        if (heartbeatTask != null) {
            heartbeatTask.cancel(false);
        }
        
        connected.set(false);
        subscriptions.clear();
        offlineMessages.clear();
        scheduler.shutdown();
        
        log.info("MsgClientProxy shutdown complete: {}", clientId);
    }
    
    @Override
    public boolean isConnected() {
        return connected.get();
    }
    
    @Override
    public String getClientId() {
        return clientId;
    }
    
    private String generateMsgId() {
        return clientId + "-" + System.currentTimeMillis() + "-" + msgIdCounter.incrementAndGet();
    }
    
    public void simulateMessage(String topic, String content) {
        MsgRecord record = new MsgRecord();
        record.setMsgId(generateMsgId());
        record.setTopic(topic);
        record.setContent(content);
        record.setTimestamp(System.currentTimeMillis());
        
        MsgListener listener = subscriptions.get(topic);
        if (listener != null) {
            listener.onMessage(record);
        }
    }
    
    public void simulateConnectionLost() {
        connected.set(false);
        for (MsgListener listener : subscriptions.values()) {
            listener.onConnectionChanged(false);
        }
        log.warn("Connection lost: {}", clientId);
    }
    
    public void simulateConnectionRestored() {
        connected.set(true);
        for (MsgListener listener : subscriptions.values()) {
            listener.onConnectionChanged(true);
        }
        log.info("Connection restored: {}", clientId);
    }
}
