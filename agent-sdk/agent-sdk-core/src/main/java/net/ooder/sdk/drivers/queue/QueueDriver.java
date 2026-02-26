package net.ooder.sdk.drivers.queue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface QueueDriver {
    
    void init(QueueConfig config);
    
    CompletableFuture<Void> createQueue(String queueName, Map<String, Object> options);
    
    CompletableFuture<Void> deleteQueue(String queueName);
    
    CompletableFuture<List<String>> listQueues();
    
    CompletableFuture<String> publish(String queueName, Object message);
    
    CompletableFuture<String> publish(String queueName, Object message, Map<String, Object> headers);
    
    CompletableFuture<Void> publishBatch(String queueName, List<Object> messages);
    
    CompletableFuture<QueueMessage> consume(String queueName, long timeoutMs);
    
    CompletableFuture<List<QueueMessage>> consumeBatch(String queueName, int maxMessages, long timeoutMs);
    
    CompletableFuture<Void> ack(String queueName, String messageId);
    
    CompletableFuture<Void> nack(String queueName, String messageId);
    
    CompletableFuture<Void> subscribe(String queueName, MessageHandler handler);
    
    CompletableFuture<Void> unsubscribe(String queueName);
    
    CompletableFuture<Long> getQueueSize(String queueName);
    
    CompletableFuture<Void> purgeQueue(String queueName);
    
    CompletableFuture<Void> createTopic(String topicName);
    
    CompletableFuture<Void> deleteTopic(String topicName);
    
    CompletableFuture<List<String>> listTopics();
    
    CompletableFuture<Void> publishToTopic(String topicName, Object message);
    
    CompletableFuture<Void> subscribeToTopic(String topicName, String subscriptionName, MessageHandler handler);
    
    CompletableFuture<Void> unsubscribeFromTopic(String topicName, String subscriptionName);
    
    void close();
    
    boolean isConnected();
    
    String getDriverName();
    
    String getDriverVersion();
    
    class QueueConfig {
        private String host;
        private int port;
        private String username;
        private String password;
        private String virtualHost;
        private int maxConnections = 10;
        private int prefetchCount = 10;
        private Map<String, Object> properties = new java.util.concurrent.ConcurrentHashMap<>();
        
        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }
        
        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getVirtualHost() { return virtualHost; }
        public void setVirtualHost(String virtualHost) { this.virtualHost = virtualHost; }
        
        public int getMaxConnections() { return maxConnections; }
        public void setMaxConnections(int maxConnections) { this.maxConnections = maxConnections; }
        
        public int getPrefetchCount() { return prefetchCount; }
        public void setPrefetchCount(int prefetchCount) { this.prefetchCount = prefetchCount; }
        
        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    }
    
    class QueueMessage {
        private String messageId;
        private Object body;
        private Map<String, Object> headers;
        private long timestamp;
        private int deliveryCount;
        
        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }
        
        public Object getBody() { return body; }
        public void setBody(Object body) { this.body = body; }
        
        public Map<String, Object> getHeaders() { return headers; }
        public void setHeaders(Map<String, Object> headers) { this.headers = headers; }
        
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        
        public int getDeliveryCount() { return deliveryCount; }
        public void setDeliveryCount(int deliveryCount) { this.deliveryCount = deliveryCount; }
    }
    
    interface MessageHandler {
        void handle(QueueMessage message);
        void onError(Throwable error);
    }
}
