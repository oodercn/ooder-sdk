package net.ooder.scene.protocol;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DepartmentShareManager {
    private Map<String, Peer> peers;
    private boolean running;

    public DepartmentShareManager() {
        this.peers = new ConcurrentHashMap<>();
        this.running = false;
    }

    public void start() {
        running = true;
        System.out.println("Department share manager started");
    }

    public void stop() {
        running = false;
        peers.clear();
        System.out.println("Department share manager stopped");
    }

    public void discoverPeers() {
        if (!running) {
            return;
        }

        // 模拟部门共享发现
        System.out.println("Discovering peers in department network");
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
