package net.ooder.skill.hotplug.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 服务定义
 */
public class ServiceDefinition {

    /**
     * 服务名称
     */
    private String name;

    /**
     * 接口类名
     */
    private String interfaceClass;

    /**
     * 实现类名
     */
    private String implementationClass;

    /**
     * 是否单例
     */
    private boolean singleton = true;

    /**
     * 从列表解析
     */
    public static List<ServiceDefinition> fromList(List<Map<String, Object>> list) {
        List<ServiceDefinition> services = new ArrayList<>();
        for (Map<String, Object> data : list) {
            ServiceDefinition service = new ServiceDefinition();
            service.name = (String) data.get("name");
            service.interfaceClass = (String) data.get("interface");
            service.implementationClass = (String) data.get("implementation");
            service.singleton = (Boolean) data.getOrDefault("singleton", true);
            services.add(service);
        }
        return services;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(String interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public String getImplementationClass() {
        return implementationClass;
    }

    public void setImplementationClass(String implementationClass) {
        this.implementationClass = implementationClass;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    @Override
    public String toString() {
        return "ServiceDefinition{" +
                "name='" + name + '\'' +
                ", interfaceClass='" + interfaceClass + '\'' +
                ", implementationClass='" + implementationClass + '\'' +
                '}';
    }
}
