package net.ooder.scene.core.security;

import java.util.Map;

/**
 * 资源访问统计
 */
public class ResourceAccessStats {
    private String resourceType;
    private String resourceId;
    private long totalAccesses;
    private Map<String, Long> operationCounts;
    private Map<String, Long> userCounts;
    private Map<String, Long> resultCounts;

    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
    public long getTotalAccesses() { return totalAccesses; }
    public void setTotalAccesses(long totalAccesses) { this.totalAccesses = totalAccesses; }
    public Map<String, Long> getOperationCounts() { return operationCounts; }
    public void setOperationCounts(Map<String, Long> operationCounts) { this.operationCounts = operationCounts; }
    public Map<String, Long> getUserCounts() { return userCounts; }
    public void setUserCounts(Map<String, Long> userCounts) { this.userCounts = userCounts; }
    public Map<String, Long> getResultCounts() { return resultCounts; }
    public void setResultCounts(Map<String, Long> resultCounts) { this.resultCounts = resultCounts; }
}
