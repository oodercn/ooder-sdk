package net.ooder.scene.config;

/**
 * 统一接口配置属性
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class UnifiedInterfaceProperties {

    private SessionConfig session = new SessionConfig();
    private AgentConfig agent = new AgentConfig();
    private MessageConfig message = new MessageConfig();
    private A2AConfig a2a = new A2AConfig();

    public SessionConfig getSession() {
        return session;
    }

    public void setSession(SessionConfig session) {
        this.session = session;
    }

    public AgentConfig getAgent() {
        return agent;
    }

    public void setAgent(AgentConfig agent) {
        this.agent = agent;
    }

    public MessageConfig getMessage() {
        return message;
    }

    public void setMessage(MessageConfig message) {
        this.message = message;
    }

    public A2AConfig getA2a() {
        return a2a;
    }

    public void setA2a(A2AConfig a2a) {
        this.a2a = a2a;
    }

    public static class SessionConfig {
        private boolean enabled = true;
        private String storageRoot = "data/sessions";
        private long defaultTtl = 86400000;
        private int cleanupInterval = 3600000;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        
        public String getStorageRoot() { return storageRoot; }
        public void setStorageRoot(String storageRoot) { this.storageRoot = storageRoot; }
        
        public long getDefaultTtl() { return defaultTtl; }
        public void setDefaultTtl(long defaultTtl) { this.defaultTtl = defaultTtl; }
        
        public int getCleanupInterval() { return cleanupInterval; }
        public void setCleanupInterval(int cleanupInterval) { this.cleanupInterval = cleanupInterval; }
    }

    public static class AgentConfig {
        private boolean enabled = true;
        private int defaultHeartbeatInterval = 30000;
        private int defaultHeartbeatTimeout = 60000;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        
        public int getDefaultHeartbeatInterval() { return defaultHeartbeatInterval; }
        public void setDefaultHeartbeatInterval(int defaultHeartbeatInterval) { this.defaultHeartbeatInterval = defaultHeartbeatInterval; }
        
        public int getDefaultHeartbeatTimeout() { return defaultHeartbeatTimeout; }
        public void setDefaultHeartbeatTimeout(int defaultHeartbeatTimeout) { this.defaultHeartbeatTimeout = defaultHeartbeatTimeout; }
    }

    public static class MessageConfig {
        private boolean enabled = true;
        private String storageRoot = "data/messages";
        private boolean persistenceEnabled = true;
        private int maxQueueSize = 10000;
        private long defaultMessageTtl = 86400000;
        private int defaultMaxRetries = 3;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        
        public String getStorageRoot() { return storageRoot; }
        public void setStorageRoot(String storageRoot) { this.storageRoot = storageRoot; }
        
        public boolean isPersistenceEnabled() { return persistenceEnabled; }
        public void setPersistenceEnabled(boolean persistenceEnabled) { this.persistenceEnabled = persistenceEnabled; }
        
        public int getMaxQueueSize() { return maxQueueSize; }
        public void setMaxQueueSize(int maxQueueSize) { this.maxQueueSize = maxQueueSize; }
        
        public long getDefaultMessageTtl() { return defaultMessageTtl; }
        public void setDefaultMessageTtl(long defaultMessageTtl) { this.defaultMessageTtl = defaultMessageTtl; }
        
        public int getDefaultMaxRetries() { return defaultMaxRetries; }
        public void setDefaultMaxRetries(int defaultMaxRetries) { this.defaultMaxRetries = defaultMaxRetries; }
    }

    public static class A2AConfig {
        private boolean enabled = true;
        private String protocolVersion = "1.0";
        private int defaultRequestTimeout = 30000;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        
        public String getProtocolVersion() { return protocolVersion; }
        public void setProtocolVersion(String protocolVersion) { this.protocolVersion = protocolVersion; }
        
        public int getDefaultRequestTimeout() { return defaultRequestTimeout; }
        public void setDefaultRequestTimeout(int defaultRequestTimeout) { this.defaultRequestTimeout = defaultRequestTimeout; }
    }
}
