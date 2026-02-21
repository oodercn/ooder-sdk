package net.ooder.sdk.skill.driver;

public interface ProxyFactory {
    
    <T> T createProxy(Class<T> providerType, InterfaceDefinition interfaceDef, 
                      EndpointConfig endpoint);
    
    <T> T createFallback(Class<T> providerType, Class<?> fallbackClass);
}