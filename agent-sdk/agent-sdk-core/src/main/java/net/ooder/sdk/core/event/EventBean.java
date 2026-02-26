package net.ooder.sdk.core.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Core 层事件管理器（内核级）
 *
 * <p>设计原则：</p>
 * <ul>
 *   <li><b>无状态</b>：EventBean 本身不维护业务状态</li>
 *   <li><b>高性能</b>：最小化锁竞争，支持高频事件发布</li>
 *   <li><b>隔离性</b>：Core 层事件与 Engine 层事件完全隔离</li>
 *   <li><b>容错性</b>：监听器异常不影响其他监听器和主流程</li>
 * </ul>
 *
 * <p>使用方式：</p>
 * <pre>
 * // 发布事件
 * EventBean.getInstance().publish(new SkillStateChangedEvent(skillId, oldState, newState));
 *
 * // 订阅事件
 * EventBean.getInstance().subscribe(SkillStateChangedEvent.class, event -> {
 *     // 处理事件
 * });
 * </pre>
 *
 * @author Ooder Team
 * @version 2.3
 */
public final class EventBean {

    private static final Logger log = LoggerFactory.getLogger(EventBean.class);

    /**
     * 单例实例
     */
    private static final EventBean INSTANCE = new EventBean();

    /**
     * 事件监听器注册表
     * Key: 事件类型, Value: 监听器列表
     */
    private final Map<Class<? extends CoreEvent>, CopyOnWriteArrayList<ListenerHolder<?>>> listeners;

    /**
     * 异步处理线程池
     */
    private final ExecutorService asyncExecutor;

    /**
     * 事件统计
     */
    private final EventStatistics statistics;

