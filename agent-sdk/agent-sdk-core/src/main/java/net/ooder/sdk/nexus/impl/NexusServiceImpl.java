package net.ooder.sdk.nexus.impl;

import net.ooder.sdk.api.security.*;
import net.ooder.sdk.nexus.*;
import net.ooder.sdk.nexus.model.*;
import net.ooder.sdk.nexus.spi.ProtocolProvider;
import net.ooder.sdk.southbound.protocol.*;
import net.ooder.sdk.southbound.protocol.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public class NexusServiceImpl implements NexusService {
    
    private static final Logger log = LoggerFactory.getLogger(NexusServiceImpl.class);
    
    private final DiscoveryProtocol discoveryProtocol;
    private final RoleProtocol roleProtocol;
    private final LoginProtocol loginProtocol;
    private final CollaborationProtocol collaborationProtocol;
    
    private final KeyManagementService keyManagementService;
    private final NetworkJoinService networkJoinService;
    
    private NexusConfig config;
    private NexusStatus status;
    private UserSession currentSession;
    private NexusState state;
    private final List<NexusListener> listeners;
    private final ExecutorService executor;
    
    @Deprecated
    public NexusServiceImpl() {
        this(new net.ooder.sdk.nexus.spi.DefaultProtocolProvider());
    }
    
    public NexusServiceImpl(ProtocolProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("ProtocolProvider cannot be null");
        }
        
        this.discoveryProtocol = provider.getDiscoveryProtocol();
        this.roleProtocol = provider.getRoleProtocol();
        this.loginProtocol = provider.getLoginProtocol();
        this.collaborationProtocol = provider.getCollaborationProtocol();
        
        this.keyManagementService = new net.ooder.sdk.api.security.impl.KeyManagementServiceImpl();
        this.networkJoinService = new net.ooder.sdk.api.security.impl.NetworkJoinServiceImpl(keyManagementService);
        
        this.listeners = new CopyOnWriteArrayList<NexusListener>();
        this.executor = Executors.newCachedThreadPool();
        this.state = NexusState.STOPPED;
        
        log.info("NexusServiceImpl initialized with provider: {}", provider.getProviderName());
    }
    
    public NexusServiceImpl(ProtocolProvider provider, KeyManagementService keyManagementService, NetworkJoinService networkJoinService) {
        if (provider == null) {
            throw new IllegalArgumentException("ProtocolProvider cannot be null");
        }
        
        this.discoveryProtocol = provider.getDiscoveryProtocol();
        this.roleProtocol = provider.getRoleProtocol();
        this.loginProtocol = provider.getLoginProtocol();
        this.collaborationProtocol = provider.getCollaborationProtocol();
        
        this.keyManagementService = keyManagementService != null ? keyManagementService : new net.ooder.sdk.api.security.impl.KeyManagementServiceImpl();
        this.networkJoinService = networkJoinService != null ? networkJoinService : new net.ooder.sdk.api.security.impl.NetworkJoinServiceImpl(this.keyManagementService);
        
        this.listeners = new CopyOnWriteArrayList<NexusListener>();
        this.executor = Executors.newCachedThreadPool();
        this.state = NexusState.STOPPED;
        
        log.info("NexusServiceImpl initialized with provider: {} and custom security services", provider.getProviderName());
    }
    
    @Override
    public CompletableFuture<NexusStatus> start(NexusConfig config) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Starting Nexus: nodeId={}", config.getNodeId());
            
            this.config = config;
            this.state = NexusState.STARTING;
            
            this.status = new NexusStatus();
            this.status.setNodeId(config.getNodeId());
            this.status.setNodeName(config.getNodeName());
            this.status.setState(NexusState.RUNNING);
            this.status.setOnline(true);
            this.status.setStartTime(System.currentTimeMillis());
            
            this.state = NexusState.RUNNING;
            notifyStateChanged(NexusState.STOPPED, NexusState.RUNNING);
            
            log.info("Nexus started: nodeId={}", config.getNodeId());
            return status;
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> stop() {
        return CompletableFuture.runAsync(() -> {
            log.info("Stopping Nexus");
            
            NexusState oldState = this.state;
            this.state = NexusState.STOPPING;
            
            if (status != null) {
                status.setState(NexusState.STOPPED);
                status.setOnline(false);
            }
            
            this.state = NexusState.STOPPED;
            notifyStateChanged(oldState, NexusState.STOPPED);
            
            log.info("Nexus stopped");
        }, executor);
    }
    
    @Override
    public CompletableFuture<NexusStatus> getStatus() {
        return CompletableFuture.supplyAsync(() -> status, executor);
    }
    
    @Override
    public CompletableFuture<Void> login(LoginRequest request) {
        return CompletableFuture.runAsync(() -> {
            log.info("Logging in: username={}", request.getUsername());
            
            LoginResult result = loginProtocol.login(request).join();
            if (result.isSuccess()) {
                currentSession = new UserSession();
                currentSession.setSessionId(result.getSessionId());
                currentSession.setUserName(request.getUsername());
                currentSession.setCreatedAt(System.currentTimeMillis());
                
                notifyLoginSuccess(currentSession);
                log.info("Login successful: username={}", request.getUsername());
            } else {
                log.warn("Login failed: username={}", request.getUsername());
                throw new RuntimeException("Login failed: " + result.getErrorMessage());
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> logout() {
        return CompletableFuture.runAsync(() -> {
            log.info("Logging out");
            currentSession = null;
            notifyLogout();
            log.info("Logout successful");
        }, executor);
    }
    
    @Override
    public CompletableFuture<UserSession> getCurrentSession() {
        return CompletableFuture.supplyAsync(() -> currentSession, executor);
    }
    
    @Override
    public CompletableFuture<List<PeerInfo>> discoverPeers() {
        return CompletableFuture.supplyAsync(() -> {
            DiscoveryResult result = discoveryProtocol.discover(new DiscoveryRequest()).join();
            return result.getPeers();
        }, executor);
    }
    
    @Override
    public CompletableFuture<RoleDecision> getCurrentRole() {
        return CompletableFuture.supplyAsync(() -> {
            RoleContext context = new RoleContext();
            context.setAgentId(config != null ? config.getNodeId() : "unknown");
            return roleProtocol.decideRole(context).join();
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> joinSceneGroup(String groupId) {
        return CompletableFuture.runAsync(() -> {
            log.info("Joining scene group: {}", groupId);
            JoinRequest request = new JoinRequest();
            request.setAgentId(config != null ? config.getNodeId() : "unknown");
            collaborationProtocol.joinSceneGroup(groupId, request).join();
            log.info("Joined scene group: {}", groupId);
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> joinSceneGroupWithKey(String groupId, String keyValue) {
        return CompletableFuture.runAsync(() -> {
            log.info("Joining scene group with key: groupId={}", groupId);
            
            KeyManagementService.KeyValidationResult validation = keyManagementService.validateKeyByValue(keyValue, groupId);
            if (!validation.isValid()) {
                throw new SecurityException("Invalid key for scene group: " + validation.getErrorMessage());
            }
            
            KeyEntity key = validation.getKeyEntity();
            if (!key.canAccessScene(groupId)) {
                throw new SecurityException("Key does not have access to scene group: " + groupId);
            }
            
            JoinRequest request = new JoinRequest();
            request.setAgentId(config != null ? config.getNodeId() : "unknown");
            request.setInviteCode(keyValue);
            
            collaborationProtocol.joinSceneGroup(groupId, request).join();
            
            key.incrementUsage();
            
            log.info("Joined scene group with key: groupId={}, keyId={}", groupId, key.getKeyId());
        }, executor);
    }
    
    @Override
    public CompletableFuture<NetworkJoinRequest> requestJoinSceneGroup(NetworkJoinRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Requesting to join scene group: groupId={}, applicant={}", 
                request.getSceneGroupId(), request.getApplicantId());
            
            request.setSceneGroupId(request.getSceneGroupId());
            
            NetworkJoinRequest createdRequest = networkJoinService.createRequest(request);
            
            if (createdRequest.isApproved()) {
                log.info("Join request auto-approved: requestId={}", createdRequest.getRequestId());
            } else {
                log.info("Join request pending approval: requestId={}", createdRequest.getRequestId());
            }
            
            return createdRequest;
        }, executor);
    }
    
    @Override
    public CompletableFuture<NetworkJoinRequest> getJoinRequestStatus(String requestId) {
        return CompletableFuture.supplyAsync(() -> {
            NetworkJoinRequest request = networkJoinService.getRequest(requestId);
            if (request == null) {
                throw new IllegalArgumentException("Request not found: " + requestId);
            }
            return request;
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> leaveSceneGroup(String groupId) {
        return CompletableFuture.runAsync(() -> {
            log.info("Leaving scene group: {}", groupId);
            collaborationProtocol.leaveSceneGroup(groupId).join();
            notifySceneGroupLeft(groupId);
            log.info("Left scene group: {}", groupId);
        }, executor);
    }
    
    @Override
    public CompletableFuture<List<SceneGroupInfo>> listSceneGroups() {
        return CompletableFuture.supplyAsync(() -> {
            List<SceneGroupInfo> groups = new ArrayList<SceneGroupInfo>();
            
            try {
                if (collaborationProtocol != null) {
                    List<SceneGroupInfo> protocolGroups = collaborationProtocol.listJoinedGroups().get(5, TimeUnit.SECONDS);
                    if (protocolGroups != null) {
                        groups.addAll(protocolGroups);
                    }
                }
            } catch (Exception e) {
                log.debug("Failed to get scene groups from collaboration protocol: {}", e.getMessage());
            }
            
            log.debug("Listed {} scene groups", groups.size());
            return groups;
        }, executor);
    }
    
    @Override
    public CompletableFuture<KeyEntity> getSceneGroupAccessKey(String groupId) {
        return CompletableFuture.supplyAsync(() -> {
            if (currentSession == null) {
                throw new IllegalStateException("Not logged in");
            }
            
            List<KeyEntity> keys = keyManagementService.getKeysByScene(groupId);
            if (keys.isEmpty()) {
                return null;
            }
            
            for (KeyEntity key : keys) {
                if (key.isValid() && key.getOwnerId().equals(currentSession.getUserName())) {
                    return key;
                }
            }
            
            return keys.get(0);
        }, executor);
    }
    
    @Override
    public CompletableFuture<Boolean> validateSceneGroupAccess(String groupId, String keyValue) {
        return CompletableFuture.supplyAsync(() -> {
            KeyManagementService.KeyValidationResult result = keyManagementService.validateKeyByValue(keyValue, groupId);
            return result.isValid();
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> setSceneGroupApprovalRequired(String groupId, boolean required) {
        return CompletableFuture.runAsync(() -> {
            networkJoinService.setApprovalRequired(groupId, required);
            log.info("Set approval required for scene group: groupId={}, required={}", groupId, required);
        }, executor);
    }
    
    @Override
    public void addNexusListener(NexusListener listener) {
        listeners.add(listener);
    }
    
    @Override
    public void removeNexusListener(NexusListener listener) {
        listeners.remove(listener);
    }
    
    @Override
    public void shutdown() {
        log.info("Shutting down NexusService");
        stop().join();
        executor.shutdown();
        listeners.clear();
        log.info("NexusService shutdown complete");
    }
    
    private void notifyStateChanged(NexusState oldState, NexusState newState) {
        for (NexusListener listener : listeners) {
            try {
                listener.onStateChanged(oldState, newState);
            } catch (Exception e) {
                log.warn("Listener notification failed", e);
            }
        }
    }
    
    private void notifyLoginSuccess(UserSession session) {
        for (NexusListener listener : listeners) {
            try {
                listener.onLoginSuccess(session);
            } catch (Exception e) {
                log.warn("Listener notification failed", e);
            }
        }
    }
    
    private void notifyLogout() {
        for (NexusListener listener : listeners) {
            try {
                listener.onLogout();
            } catch (Exception e) {
                log.warn("Listener notification failed", e);
            }
        }
    }
    
    private void notifySceneGroupLeft(String groupId) {
        for (NexusListener listener : listeners) {
            try {
                listener.onSceneGroupLeft(groupId);
            } catch (Exception e) {
                log.warn("Listener notification failed", e);
            }
        }
    }
}
