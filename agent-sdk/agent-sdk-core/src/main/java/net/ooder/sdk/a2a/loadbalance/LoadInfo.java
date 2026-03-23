package net.ooder.sdk.a2a.loadbalance;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 负载信息
 *
 * @version 2.3.1
 * @since 2.3.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoadInfo {

    private String agentId;
    private int currentLoad;
    private int maxLoad;
    private double loadPercentage;
    private long lastUpdateTime;
    private int activeRequests;
    private int completedRequests;
    private int failedRequests;

    public boolean isOverloaded() {
        return currentLoad >= maxLoad * 0.8;
    }

    public boolean isAvailable() {
        return currentLoad < maxLoad;
    }

    public double getSuccessRate() {
        int total = completedRequests + failedRequests;
        if (total == 0) return 1.0;
        return (double) completedRequests / total;
    }
}
