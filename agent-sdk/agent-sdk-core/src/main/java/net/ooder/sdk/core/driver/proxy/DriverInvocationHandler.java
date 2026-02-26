package net.ooder.sdk.core.driver.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public class DriverInvocationHandler implements InvocationHandler {
    
    private final String interfaceId;
    private final String skillId;
    private final Object target;
    private final Map<String, Object> context;
    private Callable<Object> fallback;
    
    public DriverInvocationHandler(String interfaceId, Object target) {
        this.interfaceId = interfaceId;
        this.skillId = null;
        this.target = target;
        this.context = new ConcurrentHashMap<>();
    }
    
    public DriverInvocationHandler(String interfaceId, String skillId, Object target) {
        this.interfaceId = interfaceId;
        this.skillId = skillId;
        this.target = target;
        this.context = new ConcurrentHashMap<>();
    }
    
    public void setFallback(Callable<Object> fallback) {
        this.fallback = fallback;
    }
    
    public void setContext(String key, Object value) {
        context.put(key, value);
    }
    
    public Object getContext(String key) {
        return context.get(key);
    }
    
    public String getInterfaceId() { return interfaceId; }
    public String getSkillId() { return skillId; }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(target, args);
        } catch (Exception e) {
            if (fallback != null) {
                try {
                    return fallback.call();
                } catch (Exception fallbackError) {
                    throw new DriverInvocationException(
                        interfaceId, 
                        "Fallback also failed: " + fallbackError.getMessage(), 
                        fallbackError
                    );
                }
            }
            throw new DriverInvocationException(
                interfaceId, 
                "Method invocation failed: " + method.getName(), 
                e.getCause() != null ? e.getCause() : e
            );
        }
    }
}
