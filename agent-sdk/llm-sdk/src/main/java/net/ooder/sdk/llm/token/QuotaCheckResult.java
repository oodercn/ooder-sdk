package net.ooder.sdk.llm.token;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 配额检查结果
 *
 * @version 2.3.1
 * @since 2.3.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotaCheckResult {

    private boolean allowed;
    private int remainingQuota;
    private int totalQuota;
    private int usedQuota;
    private String denialReason;
    private long resetTime;

    public static QuotaCheckResult allowed(int remaining, int total, int used) {
        return QuotaCheckResult.builder()
                .allowed(true)
                .remainingQuota(remaining)
                .totalQuota(total)
                .usedQuota(used)
                .build();
    }

    public static QuotaCheckResult denied(String reason) {
        return QuotaCheckResult.builder()
                .allowed(false)
                .denialReason(reason)
                .build();
    }
}