    /**
     * 是否已关闭
     */
    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    private EventBean() {
        this.listeners = new ConcurrentHashMap<>();
        this.asyncExecutor = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "EventBean-Async-" + System.currentTimeMillis());
            t.setDaemon(true);
            return t;
        });
        this.statistics = new EventStatistics();
    }

    /**
     * 获取 EventBean 单例实例
     */
    public static EventBean getInstance() {
        return INSTANCE;
    }

    /**
     * 发布事件
     *
     * <p>事件发布是同步的，但监听器可以选择异步执行</p>
     *
     * @param event 要发布的事件
     * @param <T>   事件类型
     */
    @SuppressWarnings("unchecked")
    public <T extends CoreEvent> void publish(T event) {
        if (shutdown.get()) {
            log.warn("EventBean is shutdown, event {} will be discarded", event.getEventType());
            return;
        }

        if (event == null) {
            log.warn("Cannot publish null event");
            return;
        }

        statistics.recordPublished(event.getClass());

        // 获取该事件类型的监听器
        List<ListenerHolder<?>> eventListeners = listeners.get(event.getClass());
        if (eventListeners == null || eventListeners.isEmpty()) {
            return;
        }

        // 遍历并通知监听器
        for (ListenerHolder<?> holder : eventListeners) {
            try {
                ListenerHolder<T> typedHolder = (ListenerHolder<T>) holder;
                notifyListener(typedHolder, event);
            } catch (Exception e) {
                log.error("Failed to notify listener {} for event {}",
                    holder.getListenerName(), event.getEventType(), e);
            }
        }
    }

    /**
     * 通知单个监听器
     */
    private <T extends CoreEvent> void notifyListener(ListenerHolder<T> holder, T event) {
        CoreEventListener<T> listener = holder.getListener();

        if (holder.isAsync()) {
            // 异步执行
            asyncExecutor.submit(() -> {
                try {
                    listener.onEvent(event);
                    statistics.recordProcessed(event.getClass());
                } catch (Exception e) {
                    log.error("Async listener {} threw exception for event {}",
                        holder.getListenerName(), event.getEventType(), e);
                    statistics.recordError(event.getClass());
                }
            });
        } else {
            // 同步执行
            try {
                listener.onEvent(event);
                statistics.recordProcessed(event.getClass());
            } catch (Exception e) {
                log.error("Listener {} threw exception for event {}",
                    holder.getListenerName(), event.getEventType(), e);
                statistics.recordError(event.getClass());
            }
        }
    }

    /**
     * 订阅事件
     *
     * @param eventType 事件类型
     * @param listener  监听器
     * @param <T>       事件类型
     */
    public <T extends CoreEvent> void subscribe(Class<T> eventType, CoreEventListener<T> listener) {
        if (shutdown.get()) {
            throw new IllegalStateException("EventBean is shutdown");
        }

        listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                 .add(new ListenerHolder<>(listener));

        log.debug("Subscribed listener {} to event type {}",
            listener.getListenerName(), eventType.getSimpleName());
    }

    /**
     * 取消订阅
     *
     * @param eventType 事件类型
     * @param listener  监听器
     * @param <T>       事件类型
     */
    public <T extends CoreEvent> void unsubscribe(Class<T> eventType, CoreEventListener<T> listener) {
        List<ListenerHolder<?>> eventListeners = listeners.get(eventType);
        if (eventListeners != null) {
            eventListeners.removeIf(holder -> holder.getListener() == listener);
            log.debug("Unsubscribed listener {} from event type {}",
                listener.getListenerName(), eventType.getSimpleName());
        }
    }

    /**
     * 获取事件统计信息
     */
    public EventStatistics getStatistics() {
        return statistics;
    }

    /**
     * 关闭 EventBean
     *
     * <p>关闭后将不再接受新的事件发布和订阅</p>
     */
    public void shutdown() {
        if (shutdown.compareAndSet(false, true)) {
            asyncExecutor.shutdown();
            try {
                if (!asyncExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    asyncExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                asyncExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
            log.info("EventBean shutdown completed");
        }
    }

    /**
     * 监听器包装器
     */
    private static class ListenerHolder<T extends CoreEvent> {
        private final CoreEventListener<T> listener;

        ListenerHolder(CoreEventListener<T> listener) {
            this.listener = listener;
        }

        CoreEventListener<T> getListener() {
            return listener;
        }

        String getListenerName() {
            return listener.getListenerName();
        }

        boolean isAsync() {
            return listener.isAsync();
        }
    }

    /**
     * 事件统计信息
     */
    public static class EventStatistics {
        private final Map<Class<? extends CoreEvent>, AtomicLong> publishedCount;
        private final Map<Class<? extends CoreEvent>, AtomicLong> processedCount;
        private final Map<Class<? extends CoreEvent>, AtomicLong> errorCount;

        EventStatistics() {
            this.publishedCount = new ConcurrentHashMap<>();
            this.processedCount = new ConcurrentHashMap<>();
            this.errorCount = new ConcurrentHashMap<>();
        }

        void recordPublished(Class<? extends CoreEvent> eventType) {
            publishedCount.computeIfAbsent(eventType, k -> new AtomicLong()).incrementAndGet();
        }

        void recordProcessed(Class<? extends CoreEvent> eventType) {
            processedCount.computeIfAbsent(eventType, k -> new AtomicLong()).incrementAndGet();
        }

        void recordError(Class<? extends CoreEvent> eventType) {
            errorCount.computeIfAbsent(eventType, k -> new AtomicLong()).incrementAndGet();
        }

        public long getPublishedCount(Class<? extends CoreEvent> eventType) {
            return publishedCount.getOrDefault(eventType, new AtomicLong(0)).get();
        }

        public long getProcessedCount(Class<? extends CoreEvent> eventType) {
            return processedCount.getOrDefault(eventType, new AtomicLong(0)).get();
        }

        public long getErrorCount(Class<? extends CoreEvent> eventType) {
            return errorCount.getOrDefault(eventType, new AtomicLong(0)).get();
        }
    }
}
