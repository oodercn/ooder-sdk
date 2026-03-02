package net.ooder.scene.discovery;

import java.util.Map;

/**
 * 发现项
 * 
 * <p>表示一个被发现的能力或资源，由DiscoveryProvider返回。</p>
 * 
 * <p>注意：此类用于DiscoveryProvider接口，与SDK的SkillPackage不同。</p>
 * 
 * @author Ooder Team
 * @version 2.3
 * @since 0.8.0
 * @see DiscoveryProvider
 */
public class DiscoveredItem {
    
    private String id;
    private String name;
    private String type;
    private String version;
    private String description;
    private String provider;
    private Map<String, Object> metadata;
    private long discoveredTime;
    
    public DiscoveredItem() {
        this.discoveredTime = System.currentTimeMillis();
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public long getDiscoveredTime() { return discoveredTime; }
    public void setDiscoveredTime(long discoveredTime) { this.discoveredTime = discoveredTime; }
}
