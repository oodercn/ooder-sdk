package net.ooder.config.spring;

import net.ooder.config.core.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ooder")
public class OoderConfigProperties extends OoderConfig {
    
    private static final long serialVersionUID = 1L;
    
    public OoderConfigProperties() {
        super();
    }
    
    public void setJds(JdsConfig jds) {
        super.setJds(jds);
    }
    
    public void setServer(ServerConfig server) {
        super.setServer(server);
    }
    
    public void setCluster(ClusterConfig cluster) {
        super.setCluster(cluster);
    }
    
    public void setSession(SessionConfig session) {
        super.setSession(session);
    }
    
    public void setCache(CacheConfig cache) {
        super.setCache(cache);
    }
    
    public void setUser(UserConfig user) {
        super.setUser(user);
    }
}
