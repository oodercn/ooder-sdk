package net.ooder.sdk.llm.token;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * 配额使用统计
 *
 * @version 2.3.1
 * @since 2.3.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotaUsageStats {

    private QuotaScope scope;
    private int totalQuota;
    private int usedQuota;
    private int remainingQuota;
    private double usagePercentage;
    private Map<String, Integer> usageByModel;
    private Map<String, Integer> usageByOperation;
    private long periodStart;
    private long periodEnd;
    private int requestCount;

    public boolean isNearLimit(double threshold) {
        return usagePercentage >= threshold;
    }
}
