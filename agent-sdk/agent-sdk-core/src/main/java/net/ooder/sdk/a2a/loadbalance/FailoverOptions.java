package net.ooder.sdk.a2a.loadbalance;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 故障转移选项
 *
 * @version 2.3.1
 * @since 2.3.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FailoverOptions {

    private int maxRetries;
    private long retryDelayMs;
    private List<String> fallbackAgents;
    private CircuitBreakerConfig circuitBreaker;
    private boolean enableFallback;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CircuitBreakerConfig {
        private int failureThreshold;
        private long openDurationMs;
        private int halfOpenRequests;
    }

    public static FailoverOptions defaultOptions() {
        return FailoverOptions.builder()
                .maxRetries(3)
                .retryDelayMs(1000)
                .enableFallback(true)
                .circuitBreaker(CircuitBreakerConfig.builder()
                        .failureThreshold(5)
                        .openDurationMs(30000)
                        .halfOpenRequests(3)
                        .build())
                .build();
    }

    public static FailoverOptions noRetry() {
        return FailoverOptions.builder()
                .maxRetries(0)
                .enableFallback(false)
                .build();
    }
}
