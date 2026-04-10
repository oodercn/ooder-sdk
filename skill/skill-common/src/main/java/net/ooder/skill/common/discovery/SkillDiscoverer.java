package net.ooder.skill.common.discovery;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SkillDiscoverer {
    
    CompletableFuture<DiscoveryResult> discover(DiscoveryRequest request);
    
    CompletableFuture<CapabilityDTO> discoverOne(String skillId);
    
    DiscoveryMethod getMethod();
    
    boolean isAvailable();
}
