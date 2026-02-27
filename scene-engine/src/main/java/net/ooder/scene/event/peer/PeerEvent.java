package net.ooder.scene.event.peer;

import net.ooder.scene.event.SceneEvent;
import net.ooder.scene.event.SceneEventType;
import net.ooder.scene.protocol.Peer;

public class PeerEvent extends SceneEvent {
    
    private final String peerId;
    private final Peer peer;
    private final String oldStatus;
    private final String newStatus;
    private final int discoveredCount;
    
    private PeerEvent(Object source, SceneEventType eventType, String peerId) {
        super(source, eventType);
        this.peerId = peerId;
        this.peer = null;
        this.oldStatus = null;
        this.newStatus = null;
        this.discoveredCount = 0;
    }
    
    private PeerEvent(Object source, SceneEventType eventType, String peerId, Peer peer) {
        super(source, eventType);
        this.peerId = peerId;
        this.peer = peer;
        this.oldStatus = null;
        this.newStatus = null;
        this.discoveredCount = 0;
    }
    
    private PeerEvent(Object source, SceneEventType eventType, String peerId, 
                      String oldStatus, String newStatus) {
        super(source, eventType);
        this.peerId = peerId;
        this.peer = null;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.discoveredCount = 0;
    }
    
    private PeerEvent(Object source, SceneEventType eventType, int discoveredCount) {
        super(source, eventType);
        this.peerId = null;
        this.peer = null;
        this.oldStatus = null;
        this.newStatus = null;
        this.discoveredCount = discoveredCount;
    }
    
    public static PeerEvent discovered(Object source, Peer peer) {
        return new PeerEvent(source, SceneEventType.PEER_DISCOVERED, peer.getPeerId(), peer);
    }
    
    public static PeerEvent offline(Object source, String peerId) {
        return new PeerEvent(source, SceneEventType.PEER_OFFLINE, peerId);
    }
    
    public static PeerEvent statusChanged(Object source, String peerId, String oldStatus, String newStatus) {
        return new PeerEvent(source, SceneEventType.PEER_STATUS_CHANGED, peerId, oldStatus, newStatus);
    }
    
    public static PeerEvent discoveryCompleted(Object source, int discoveredCount) {
        return new PeerEvent(source, SceneEventType.PEER_DISCOVERY_COMPLETED, discoveredCount);
    }
    
    public static PeerEvent mcpDiscovered(Object source, Peer peer) {
        return new PeerEvent(source, SceneEventType.MCP_DISCOVERED, peer.getPeerId(), peer);
    }
    
    public String getPeerId() {
        return peerId;
    }
    
    public Peer getPeer() {
        return peer;
    }
    
    public String getOldStatus() {
        return oldStatus;
    }
    
    public String getNewStatus() {
        return newStatus;
    }
    
    public int getDiscoveredCount() {
        return discoveredCount;
    }
    
    @Override
    public String toString() {
        return "PeerEvent{" +
                "eventType=" + getEventType() +
                ", peerId='" + peerId + '\'' +
                ", oldStatus='" + oldStatus + '\'' +
                ", newStatus='" + newStatus + '\'' +
                ", discoveredCount=" + discoveredCount +
                ", traceId='" + getTraceId() + '\'' +
                '}';
    }
}
