package net.ooder.scene.protocol;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DiscoveryCoordinator {
    private UdpDiscoveryService udpService;
    private MdnsDiscoveryService mdnsService;
    private PersonalNetworkManager personalManager;
    private DepartmentShareManager departmentManager;
    private CompanyCenterConnector companyConnector;
    private Map<String, Peer> discoveredPeers;

    public DiscoveryCoordinator() {
        this.udpService = new UdpDiscoveryService();
        this.mdnsService = new MdnsDiscoveryService();
        this.personalManager = new PersonalNetworkManager();
        this.departmentManager = new DepartmentShareManager();
        this.companyConnector = new CompanyCenterConnector();
        this.discoveredPeers = new ConcurrentHashMap<>();
    }

    public void start() {
        try {
            // 启动 UDP 发现服务
            udpService.start();
            // 启动 mDNS 服务
            mdnsService.start();
            // 启动各层管理器
            personalManager.start();
            departmentManager.start();
            companyConnector.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        // 停止各服务
        udpService.stop();
        mdnsService.stop();
        personalManager.stop();
        departmentManager.stop();
        companyConnector.stop();
    }

    public void discoverPeers() {
        // 个人网络优先发现
        personalManager.discoverPeers();
        
        // 部门共享发现
        departmentManager.discoverPeers();
        
        // 企业管理发现
        companyConnector.discoverPeers();
    }

    public Map<String, Peer> getDiscoveredPeers() {
        return discoveredPeers;
    }

    public void addPeer(Peer peer) {
        discoveredPeers.put(peer.getPeerId(), peer);
    }

    public void removePeer(String peerId) {
        discoveredPeers.remove(peerId);
    }
}
