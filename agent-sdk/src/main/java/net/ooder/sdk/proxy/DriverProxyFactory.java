package net.ooder.sdk.proxy;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DriverProxyFactory {
    
    private static final Map<Class<?>, Object> proxyCache = new ConcurrentHashMap<>();
    
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(Class<T> interfaceType, DriverInvocationHandler handler) {
        if (interfaceType == null) {
            throw new IllegalArgumentException("Interface type cannot be null");
        }
        
        if (!interfaceType.isInterface()) {
            throw new IllegalArgumentException("Type must be an interface: " + interfaceType.getName());
        }
        
        return (T) Proxy.newProxyInstance(
            interfaceType.getClassLoader(),
            new Class<?>[] { interfaceType },
            handler
        );
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T createCachedProxy(Class<T> interfaceType, DriverInvocationHandler handler) {
        return proxyCache.computeIfAbsent(interfaceType, 
            type -> createProxy(interfaceType, handler));
    }
    
    public static void clearCache() {
        proxyCache.clear();
    }
    
    public static int getCacheSize() {
        return proxyCache.size();
    }
}
