package net.ooder.scene.failover;

public interface FailoverListener {

    void onFailoverEvent(FailoverEvent event);

    default boolean supports(FailoverEventType type) {
        return true;
    }
}
