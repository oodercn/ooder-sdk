package net.ooder.sdk.a2a.loadbalance.impl;

import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.a2a.loadbalance.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 故障转移服务实现
 *
 * @version 2.3.1
 * @since 2.3.1
 */
@Slf4j
public class FailoverServiceImpl implements FailoverService {

    private final Map<String, CircuitBreaker> circuitBreakers = new ConcurrentHashMap<>();
    private final LoadBalancer loadBalancer;

    public FailoverServiceImpl() {
        this(new LoadBalancerImpl());
    }

    public FailoverServiceImpl(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Override
    public <T> T executeWithFailover(Operation<T> operation, FailoverOptions failoverOptions) {
        if (failoverOptions == null) {
            failoverOptions = FailoverOptions.defaultOptions();
        }

        Exception lastException = null;
        int maxAttempts = failoverOptions.getMaxRetries() + 1;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return operation.execute();
            } catch (Exception e) {
                lastException = e;
                log.warn("Operation failed on attempt {}/{}: {}", attempt, maxAttempts, e.getMessage());

                if (attempt < maxAttempts) {
                    try {
                        Thread.sleep(failoverOptions.getRetryDelayMs());
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Operation interrupted", ie);
                    }
                }
            }
        }

        throw new RuntimeException("Operation failed after " + maxAttempts + " attempts", lastException);
    }

    @Override
    public void recordFailure(String agentId, Exception error) {
        CircuitBreaker cb = circuitBreakers.computeIfAbsent(agentId, id -> 
                new CircuitBreaker(5, 30000, 3));
        
        cb.recordFailure();
        
        if (cb.getState() == CircuitBreakerState.OPEN) {
            log.warn("Circuit breaker opened for agent: {}", agentId);
        }
    }

    @Override
    public boolean isAvailable(String agentId) {
        CircuitBreaker cb = circuitBreakers.get(agentId);
        if (cb == null) {
            return true;
        }
        return cb.getState() != CircuitBreakerState.OPEN;
    }

    @Override
    public CircuitBreakerState getCircuitBreakerState(String agentId) {
        CircuitBreaker cb = circuitBreakers.get(agentId);
        return cb != null ? cb.getState() : CircuitBreakerState.CLOSED;
    }

    @Override
    public void resetCircuitBreaker(String agentId) {
        circuitBreakers.remove(agentId);
        log.info("Circuit breaker reset for agent: {}", agentId);
    }

    private static class CircuitBreaker {
        private final int failureThreshold;
        private final long openDurationMs;
        private final int halfOpenRequests;
        
        private final AtomicInteger failureCount = new AtomicInteger(0);
        private final AtomicInteger halfOpenSuccessCount = new AtomicInteger(0);
        private volatile CircuitBreakerState state = CircuitBreakerState.CLOSED;
        private volatile long lastFailureTime = 0;

        CircuitBreaker(int failureThreshold, long openDurationMs, int halfOpenRequests) {
            this.failureThreshold = failureThreshold;
            this.openDurationMs = openDurationMs;
            this.halfOpenRequests = halfOpenRequests;
        }

        synchronized void recordFailure() {
            failureCount.incrementAndGet();
            lastFailureTime = System.currentTimeMillis();

            if (state == CircuitBreakerState.HALF_OPEN) {
                state = CircuitBreakerState.OPEN;
                halfOpenSuccessCount.set(0);
            } else if (failureCount.get() >= failureThreshold) {
                state = CircuitBreakerState.OPEN;
            }
        }

        synchronized void recordSuccess() {
            if (state == CircuitBreakerState.HALF_OPEN) {
                if (halfOpenSuccessCount.incrementAndGet() >= halfOpenRequests) {
                    state = CircuitBreakerState.CLOSED;
                    failureCount.set(0);
                    halfOpenSuccessCount.set(0);
                }
            }
        }

        CircuitBreakerState getState() {
            if (state == CircuitBreakerState.OPEN) {
                if (System.currentTimeMillis() - lastFailureTime >= openDurationMs) {
                    state = CircuitBreakerState.HALF_OPEN;
                    halfOpenSuccessCount.set(0);
                }
            }
            return state;
        }
    }
}
