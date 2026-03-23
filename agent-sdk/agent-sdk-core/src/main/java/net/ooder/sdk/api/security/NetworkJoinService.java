package net.ooder.sdk.api.security;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface NetworkJoinService {
    
    NetworkJoinRequest createRequest(NetworkJoinRequest request);
    
    NetworkJoinRequest getRequest(String requestId);
    
    List<NetworkJoinRequest> getPendingRequests();
    
    List<NetworkJoinRequest> getRequestsByStatus(RequestStatus status);
    
    List<NetworkJoinRequest> getRequestsByApplicant(String applicantId);
    
    NetworkJoinRequest approve(String requestId, String reviewerId, String comment, KeyRule rule);
    
    NetworkJoinRequest reject(String requestId, String reviewerId, String comment);
    
    boolean cancelRequest(String requestId);
    
    int getPendingCount();
    
    Map<RequestStatus, Integer> getCountByStatus();
    
    CompletableFuture<NetworkJoinRequest> createRequestAsync(NetworkJoinRequest request);
    
    CompletableFuture<NetworkJoinRequest> approveAsync(String requestId, String reviewerId, String comment, KeyRule rule);
    
    boolean isApprovalRequired(String sceneGroupId);
    
    void setApprovalRequired(String sceneGroupId, boolean required);
    
    KeyEntity getIssuedKey(String requestId);
}
