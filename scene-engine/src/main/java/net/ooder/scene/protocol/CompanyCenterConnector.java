package net.ooder.scene.protocol;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CompanyCenterConnector {
    private Map<String, Peer> peers;
    private boolean running;

    public CompanyCenterConnector() {
        this.peers = new ConcurrentHashMap<>();
        this.running = false;
    }

    public void start() {
        running = true;
        System.out.println("Company center connector started");
    }

    public void stop() {
        running = false;
        peers.clear();
        System.out.println("Company center connector stopped");
    }

    public void discoverPeers() {
        if (!running) {
            return;
        }

        // 模拟企业中心发现
        System.out.println("Discovering peers in company network");
        // 这里可以添加实际的发现逻辑
    }

    public void addPeer(Peer peer) {
        peers.put(peer.getPeerId(), peer);
    }

    public void removePeer(String peerId) {
        peers.remove(peerId);
    }

    public Map<String, Peer> getPeers() {
        return peers;
    }
}
