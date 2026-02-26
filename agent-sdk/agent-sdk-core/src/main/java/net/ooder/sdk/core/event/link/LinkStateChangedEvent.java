package net.ooder.sdk.core.event.link;

import net.ooder.sdk.core.event.CoreEvent;

/**
 * Link 连接状态变更事件（Core 层）
 *
 * <p>当网络连接状态发生变更时触发</p>
 *
 * @author Ooder Team
 * @version 2.3
 */
public final class LinkStateChangedEvent extends CoreEvent {

    private static final long serialVersionUID = 1L;

    /**
     * Link ID
     */
    private final String linkId;

    /**
     * 对端节点 ID
     */
    private final String peerId;

    /**
     * 旧状态
     */
    private final LinkState oldState;

    /**
     * 新状态
     */
    private final LinkState newState;

    /**
     * 延迟（毫秒）
     */
    private final long latencyMs;

    public LinkStateChangedEvent(String linkId, String peerId, LinkState oldState, LinkState newState, long latencyMs) {
        super("LinkManager");
        this.linkId = linkId;
        this.peerId = peerId;
        this.oldState = oldState;
        this.newState = newState;
        this.latencyMs = latencyMs;
    }

    public String getLinkId() {
        return linkId;
    }

    public String getPeerId() {
        return peerId;
    }

    public LinkState getOldState() {
        return oldState;
    }

    public LinkState getNewState() {
        return newState;
    }

    public long getLatencyMs() {
        return latencyMs;
    }

    @Override
    public String getDescription() {
        return String.format("Link %s to %s state changed from %s to %s (latency: %dms)",
            linkId, peerId, oldState, newState, latencyMs);
    }

    @Override
    public EventPriority getPriority() {
        // 连接断开为高优先级
        return newState == LinkState.DISCONNECTED ? EventPriority.CRITICAL : EventPriority.NORMAL;
    }

    @Override
    public String toString() {
        return String.format("LinkStateChangedEvent[link=%s, peer=%s, %s -> %s, latency=%dms, time=%s]",
            linkId, peerId, oldState, newState, latencyMs, getInstant());
    }

    /**
     * Link 状态枚举
     */
    public enum LinkState {
        CONNECTING,
        CONNECTED,
        DISCONNECTED,
        RECONNECTING,
        FAILED
    }
}
