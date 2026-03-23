package net.ooder.sdk.api.security.impl;

import net.ooder.sdk.api.security.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkJoinServiceImpl implements NetworkJoinService {
    
    private static final Logger log = LoggerFactory.getLogger(NetworkJoinServiceImpl.class);
    
    private final Map<String, NetworkJoinRequest> requests;
    private final Map<String, Boolean> sceneApprovalSettings;
    private final Map<String, KeyRule> defaultRules;
    private final KeyManagementService keyManagementService;
    
    public NetworkJoinServiceImpl(KeyManagementService keyManagementService) {
        this.requests = new ConcurrentHashMap<String, NetworkJoinRequest>();
        this.sceneApprovalSettings = new ConcurrentHashMap<String, Boolean>();
        this.defaultRules = new ConcurrentHashMap<String, KeyRule>();
        this.keyManagementService = keyManagementService;
    }
    
    @Override
    public NetworkJoinRequest createRequest(NetworkJoinRequest request) {
        if (request.getRequestId() == null || request.getRequestId().isEmpty()) {
            request.setRequestId("req-" + UUID.randomUUID().toString());
        }
        
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(System.currentTimeMillis());
        request.setUpdatedAt(System.currentTimeMillis());
        
        boolean needsApproval = isApprovalRequired(request.getSceneGroupId());
        request.setManualApprovalRequired(needsApproval);
        
        if (!needsApproval) {
            KeyEntity key = autoApproveAndIssueKey(request);
            request.setIssuedKey(key);
            request.setStatus(RequestStatus.APPROVED);
            request.setReviewedAt(System.currentTimeMillis());
            log.info("Auto-approved join request: id={}, applicant={}", 
                request.getRequestId(), request.getApplicantId());
        }
        
        requests.put(request.getRequestId(), request);
        
        log.info("Created join request: id={}, type={}, applicant={}", 
            request.getRequestId(), request.getRequestType(), request.getApplicantId());
        
        return request;
    }
    
    @Override
    public NetworkJoinRequest getRequest(String requestId) {
        return requests.get(requestId);
    }
    
    @Override
    public List<NetworkJoinRequest> getPendingRequests() {
        return getRequestsByStatus(RequestStatus.PENDING);
    }
    
    @Override
    public List<NetworkJoinRequest> getRequestsByStatus(RequestStatus status) {
        List<NetworkJoinRequest> result = new ArrayList<NetworkJoinRequest>();
        for (NetworkJoinRequest request : requests.values()) {
            if (request.getStatus() == status) {
                result.add(request);
            }
        }
        return result;
    }
    
    @Override
    public List<NetworkJoinRequest> getRequestsByApplicant(String applicantId) {
        List<NetworkJoinRequest> result = new ArrayList<NetworkJoinRequest>();
        for (NetworkJoinRequest request : requests.values()) {
            if (applicantId.equals(request.getApplicantId())) {
                result.add(request);
            }
        }
        return result;
    }
    
    @Override
    public NetworkJoinRequest approve(String requestId, String reviewerId, String comment, KeyRule rule) {
        NetworkJoinRequest request = requests.get(requestId);
        if (request == null) {
            log.warn("Request not found: {}", requestId);
            return null;
        }
        
        if (request.getStatus() != RequestStatus.PENDING) {
            log.warn("Request is not pending: id={}, status={}", requestId, request.getStatus());
            return request;
        }
        
        KeyEntity key = issueKeyForRequest(request, rule);
        
        request.setStatus(RequestStatus.APPROVED);
        request.setReviewerId(reviewerId);
        request.setReviewComment(comment);
        request.setReviewedAt(System.currentTimeMillis());
        request.setIssuedKey(key);
        request.setUpdatedAt(System.currentTimeMillis());
        
        log.info("Approved join request: id={}, reviewer={}, keyId={}", 
            requestId, reviewerId, key.getKeyId());
        
        return request;
    }
    
    @Override
    public NetworkJoinRequest reject(String requestId, String reviewerId, String comment) {
        NetworkJoinRequest request = requests.get(requestId);
        if (request == null) {
            log.warn("Request not found: {}", requestId);
            return null;
        }
        
        if (request.getStatus() != RequestStatus.PENDING) {
            log.warn("Request is not pending: id={}, status={}", requestId, request.getStatus());
            return request;
        }
        
        request.setStatus(RequestStatus.REJECTED);
        request.setReviewerId(reviewerId);
        request.setReviewComment(comment);
        request.setReviewedAt(System.currentTimeMillis());
        request.setUpdatedAt(System.currentTimeMillis());
        
        log.info("Rejected join request: id={}, reviewer={}, reason={}", 
            requestId, reviewerId, comment);
        
        return request;
    }
    
    @Override
    public boolean cancelRequest(String requestId) {
        NetworkJoinRequest request = requests.get(requestId);
        if (request == null) {
            return false;
        }
        
        if (request.getStatus() != RequestStatus.PENDING) {
            return false;
        }
        
        request.setStatus(RequestStatus.CANCELLED);
        request.setUpdatedAt(System.currentTimeMillis());
        
        log.info("Cancelled join request: id={}", requestId);
        return true;
    }
    
    @Override
    public int getPendingCount() {
        return getPendingRequests().size();
    }
    
    @Override
    public Map<RequestStatus, Integer> getCountByStatus() {
        Map<RequestStatus, Integer> counts = new EnumMap<RequestStatus, Integer>(RequestStatus.class);
        for (RequestStatus status : RequestStatus.values()) {
            counts.put(status, 0);
        }
        for (NetworkJoinRequest request : requests.values()) {
            counts.put(request.getStatus(), counts.get(request.getStatus()) + 1);
        }
        return counts;
    }
    
    @Override
    public java.util.concurrent.CompletableFuture<NetworkJoinRequest> createRequestAsync(NetworkJoinRequest request) {
        return java.util.concurrent.CompletableFuture.supplyAsync(() -> createRequest(request));
    }
    
    @Override
    public java.util.concurrent.CompletableFuture<NetworkJoinRequest> approveAsync(String requestId, String reviewerId, String comment, KeyRule rule) {
        return java.util.concurrent.CompletableFuture.supplyAsync(() -> approve(requestId, reviewerId, comment, rule));
    }
    
    @Override
    public boolean isApprovalRequired(String sceneGroupId) {
        return sceneApprovalSettings.getOrDefault(sceneGroupId, false);
    }
    
    @Override
    public void setApprovalRequired(String sceneGroupId, boolean required) {
        sceneApprovalSettings.put(sceneGroupId, required);
        log.info("Set approval required for scene: scene={}, required={}", sceneGroupId, required);
    }
    
    @Override
    public KeyEntity getIssuedKey(String requestId) {
        NetworkJoinRequest request = requests.get(requestId);
        return request != null ? request.getIssuedKey() : null;
    }
    
    private KeyEntity autoApproveAndIssueKey(NetworkJoinRequest request) {
        KeyRule rule = getRecommendedRule(request.getRequestType(), request.getSceneGroupId());
        return issueKeyForRequest(request, rule);
    }
    
    private KeyEntity issueKeyForRequest(NetworkJoinRequest request, KeyRule rule) {
        KeyManagementService.KeyGenerateRequest keyRequest = new KeyManagementService.KeyGenerateRequest();
        keyRequest.setOwnerId(request.getApplicantId());
        keyRequest.setOwnerType(request.getApplicantType());
        keyRequest.setKeyType(determineKeyType(request.getRequestType()));
        keyRequest.setKeyName(generateKeyName(request));
        keyRequest.setSceneGroupId(request.getSceneGroupId());
        
        if (rule != null) {
            keyRequest.setExpiresInSeconds(rule.getDefaultExpiresInSeconds());
            keyRequest.setMaxUseCount(rule.getDefaultMaxUseCount());
            keyRequest.setAllowedScenes(rule.getAllowedScenes());
            keyRequest.setAllowedOperations(rule.getAllowedOperations());
            keyRequest.setApprovalRequired(rule.isApprovalRequired());
        } else {
            keyRequest.setExpiresInSeconds(86400);
            keyRequest.setMaxUseCount(1000);
        }
        
        return keyManagementService.generateKey(keyRequest);
    }
    
    private KeyType determineKeyType(RequestType requestType) {
        switch (requestType) {
            case USER_JOIN:
                return KeyType.SESSION_TOKEN;
            case DEVICE_JOIN:
                return KeyType.DEVICE_KEY;
            case AGENT_JOIN:
                return KeyType.AGENT_KEY;
            default:
                return KeyType.NETWORK_JOIN_KEY;
        }
    }
    
    private String generateKeyName(NetworkJoinRequest request) {
        return request.getRequestType().name() + "-" + request.getApplicantName();
    }
    
    public void setDefaultRule(String sceneGroupId, KeyRule rule) {
        defaultRules.put(sceneGroupId, rule);
        log.info("Set default rule for scene: scene={}", sceneGroupId);
    }
    
    public KeyRule getDefaultRule(String sceneGroupId) {
        return defaultRules.get(sceneGroupId);
    }
    
    private KeyRule getRecommendedRule(RequestType requestType, String sceneGroupId) {
        KeyRule defaultRule = defaultRules.get(sceneGroupId);
        if (defaultRule != null) {
            return defaultRule;
        }
        
        KeyRule rule = new KeyRule();
        rule.setRuleId("auto-rule-" + sceneGroupId);
        rule.setRuleName("Auto Generated Rule");
        rule.setSceneGroupId(sceneGroupId);
        rule.setDefaultExpiresInSeconds(86400);
        rule.setDefaultMaxUseCount(1000);
        rule.setApprovalRequired(false);
        
        return rule;
    }
}
