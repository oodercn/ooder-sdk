package net.ooder.scene.protocol.impl;

import net.ooder.scene.protocol.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 发现协议适配器实现
 *
 * <p>提供节点发现、状态监控等功能</p>
 */
public class DiscoveryProtocolAdapterImpl implements DiscoveryProtocolAdapter {

    private final Map<String, Peer> peerRegistry = new ConcurrentHashMap<>();
    private final List<DiscoveryEventListener> listeners = new CopyOnWriteArrayList<>();
    
    private String localPeerId;
    private String localPeerName;
    private String localPeerType;
    private int discoveryTimeout = 30000;
    private int maxPeers = 100;

    public DiscoveryProtocolAdapterImpl() {
        this.localPeerId = UUID.randomUUID().toString();
        this.localPeerName = "local-node";
        this.localPeerType = "SEC_ENGINE";
    }

    public void setLocalPeerId(String localPeerId) {
        this.localPeerId = localPeerId;
    }

    public void setLocalPeerName(String localPeerName) {
        this.localPeerName = localPeerName;
    }

    public void setLocalPeerType(String localPeerType) {
        this.localPeerType = localPeerType;
    }

    public void setDiscoveryTimeout(int discoveryTimeout) {
        this.discoveryTimeout = discoveryTimeout;
    }

    public void setMaxPeers(int maxPeers) {
        this.maxPeers = maxPeers;
    }

