package net.ooder.skills.sync.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventListenerRegistry {
    
    private final List<SyncEventListener> listeners;
    
    public EventListenerRegistry() {
        this.listeners = new CopyOnWriteArrayList<>();
    }
    
    public void addListener(SyncEventListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    public void removeListener(SyncEventListener listener) {
        listeners.remove(listener);
    }
    
    public void notifyListeners(SyncEvent event) {
        for (SyncEventListener listener : listeners) {
            try {
                if (listener.supports(event.getType())) {
                    listener.onEvent(event);
                }
            } catch (Exception e) {
                System.err.println("Error notifying listener for event " + event.getType() + ": " + e.getMessage());
            }
        }
    }
    
    public int getListenerCount() {
        return listeners.size();
    }
    
    public void clear() {
        listeners.clear();
    }
}
