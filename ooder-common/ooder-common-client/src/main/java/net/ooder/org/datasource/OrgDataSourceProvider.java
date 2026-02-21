package net.ooder.org.datasource;

import net.ooder.common.ConfigCode;
import net.ooder.org.OrgManager;

import java.util.Map;

public interface OrgDataSourceProvider {
    
    DataSourceType getType();
    
    String getProviderName();
    
    void initialize(Map<String, Object> config);
    
    boolean isInitialized();
    
    boolean testConnection();
    
    OrgManager createOrgManager(ConfigCode configCode);
    
    void shutdown();
    
    Map<String, Object> getStatus();
}
