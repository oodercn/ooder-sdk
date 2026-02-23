package net.ooder.sdk.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class DriverInvocationHandler implements InvocationHandler {
    
    private final Object target;
    private final String interfaceId;
    private final String skillId;
    private final Map<String, MethodStats> methodStats = new ConcurrentHashMap<>();
    private volatile boolean active = true;
    
    public DriverInvocationHandler(Object target, String interfaceId, String skillId) {
        this.target = target;
        this.interfaceId = interfaceId;
        this.skillId = skillId;
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!active) {
            throw new IllegalStateException("Driver proxy is deactivated: " + interfaceId);
        }
        
        String methodName = method.getName();
        
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = method.invoke(target, args);
            
            recordSuccess(methodName, System.currentTimeMillis() - startTime);
            
            return result;
            
        } catch (Exception e) {
            recordFailure(methodName, System.currentTimeMillis() - startTime);
            
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            throw new DriverInvocationException(
                "Driver invocation failed: " + interfaceId + "." + methodName, cause);
        }
    }
    
    private void recordSuccess(String methodName, long duration) {
        MethodStats stats = methodStats.computeIfAbsent(methodName, k -> new MethodStats());
        stats.recordSuccess(duration);
    }
    
    private void recordFailure(String methodName, long duration) {
        MethodStats stats = methodStats.computeIfAbsent(methodName, k -> new MethodStats());
        stats.recordFailure(duration);
    }
    
    public void deactivate() {
        this.active = false;
    }
    
    public void activate() {
        this.active = true;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public String getInterfaceId() {
        return interfaceId;
    }
    
    public String getSkillId() {
        return skillId;
    }
    
    public Object getTarget() {
        return target;
    }
    
    public Map<String, MethodStats> getMethodStats() {
        return new ConcurrentHashMap<>(methodStats);
    }
    
    public static class MethodStats {
        private final AtomicLong successCount = new AtomicLong(0);
        private final AtomicLong failureCount = new AtomicLong(0);
        private final AtomicLong totalDuration = new AtomicLong(0);
        private volatile long lastInvokeTime;
        
        public void recordSuccess(long duration) {
            successCount.incrementAndGet();
            totalDuration.addAndGet(duration);
            lastInvokeTime = System.currentTimeMillis();
        }
        
        public void recordFailure(long duration) {
            failureCount.incrementAndGet();
            totalDuration.addAndGet(duration);
            lastInvokeTime = System.currentTimeMillis();
        }
        
        public long getSuccessCount() { return successCount.get(); }
        public long getFailureCount() { return failureCount.get(); }
        public long getTotalDuration() { return totalDuration.get(); }
        public long getLastInvokeTime() { return lastInvokeTime; }
        
        public long getTotalCount() {
            return successCount.get() + failureCount.get();
        }
        
        public double getAverageDuration() {
            long total = getTotalCount();
            return total > 0 ? (double) totalDuration.get() / total : 0;
        }
        
        public double getSuccessRate() {
            long total = getTotalCount();
            return total > 0 ? (double) successCount.get() / total : 0;
        }
    }
}
