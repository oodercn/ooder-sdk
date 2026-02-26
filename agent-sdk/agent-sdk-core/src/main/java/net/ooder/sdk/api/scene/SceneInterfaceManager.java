package net.ooder.sdk.api.scene;

import net.ooder.sdk.api.skill.InterfaceDefinition;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface SceneInterfaceManager {
    
    void initialize(String sceneId);
    
    void shutdown();
    
    CompletableFuture<Void> bindInterface(String sceneId, String interfaceId, String skillId);
    
    CompletableFuture<Void> unbindInterface(String sceneId, String interfaceId);
    
    CompletableFuture<Void> bindInterfaces(String sceneId, Map<String, String> interfaceBindings);
    
    Optional<InterfaceBinding> getBinding(String sceneId, String interfaceId);
    
    List<InterfaceBinding> getBindings(String sceneId);
    
    <T> Optional<T> getInterface(String sceneId, String interfaceId, Class<T> type);
    
    <T> Optional<T> getInterface(String sceneId, String interfaceId, String skillId, Class<T> type);
    
    CompletableFuture<ResolvedInterface> resolveInterface(String sceneId, String interfaceId);
    
    CompletableFuture<List<ResolvedInterface>> resolveAllInterfaces(String sceneId);
    
    boolean hasInterface(String sceneId, String interfaceId);
    
    List<String> getAvailableInterfaces(String sceneId);
    
    void setPreferredImplementation(String sceneId, String interfaceId, String skillId);
    
    String getPreferredImplementation(String sceneId, String interfaceId);
    
    void registerInterfaceDefinition(InterfaceDefinition definition);
    
    void unregisterInterfaceDefinition(String interfaceId);
    
    Optional<InterfaceDefinition> getInterfaceDefinition(String interfaceId);
    
    List<InterfaceDefinition> getAllInterfaceDefinitions();
    
    InterfacePoolStats getPoolStats(String sceneId);
    
    void clearScene(String sceneId);
    
    class InterfaceBinding {
        private String sceneId;
        private String interfaceId;
        private String skillId;
        private Object driver;
        private long bindTime;
        private long lastAccessTime;
        private int accessCount;
        private boolean active;
        
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        
        public String getInterfaceId() { return interfaceId; }
        public void setInterfaceId(String interfaceId) { this.interfaceId = interfaceId; }
        
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        
        public Object getDriver() { return driver; }
        public void setDriver(Object driver) { this.driver = driver; }
        
        public long getBindTime() { return bindTime; }
        public void setBindTime(long bindTime) { this.bindTime = bindTime; }
        
        public long getLastAccessTime() { return lastAccessTime; }
        public void setLastAccessTime(long lastAccessTime) { this.lastAccessTime = lastAccessTime; }
        
        public int getAccessCount() { return accessCount; }
        public void setAccessCount(int accessCount) { this.accessCount = accessCount; }
        
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        
        public void recordAccess() {
            this.lastAccessTime = System.currentTimeMillis();
            this.accessCount++;
        }
    }
    
    class InterfacePoolStats {
        private String sceneId;
        private int totalBindings;
        private int activeBindings;
        private int inactiveBindings;
        private long totalAccessCount;
        private long lastAccessTime;
        
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        
        public int getTotalBindings() { return totalBindings; }
        public void setTotalBindings(int totalBindings) { this.totalBindings = totalBindings; }
        
        public int getActiveBindings() { return activeBindings; }
        public void setActiveBindings(int activeBindings) { this.activeBindings = activeBindings; }
        
        public int getInactiveBindings() { return inactiveBindings; }
        public void setInactiveBindings(int inactiveBindings) { this.inactiveBindings = inactiveBindings; }
        
        public long getTotalAccessCount() { return totalAccessCount; }
        public void setTotalAccessCount(long totalAccessCount) { this.totalAccessCount = totalAccessCount; }
        
        public long getLastAccessTime() { return lastAccessTime; }
        public void setLastAccessTime(long lastAccessTime) { this.lastAccessTime = lastAccessTime; }
    }
    
    class ResolvedInterface {
        private String interfaceId;
        private String skillId;
        private Object instance;
        private boolean fallback;
        
        public String getInterfaceId() { return interfaceId; }
        public void setInterfaceId(String interfaceId) { this.interfaceId = interfaceId; }
        
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        
        public Object getInstance() { return instance; }
        public void setInstance(Object instance) { this.instance = instance; }
        
        public boolean isFallback() { return fallback; }
        public void setFallback(boolean fallback) { this.fallback = fallback; }
    }
}
