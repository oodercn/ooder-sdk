package net.ooder.sdk.api.security;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class NetworkJoinRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String requestId;
    private RequestType requestType;
    private RequestStatus status;
    
    private String applicantId;
    private String applicantName;
    private OwnerType applicantType;
    
    private String sceneGroupId;
    private String inviteCode;
    private Map<String, Object> capabilities;
    
    private String reviewerId;
    private String reviewComment;
    private long reviewedAt;
    
    private KeyEntity issuedKey;
    
    private long createdAt;
    private long updatedAt;
    
    private boolean manualApprovalRequired;
    
    public NetworkJoinRequest() {
        this.status = RequestStatus.PENDING;
        this.capabilities = new HashMap<String, Object>();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.manualApprovalRequired = false;
    }
    
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    
    public RequestType getRequestType() { return requestType; }
    public void setRequestType(RequestType requestType) { this.requestType = requestType; }
    
    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }
    
    public String getApplicantId() { return applicantId; }
    public void setApplicantId(String applicantId) { this.applicantId = applicantId; }
    
    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }
    
    public OwnerType getApplicantType() { return applicantType; }
    public void setApplicantType(OwnerType applicantType) { this.applicantType = applicantType; }
    
    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
    
    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }
    
    public Map<String, Object> getCapabilities() { return capabilities; }
    public void setCapabilities(Map<String, Object> capabilities) { this.capabilities = capabilities; }
    
    public String getReviewerId() { return reviewerId; }
    public void setReviewerId(String reviewerId) { this.reviewerId = reviewerId; }
    
    public String getReviewComment() { return reviewComment; }
    public void setReviewComment(String reviewComment) { this.reviewComment = reviewComment; }
    
    public long getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(long reviewedAt) { this.reviewedAt = reviewedAt; }
    
    public KeyEntity getIssuedKey() { return issuedKey; }
    public void setIssuedKey(KeyEntity issuedKey) { this.issuedKey = issuedKey; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
    
    public boolean isManualApprovalRequired() { return manualApprovalRequired; }
    public void setManualApprovalRequired(boolean manualApprovalRequired) { this.manualApprovalRequired = manualApprovalRequired; }
    
    public boolean isPending() {
        return status == RequestStatus.PENDING;
    }
    
    public boolean isApproved() {
        return status == RequestStatus.APPROVED;
    }
    
    public boolean isRejected() {
        return status == RequestStatus.REJECTED;
    }
    
    public boolean canApprove() {
        return isPending() && manualApprovalRequired;
    }
    
    public void approve(String reviewerId, String comment, KeyEntity key) {
        this.status = RequestStatus.APPROVED;
        this.reviewerId = reviewerId;
        this.reviewComment = comment;
        this.reviewedAt = System.currentTimeMillis();
        this.issuedKey = key;
        this.updatedAt = this.reviewedAt;
    }
    
    public void reject(String reviewerId, String comment) {
        this.status = RequestStatus.REJECTED;
        this.reviewerId = reviewerId;
        this.reviewComment = comment;
        this.reviewedAt = System.currentTimeMillis();
        this.updatedAt = this.reviewedAt;
    }
    
    public void cancel() {
        this.status = RequestStatus.CANCELLED;
        this.updatedAt = System.currentTimeMillis();
    }
    
    public static NetworkJoinRequest forUser(String userId, String userName, String sceneGroupId) {
        NetworkJoinRequest request = new NetworkJoinRequest();
        request.setRequestType(RequestType.USER_JOIN);
        request.setApplicantId(userId);
        request.setApplicantName(userName);
        request.setApplicantType(OwnerType.USER);
        request.setSceneGroupId(sceneGroupId);
        return request;
    }
    
    public static NetworkJoinRequest forAgent(String agentId, String agentName, String sceneGroupId) {
        NetworkJoinRequest request = new NetworkJoinRequest();
        request.setRequestType(RequestType.AGENT_JOIN);
        request.setApplicantId(agentId);
        request.setApplicantName(agentName);
        request.setApplicantType(OwnerType.AGENT);
        request.setSceneGroupId(sceneGroupId);
        return request;
    }
    
    public static NetworkJoinRequest forDevice(String deviceId, String deviceName, String sceneGroupId) {
        NetworkJoinRequest request = new NetworkJoinRequest();
        request.setRequestType(RequestType.DEVICE_JOIN);
        request.setApplicantId(deviceId);
        request.setApplicantName(deviceName);
        request.setApplicantType(OwnerType.DEVICE);
        request.setSceneGroupId(sceneGroupId);
        return request;
    }
}
