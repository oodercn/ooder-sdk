package net.ooder.scene.protocol;

public interface DiscoveryEventListener {
    
    void onPeerDiscovered(Peer peer);
    
    void onPeerOffline(String peerId);
    
    void onPeerStatusChanged(String peerId, String oldStatus, String newStatus);
}
