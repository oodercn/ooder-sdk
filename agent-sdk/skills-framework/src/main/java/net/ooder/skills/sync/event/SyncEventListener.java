package net.ooder.skills.sync.event;

public interface SyncEventListener {
    
    void onEvent(SyncEvent event);
    
    default boolean supports(SyncEvent.Type type) {
        return true;
    }
}
