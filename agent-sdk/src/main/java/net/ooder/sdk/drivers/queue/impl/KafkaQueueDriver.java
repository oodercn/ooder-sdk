package net.ooder.sdk.drivers.queue.impl;

import net.ooder.sdk.driver.annotation.DriverImplementation;
import net.ooder.sdk.drivers.queue.QueueDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@DriverImplementation(value = "QueueDriver", skillId = "skill-queue-kafka")
public class KafkaQueueDriver implements QueueDriver {
    
    private static final Logger log = LoggerFactory.getLogger(KafkaQueueDriver.class);
    
    private final Map<String, BlockingQueue<QueueMessage>> queues = new ConcurrentHashMap<>();
    private final Map<String, List<MessageHandler>> subscribers = new ConcurrentHashMap<>();
    private final Map<String, CopyOnWriteArrayList<QueueMessage>> topics = new ConcurrentHashMap<>();
    private final Map<String, Map<String, MessageHandler>> topicSubscriptions = new ConcurrentHashMap<>();
    
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final AtomicLong messageIdCounter = new AtomicLong(0);
    private final AtomicBoolean connected = new AtomicBoolean(false);
    
    @Override
    public void init(QueueConfig config) {
        connected.set(true);
        log.info("Kafka queue initialized (simulated mode)");
    }
    
    @Override
    public CompletableFuture<Void> createQueue(String queueName, Map<String, Object> options) {
        return CompletableFuture.runAsync(() -> {
            queues.computeIfAbsent(queueName, k -> new LinkedBlockingQueue<>());
            log.info("Queue created: {}", queueName);
        });
    }
    
    @Override
    public CompletableFuture<Void> deleteQueue(String queueName) {
        return CompletableFuture.runAsync(() -> {
            queues.remove(queueName);
            subscribers.remove(queueName);
            log.info("Queue deleted: {}", queueName);
        });
    }
    
    @Override
    public CompletableFuture<List<String>> listQueues() {
        return CompletableFuture.supplyAsync(() -> new ArrayList<>(queues.keySet()));
    }
    
    @Override
    public CompletableFuture<String> publish(String queueName, Object message) {
        return publish(queueName, message, null);
    }
    
    @Override
    public CompletableFuture<String> publish(String queueName, Object message, Map<String, Object> headers) {
        return CompletableFuture.supplyAsync(() -> {
            BlockingQueue<QueueMessage> queue = queues.computeIfAbsent(queueName, k -> new LinkedBlockingQueue<>());
            
            QueueMessage msg = new QueueMessage();
            msg.setMessageId(generateMessageId());
            msg.setBody(message);
            msg.setHeaders(headers != null ? headers : new HashMap<>());
            msg.setTimestamp(System.currentTimeMillis());
            msg.setDeliveryCount(0);
            
            queue.offer(msg);
            
            notifySubscribers(queueName, msg);
            
            log.debug("Message published to queue {}: {}", queueName, msg.getMessageId());
            return msg.getMessageId();
        });
    }
    
    @Override
    public CompletableFuture<Void> publishBatch(String queueName, List<Object> messages) {
        return CompletableFuture.runAsync(() -> {
            for (Object message : messages) {
                publish(queueName, message).join();
            }
        });
    }
    
