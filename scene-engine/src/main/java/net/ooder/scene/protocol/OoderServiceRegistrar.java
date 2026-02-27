package net.ooder.scene.protocol;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OoderServiceRegistrar {
    private Map<String, ServiceInfo> services;

    public OoderServiceRegistrar() {
        this.services = new ConcurrentHashMap<>();
    }

    public void registerService(String serviceName, int port) {
        ServiceInfo info = new ServiceInfo(serviceName, port);
        services.put(serviceName, info);
        System.out.println("Registered service: " + serviceName + " on port " + port);
    }

    public void unregisterService(String serviceName) {
        services.remove(serviceName);
        System.out.println("Unregistered service: " + serviceName);
    }

    public void unregisterServices() {
        services.clear();
        System.out.println("Unregistered all services");
    }

    public ServiceInfo getService(String serviceName) {
        return services.get(serviceName);
    }

    public Map<String, ServiceInfo> getAllServices() {
        return services;
    }

    public static class ServiceInfo {
        private String serviceName;
        private int port;

        public ServiceInfo(String serviceName, int port) {
            this.serviceName = serviceName;
            this.port = port;
        }

        public String getServiceName() {
            return serviceName;
        }

        public int getPort() {
            return port;
        }
    }
}
