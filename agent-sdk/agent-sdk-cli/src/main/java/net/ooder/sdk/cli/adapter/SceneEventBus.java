package net.ooder.sdk.cli.adapter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class SceneEventBus {

    private final Map<String, List<Consumer<SceneEvent>>> subscribers = new ConcurrentHashMap<>();
    private final List<Consumer<SceneEvent>> globalSubscribers = new CopyOnWriteArrayList<>();

    public void subscribe(String eventType, Consumer<SceneEvent> handler) {
        subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(handler);
    }

    public void subscribeGlobal(Consumer<SceneEvent> handler) {
        globalSubscribers.add(handler);
    }

    public void unsubscribe(String eventType, Consumer<SceneEvent> handler) {
        List<Consumer<SceneEvent>> handlers = subscribers.get(eventType);
        if (handlers != null) {
            handlers.remove(handler);
        }
    }

    public CompletableFuture<Void> publish(String groupId, String eventType, Map<String, Object> data) {
        SceneEvent event = new SceneEvent(groupId, eventType, data, System.currentTimeMillis());

        List<Consumer<SceneEvent>> handlers = subscribers.get(eventType);
        if (handlers != null) {
            for (Consumer<SceneEvent> handler : handlers) {
                try {
                    handler.accept(event);
                } catch (Exception e) {
                    System.err.println("Event handler error for " + eventType + ": " + e.getMessage());
                }
            }
        }

        for (Consumer<SceneEvent> handler : globalSubscribers) {
            try {
                handler.accept(event);
            } catch (Exception e) {
                System.err.println("Global event handler error: " + e.getMessage());
            }
        }

        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<Void> publish(String groupId, String eventType) {
        return publish(groupId, eventType, Map.of());
    }

    public static class SceneEvent {
        private final String groupId;
        private final String eventType;
        private final Map<String, Object> data;
        private final long timestamp;

        public SceneEvent(String groupId, String eventType, Map<String, Object> data, long timestamp) {
            this.groupId = groupId;
            this.eventType = eventType;
            this.data = data;
            this.timestamp = timestamp;
        }

        public String getGroupId() { return groupId; }
        public String getEventType() { return eventType; }
        public Map<String, Object> getData() { return data; }
        public long getTimestamp() { return timestamp; }

        @Override
        public String toString() {
            return String.format("SceneEvent{groupId='%s', eventType='%s', timestamp=%d}", groupId, eventType, timestamp);
        }
    }
}
