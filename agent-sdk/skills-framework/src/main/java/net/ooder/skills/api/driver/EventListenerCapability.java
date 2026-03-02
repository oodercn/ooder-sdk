package net.ooder.skills.api.driver;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 事件监听能力接口
 *
 * 监听业务事件并触发能力调用的驱动能力
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface EventListenerCapability {

    /**
     * 订阅事件
     *
     * @param eventType 事件类型
     * @param filter 事件过滤器
     * @param handler 事件处理器
     * @return 订阅信息
     */
    CompletableFuture<Subscription> subscribe(String eventType, EventFilter filter, EventHandler handler);

    /**
     * 取消订阅
     *
     * @param subscriptionId 订阅ID
     * @return 是否成功
     */
    CompletableFuture<Boolean> unsubscribe(String subscriptionId);

    /**
     * 暂停订阅
     *
     * @param subscriptionId 订阅ID
     * @return 是否成功
     */
    CompletableFuture<Boolean> pause(String subscriptionId);

    /**
     * 恢复订阅
     *
     * @param subscriptionId 订阅ID
     * @return 是否成功
     */
    CompletableFuture<Boolean> resume(String subscriptionId);

    /**
     * 获取订阅信息
     *
     * @param subscriptionId 订阅ID
     * @return 订阅信息
     */
    CompletableFuture<Subscription> getSubscription(String subscriptionId);

    /**
     * 列出所有订阅
     *
     * @return 订阅列表
     */
    CompletableFuture<List<Subscription>> listSubscriptions();

    /**
     * 列出指定事件类型的订阅
     *
     * @param eventType 事件类型
     * @return 订阅列表
     */
    CompletableFuture<List<Subscription>> listSubscriptionsByType(String eventType);

    /**
     * 发布事件
     *
     * @param event 事件对象
     * @return 发布结果
     */
    CompletableFuture<Boolean> publish(Event event);

    /**
     * 添加事件监听器
     *
     * @param listener 监听器
     */
    void addEventListener(EventBusListener listener);

    /**
     * 移除事件监听器
     *
     * @param listener 监听器
     */
    void removeEventListener(EventBusListener listener);

    /**
     * 订阅信息
     */
    class Subscription {
        private String subscriptionId;
        private String eventType;
        private SubscriptionStatus status;
        private EventFilter filter;
        private long createTime;
        private long lastEventTime;
        private int eventCount;
        private Map<String, Object> metadata;

        public enum SubscriptionStatus {
            ACTIVE,     // 活跃
            PAUSED,     // 已暂停
            CANCELLED   // 已取消
        }

        // Getters and Setters
        public String getSubscriptionId() { return subscriptionId; }
        public void setSubscriptionId(String subscriptionId) { this.subscriptionId = subscriptionId; }
        public String getEventType() { return eventType; }
        public void setEventType(String eventType) { this.eventType = eventType; }
        public SubscriptionStatus getStatus() { return status; }
        public void setStatus(SubscriptionStatus status) { this.status = status; }
        public EventFilter getFilter() { return filter; }
        public void setFilter(EventFilter filter) { this.filter = filter; }
        public long getCreateTime() { return createTime; }
        public void setCreateTime(long createTime) { this.createTime = createTime; }
        public long getLastEventTime() { return lastEventTime; }
        public void setLastEventTime(long lastEventTime) { this.lastEventTime = lastEventTime; }
        public int getEventCount() { return eventCount; }
        public void setEventCount(int eventCount) { this.eventCount = eventCount; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }

    /**
     * 事件过滤器
     */
    interface EventFilter {
        /**
         * 是否接受事件
         *
         * @param event 事件对象
         * @return 是否接受
         */
        boolean accept(Event event);
    }

    /**
     * 事件处理器
     */
    interface EventHandler {
        /**
         * 处理事件
         *
         * @param event 事件对象
         * @return 处理结果
         */
        CompletableFuture<EventResult> handle(Event event);
    }

    /**
     * 事件对象
     */
    class Event {
        private String eventId;
        private String eventType;
        private String source;
        private Map<String, Object> payload;
        private long timestamp;
        private int priority;

        // Getters and Setters
        public String getEventId() { return eventId; }
        public void setEventId(String eventId) { this.eventId = eventId; }
        public String getEventType() { return eventType; }
        public void setEventType(String eventType) { this.eventType = eventType; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public Map<String, Object> getPayload() { return payload; }
        public void setPayload(Map<String, Object> payload) { this.payload = payload; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
    }

    /**
     * 事件处理结果
     */
    class EventResult {
        private boolean success;
        private String message;
        private Map<String, Object> result;

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Map<String, Object> getResult() { return result; }
        public void setResult(Map<String, Object> result) { this.result = result; }
    }

    /**
     * 事件总线监听器
     */
    interface EventBusListener {
        void onSubscriptionCreated(Subscription subscription);
        void onSubscriptionCancelled(Subscription subscription);
        void onEventPublished(Event event);
        void onEventHandled(Subscription subscription, EventResult result);
    }
}
