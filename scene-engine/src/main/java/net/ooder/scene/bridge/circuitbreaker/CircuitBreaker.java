package net.ooder.scene.bridge.circuitbreaker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * 熔断器实现
 * 
 * <p>用于 SDK-SE 桥接层的熔断降级，防止级联故障。</p>
 * 
 * <h3>状态机：</h3>
 * <ul>
 *   <li>CLOSED - 正常状态，请求正常通过</li>
 *   <li>OPEN - 熔断状态，请求直接返回降级响应</li>
 *   <li>HALF_OPEN - 半开状态，允许少量请求探测恢复</li>
 * </ul>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class CircuitBreaker {
    
    private static final Logger logger = LoggerFactory.getLogger(CircuitBreaker.class);
    
    public enum State {
        CLOSED,
        OPEN,
        HALF_OPEN
    }
    
    private final String name;
    private final CircuitBreakerConfig config;
    private final AtomicReference<State> state = new AtomicReference<>(State.CLOSED);
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicLong lastFailureTime = new AtomicLong(0);
    private final AtomicInteger halfOpenSuccessCount = new AtomicInteger(0);
    
    public CircuitBreaker(String name, CircuitBreakerConfig config) {
        this.name = name;
        this.config = config;
    }
    
    public CircuitBreaker(String name) {
        this(name, CircuitBreakerConfig.defaultConfig());
    }
    
    /**
     * 执行带熔断保护的调用
     */
    public <T> T execute(Supplier<T> supplier, Supplier<T> fallback) {
        if (!allowRequest()) {
            logger.debug("CircuitBreaker [{}] is OPEN, using fallback", name);
            return fallback.get();
        }
        
        try {
            T result = supplier.get();
            recordSuccess();
            return result;
        } catch (Exception e) {
            recordFailure(e);
            if (config.isFallbackEnabled()) {
                logger.debug("CircuitBreaker [{}] caught exception, using fallback: {}", name, e.getMessage());
                return fallback.get();
            }
            throw e;
        }
    }
    
    /**
     * 执行带熔断保护的调用（无返回值）
     */
    public void execute(Runnable runnable, Runnable fallback) {
        if (!allowRequest()) {
            logger.debug("CircuitBreaker [{}] is OPEN, using fallback", name);
            if (fallback != null) {
                fallback.run();
            }
            return;
        }
        
        try {
            runnable.run();
            recordSuccess();
        } catch (Exception e) {
            recordFailure(e);
            if (config.isFallbackEnabled() && fallback != null) {
                logger.debug("CircuitBreaker [{}] caught exception, using fallback: {}", name, e.getMessage());
                fallback.run();
            } else {
                throw e;
            }
        }
    }
    
    /**
     * 判断是否允许请求通过
     */
    public boolean allowRequest() {
        State currentState = state.get();
        
        switch (currentState) {
            case CLOSED:
                return true;
                
            case OPEN:
                long timeSinceLastFailure = System.currentTimeMillis() - lastFailureTime.get();
                if (timeSinceLastFailure >= config.getOpenToHalfOpenTimeoutMs()) {
                    if (state.compareAndSet(State.OPEN, State.HALF_OPEN)) {
                        halfOpenSuccessCount.set(0);
                        logger.info("CircuitBreaker [{}] transitioned from OPEN to HALF_OPEN", name);
                    }
                    return true;
                }
                return false;
                
            case HALF_OPEN:
                return true;
                
            default:
                return false;
        }
    }
    
    /**
     * 记录成功
     */
    public void recordSuccess() {
        State currentState = state.get();
        
        if (currentState == State.HALF_OPEN) {
            int count = halfOpenSuccessCount.incrementAndGet();
            if (count >= config.getHalfOpenSuccessThreshold()) {
                if (state.compareAndSet(State.HALF_OPEN, State.CLOSED)) {
                    failureCount.set(0);
                    logger.info("CircuitBreaker [{}] transitioned from HALF_OPEN to CLOSED", name);
                }
            }
        } else if (currentState == State.CLOSED) {
            successCount.incrementAndGet();
        }
    }
    
    /**
     * 记录失败
     */
    public void recordFailure(Exception e) {
        lastFailureTime.set(System.currentTimeMillis());
        
        State currentState = state.get();
        
        if (currentState == State.HALF_OPEN) {
            if (state.compareAndSet(State.HALF_OPEN, State.OPEN)) {
                logger.warn("CircuitBreaker [{}] transitioned from HALF_OPEN to OPEN due to failure: {}", 
                    name, e.getMessage());
            }
        } else if (currentState == State.CLOSED) {
            int count = failureCount.incrementAndGet();
            if (count >= config.getFailureThreshold()) {
                if (state.compareAndSet(State.CLOSED, State.OPEN)) {
                    logger.warn("CircuitBreaker [{}] transitioned from CLOSED to OPEN after {} failures", 
                        name, count);
                }
            }
        }
    }
    
    /**
     * 强制打开熔断器
     */
    public void forceOpen() {
        state.set(State.OPEN);
        lastFailureTime.set(System.currentTimeMillis());
        logger.warn("CircuitBreaker [{}] forced OPEN", name);
    }
    
    /**
     * 强制关闭熔断器
     */
    public void forceClose() {
        state.set(State.CLOSED);
        failureCount.set(0);
        logger.info("CircuitBreaker [{}] forced CLOSED", name);
    }
    
    /**
     * 获取当前状态
     */
    public State getState() {
        return state.get();
    }
    
    /**
     * 获取失败计数
     */
    public int getFailureCount() {
        return failureCount.get();
    }
    
    /**
     * 获取成功计数
     */
    public int getSuccessCount() {
        return successCount.get();
    }
    
    /**
     * 获取熔断器名称
     */
    public String getName() {
        return name;
    }
    
    /**
     * 获取配置
     */
    public CircuitBreakerConfig getConfig() {
        return config;
    }
    
    /**
     * 获取指标
     */
    public CircuitBreakerMetrics getMetrics() {
        return new CircuitBreakerMetrics(
            name,
            state.get(),
            failureCount.get(),
            successCount.get(),
            lastFailureTime.get()
        );
    }
    
    /**
     * 熔断器指标
     */
    public static class CircuitBreakerMetrics {
        private final String name;
        private final State state;
        private final int failureCount;
        private final int successCount;
        private final long lastFailureTime;
        
        public CircuitBreakerMetrics(String name, State state, int failureCount, 
                                      int successCount, long lastFailureTime) {
            this.name = name;
            this.state = state;
            this.failureCount = failureCount;
            this.successCount = successCount;
            this.lastFailureTime = lastFailureTime;
        }
        
        public String getName() { return name; }
        public State getState() { return state; }
        public int getFailureCount() { return failureCount; }
        public int getSuccessCount() { return successCount; }
        public long getLastFailureTime() { return lastFailureTime; }
        
        @Override
        public String toString() {
            return String.format("CircuitBreakerMetrics{name=%s, state=%s, failures=%d, successes=%d}", 
                name, state, failureCount, successCount);
        }
    }
}