    @Override
    public CompletableFuture<QueueMessage> consume(String queueName, long timeoutMs) {
        return CompletableFuture.supplyAsync(() -> {
            BlockingQueue<QueueMessage> queue = queues.get(queueName);
            if (queue == null) {
                return null;
            }
            
            try {
                QueueMessage msg = queue.poll(timeoutMs, TimeUnit.MILLISECONDS);
                if (msg != null) {
                    msg.setDeliveryCount(msg.getDeliveryCount() + 1);
                }
                return msg;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        });
    }
    
    @Override
    public CompletableFuture<List<QueueMessage>> consumeBatch(String queueName, int maxMessages, long timeoutMs) {
        return CompletableFuture.supplyAsync(() -> {
            List<QueueMessage> messages = new ArrayList<>();
            BlockingQueue<QueueMessage> queue = queues.get(queueName);
            if (queue == null) {
                return messages;
            }
            
            long endTime = System.currentTimeMillis() + timeoutMs;
            while (messages.size() < maxMessages && System.currentTimeMillis() < endTime) {
                try {
                    QueueMessage msg = queue.poll(100, TimeUnit.MILLISECONDS);
                    if (msg != null) {
                        msg.setDeliveryCount(msg.getDeliveryCount() + 1);
                        messages.add(msg);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            return messages;
        });
    }
    
    @Override
    public CompletableFuture<Void> ack(String queueName, String messageId) {
        return CompletableFuture.completedFuture(null);
    }
    
    @Override
    public CompletableFuture<Void> nack(String queueName, String messageId) {
        return CompletableFuture.runAsync(() -> {
            log.debug("Message nacked: {}", messageId);
        });
    }
    
    @Override
    public CompletableFuture<Void> subscribe(String queueName, MessageHandler handler) {
        return CompletableFuture.runAsync(() -> {
            subscribers.computeIfAbsent(queueName, k -> new CopyOnWriteArrayList<>()).add(handler);
            log.info("Subscribed to queue: {}", queueName);
        });
    }
    
    @Override
    public CompletableFuture<Void> unsubscribe(String queueName) {
        return CompletableFuture.runAsync(() -> {
            subscribers.remove(queueName);
            log.info("Unsubscribed from queue: {}", queueName);
        });
    }
    
    @Override
    public CompletableFuture<Long> getQueueSize(String queueName) {
        return CompletableFuture.supplyAsync(() -> {
            BlockingQueue<QueueMessage> queue = queues.get(queueName);
            return queue != null ? (long) queue.size() : 0L;
        });
    }
    
    @Override
    public CompletableFuture<Void> purgeQueue(String queueName) {
        return CompletableFuture.runAsync(() -> {
            BlockingQueue<QueueMessage> queue = queues.get(queueName);
            if (queue != null) {
                queue.clear();
            }
            log.info("Queue purged: {}", queueName);
        });
    }
    
    @Override
    public CompletableFuture<Void> createTopic(String topicName) {
        return CompletableFuture.runAsync(() -> {
            topics.computeIfAbsent(topicName, k -> new CopyOnWriteArrayList<>());
            log.info("Topic created: {}", topicName);
        });
    }
    
    @Override
    public CompletableFuture<Void> deleteTopic(String topicName) {
        return CompletableFuture.runAsync(() -> {
            topics.remove(topicName);
            topicSubscriptions.remove(topicName);
            log.info("Topic deleted: {}", topicName);
        });
    }
    
    @Override
    public CompletableFuture<List<String>> listTopics() {
        return CompletableFuture.supplyAsync(() -> new ArrayList<>(topics.keySet()));
    }
    
    @Override
    public CompletableFuture<Void> publishToTopic(String topicName, Object message) {
        return CompletableFuture.runAsync(() -> {
            QueueMessage msg = new QueueMessage();
            msg.setMessageId(generateMessageId());
            msg.setBody(message);
            msg.setTimestamp(System.currentTimeMillis());
            
            topics.computeIfAbsent(topicName, k -> new CopyOnWriteArrayList<>()).add(msg);
            
            Map<String, MessageHandler> subs = topicSubscriptions.get(topicName);
            if (subs != null) {
                for (MessageHandler handler : subs.values()) {
                    executor.submit(() -> {
                        try {
                            handler.handle(msg);
                        } catch (Exception e) {
                            handler.onError(e);
                        }
                    });
                }
            }
            
            log.debug("Message published to topic {}: {}", topicName, msg.getMessageId());
        });
    }
    
    @Override
    public CompletableFuture<Void> subscribeToTopic(String topicName, String subscriptionName, MessageHandler handler) {
        return CompletableFuture.runAsync(() -> {
            topicSubscriptions.computeIfAbsent(topicName, k -> new ConcurrentHashMap<>()).put(subscriptionName, handler);
            log.info("Subscribed to topic {} with subscription {}", topicName, subscriptionName);
        });
    }
    
    @Override
    public CompletableFuture<Void> unsubscribeFromTopic(String topicName, String subscriptionName) {
        return CompletableFuture.runAsync(() -> {
            Map<String, MessageHandler> subs = topicSubscriptions.get(topicName);
            if (subs != null) {
                subs.remove(subscriptionName);
            }
            log.info("Unsubscribed from topic {} with subscription {}", topicName, subscriptionName);
        });
    }
    
    @Override
    public void close() {
        executor.shutdown();
        queues.clear();
        subscribers.clear();
        topics.clear();
        topicSubscriptions.clear();
        connected.set(false);
        log.info("Kafka queue closed");
    }
    
    @Override
    public boolean isConnected() {
        return connected.get();
    }
    
    @Override
    public String getDriverName() {
        return "Kafka";
    }
    
    @Override
    public String getDriverVersion() {
        return "1.0.0";
    }
    
    private String generateMessageId() {
        return "msg-" + System.currentTimeMillis() + "-" + messageIdCounter.incrementAndGet();
    }
    
    private void notifySubscribers(String queueName, QueueMessage msg) {
        List<MessageHandler> handlers = subscribers.get(queueName);
        if (handlers != null) {
            for (MessageHandler handler : handlers) {
                executor.submit(() -> {
                    try {
                        handler.handle(msg);
                    } catch (Exception e) {
                        handler.onError(e);
                    }
                });
            }
        }
    }
}