    @Override
    public CompletableFuture<DiscoveryResult> discoverPeers(DiscoveryRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            DiscoveryResult result = new DiscoveryResult();
            long startTime = System.currentTimeMillis();
            
            try {
                List<Peer> discoveredPeers = new ArrayList<>();
                String discoveryType = request.getDiscoveryType();
                int maxResults = request.getMaxPeers() > 0 ? request.getMaxPeers() : maxPeers;
                
                if ("BROADCAST".equals(discoveryType)) {
                    discoveredPeers = discoverByBroadcast(maxResults);
                } else if ("MULTICAST".equals(discoveryType)) {
                    discoveredPeers = discoverByMulticast(maxResults);
                } else if ("UNICAST".equals(discoveryType)) {
                    discoveredPeers = discoverByUnicast(maxResults);
                } else {
                    discoveredPeers = discoverAllPeers(maxResults);
                }
                
                List<String> peerTypes = request.getPeerTypes();
                if (peerTypes != null && !peerTypes.isEmpty()) {
                    discoveredPeers = filterByPeerTypes(discoveredPeers, peerTypes);
                }
                
                String filter = request.getFilter();
                if (filter != null && !filter.isEmpty()) {
                    discoveredPeers = filterPeers(discoveredPeers, filter);
                }
                
                for (Peer peer : discoveredPeers) {
                    if (!peerRegistry.containsKey(peer.getPeerId())) {
                        peerRegistry.put(peer.getPeerId(), peer);
                        notifyPeerDiscovered(peer);
                    }
                }
                
                result.setSuccess(true);
                result.setMessage("Discovery completed");
                result.setPeers(discoveredPeers);
                result.setTotalFound(discoveredPeers.size());
                result.setDuration(System.currentTimeMillis() - startTime);
                
            } catch (Exception e) {
                result.setSuccess(false);
                result.setMessage("Discovery failed: " + e.getMessage());
                result.setDuration(System.currentTimeMillis() - startTime);
            }
            
            return result;
        });
    }

    @Override
    public CompletableFuture<List<Peer>> listDiscoveredPeers() {
        return CompletableFuture.supplyAsync(() -> {
            return new ArrayList<>(peerRegistry.values());
        });
    }

    @Override
    public CompletableFuture<Peer> discoverMcp() {
        return CompletableFuture.supplyAsync(() -> {
            for (Peer peer : peerRegistry.values()) {
                if ("MCP".equals(peer.getPeerType())) {
                    return peer;
                }
            }
            
            Peer mcpPeer = new Peer();
            mcpPeer.setPeerId("mcp-" + UUID.randomUUID().toString());
            mcpPeer.setPeerName("MCP Server");
            mcpPeer.setPeerType("MCP");
            mcpPeer.setAddress("localhost");
            mcpPeer.setPort(8080);
            mcpPeer.setStatus("ONLINE");
            mcpPeer.setLastSeen(System.currentTimeMillis());
            mcpPeer.setRegisteredAt(System.currentTimeMillis());
            mcpPeer.setCapabilities(Arrays.asList("protocol", "command", "event"));
            mcpPeer.setVersion("0.7.3");
            
            peerRegistry.put(mcpPeer.getPeerId(), mcpPeer);
            
            return mcpPeer;
        });
    }

    @Override
    public CompletableFuture<Peer> getPeer(String peerId) {
        return CompletableFuture.supplyAsync(() -> {
            if (peerId == null || peerId.isEmpty()) {
                return null;
            }
            return peerRegistry.get(peerId);
        });
    }

    @Override
    public CompletableFuture<Boolean> isPeerOnline(String peerId) {
        return CompletableFuture.supplyAsync(() -> {
            if (peerId == null || peerId.isEmpty()) {
                return false;
            }
            Peer peer = peerRegistry.get(peerId);
            return peer != null && peer.isOnline();
        });
    }

    @Override
    public void addDiscoveryListener(DiscoveryEventListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeDiscoveryListener(DiscoveryEventListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    public void registerPeer(Peer peer) {
        if (peer == null || peer.getPeerId() == null) {
            return;
        }
        
        Peer existingPeer = peerRegistry.get(peer.getPeerId());
        if (existingPeer != null) {
            String oldStatus = existingPeer.getStatus();
            String newStatus = peer.getStatus();
            existingPeer.setLastSeen(System.currentTimeMillis());
            if (!Objects.equals(oldStatus, newStatus)) {
                existingPeer.setStatus(newStatus);
                notifyPeerStatusChanged(peer.getPeerId(), oldStatus, newStatus);
            }
        } else {
            peer.setRegisteredAt(System.currentTimeMillis());
            peer.setLastSeen(System.currentTimeMillis());
            peerRegistry.put(peer.getPeerId(), peer);
            notifyPeerDiscovered(peer);
        }
    }

    public void unregisterPeer(String peerId) {
        if (peerId == null || peerId.isEmpty()) {
            return;
        }
        
        Peer peer = peerRegistry.remove(peerId);
        if (peer != null) {
            notifyPeerOffline(peerId);
        }
    }

    public void updatePeerStatus(String peerId, String status) {
        if (peerId == null || peerId.isEmpty()) {
            return;
        }
        
        Peer peer = peerRegistry.get(peerId);
        if (peer != null) {
            String oldStatus = peer.getStatus();
            peer.setStatus(status);
            peer.setLastSeen(System.currentTimeMillis());
            notifyPeerStatusChanged(peerId, oldStatus, status);
        }
    }

    private List<Peer> discoverByBroadcast(int maxResults) {
        List<Peer> peers = new ArrayList<>();
        int count = 0;
        
        for (Peer peer : peerRegistry.values()) {
            if (count >= maxResults) break;
            peers.add(peer);
            count++;
        }
        
        return peers;
    }

    private List<Peer> discoverByMulticast(int maxResults) {
        return discoverByBroadcast(maxResults);
    }

    private List<Peer> discoverByUnicast(int maxResults) {
        return discoverByBroadcast(maxResults);
    }

    private List<Peer> discoverAllPeers(int maxResults) {
        List<Peer> peers = new ArrayList<>();
        int count = 0;
        
        for (Peer peer : peerRegistry.values()) {
            if (count >= maxResults) break;
            peers.add(peer);
            count++;
        }
        
        return peers;
    }

    private List<Peer> filterByPeerTypes(List<Peer> peers, List<String> peerTypes) {
        List<Peer> filtered = new ArrayList<>();
        for (Peer peer : peers) {
            if (peerTypes.contains(peer.getPeerType())) {
                filtered.add(peer);
            }
        }
        return filtered;
    }

    private List<Peer> filterPeers(List<Peer> peers, String filter) {
        List<Peer> filtered = new ArrayList<>();
        String lowerFilter = filter.toLowerCase();
        
        for (Peer peer : peers) {
            if (peer.getPeerName() != null && peer.getPeerName().toLowerCase().contains(lowerFilter)) {
                filtered.add(peer);
            } else if (peer.getPeerType() != null && peer.getPeerType().toLowerCase().contains(lowerFilter)) {
                filtered.add(peer);
            }
        }
        
        return filtered;
    }

    private void notifyPeerDiscovered(Peer peer) {
        for (DiscoveryEventListener listener : listeners) {
            try {
                listener.onPeerDiscovered(peer);
            } catch (Exception e) {
            }
        }
    }

    private void notifyPeerOffline(String peerId) {
        for (DiscoveryEventListener listener : listeners) {
            try {
                listener.onPeerOffline(peerId);
            } catch (Exception e) {
            }
        }
    }

    private void notifyPeerStatusChanged(String peerId, String oldStatus, String newStatus) {
        for (DiscoveryEventListener listener : listeners) {
            try {
                listener.onPeerStatusChanged(peerId, oldStatus, newStatus);
            } catch (Exception e) {
            }
        }
    }
}
