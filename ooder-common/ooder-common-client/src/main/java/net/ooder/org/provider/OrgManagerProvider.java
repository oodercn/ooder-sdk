package net.ooder.org.provider;

import net.ooder.org.OrgManager;
import net.ooder.common.ConfigCode;

import java.util.Map;

public interface OrgManagerProvider {
    
    String getProviderType();
    
    OrgManager createOrgManager(ConfigCode configCode, Map<String, Object> properties);
    
    boolean isAvailable();
    
    void initialize(Map<String, Object> properties);
    
    String getProviderName();
    
    int getPriority();
}
