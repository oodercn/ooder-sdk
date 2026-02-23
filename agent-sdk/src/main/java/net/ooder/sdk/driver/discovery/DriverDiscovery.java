package net.ooder.sdk.driver.discovery;

import java.util.List;

public interface DriverDiscovery {
    
    List<DiscoveredDriver> discover();
    
    List<DiscoveredDriver> discover(String basePackage);
    
    List<DiscoveredDriver> discoverByAnnotation(Class<?> annotationClass);
    
    List<DiscoveredDriver> discoverByInterface(String interfaceId);
    
    void addDiscoveryListener(DriverDiscoveryListener listener);
    
    void removeDiscoveryListener(DriverDiscoveryListener listener);
    
    void setScanPackages(List<String> packages);
    
    List<String> getScanPackages();
    
    class DiscoveredDriver {
        private String interfaceId;
        private String skillId;
        private String className;
        private Class<?> driverClass;
        private Object driverInstance;
        private int priority;
        private boolean singleton;
        private String description;
        
        public String getInterfaceId() { return interfaceId; }
        public void setInterfaceId(String interfaceId) { this.interfaceId = interfaceId; }
        
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        
        public Class<?> getDriverClass() { return driverClass; }
        public void setDriverClass(Class<?> driverClass) { this.driverClass = driverClass; }
        
        public Object getDriverInstance() { return driverInstance; }
        public void setDriverInstance(Object driverInstance) { this.driverInstance = driverInstance; }
        
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
        
        public boolean isSingleton() { return singleton; }
        public void setSingleton(boolean singleton) { this.singleton = singleton; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
