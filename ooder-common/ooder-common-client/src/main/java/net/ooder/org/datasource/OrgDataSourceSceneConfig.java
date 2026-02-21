package net.ooder.org.datasource;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OrgDataSourceSceneConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String sceneId;
    private DataSourceType dataSourceType = DataSourceType.JSON;
    private Map<String, Object> jsonConfig = new HashMap<String, Object>();
    private Map<String, Object> databaseConfig = new HashMap<String, Object>();
    private Map<String, Object> dingtalkConfig = new HashMap<String, Object>();
    private Map<String, Object> feishuConfig = new HashMap<String, Object>();
    private Map<String, Object> wecomConfig = new HashMap<String, Object>();
    private SyncConfig syncConfig = new SyncConfig();
    private Map<String, Object> capabilities = new HashMap<String, Object>();
    
    public static class SyncConfig implements Serializable {
        private boolean enabled = false;
        private long interval = 300000L;
        private int batchSize = 100;
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public long getInterval() {
            return interval;
        }
        
        public void setInterval(long interval) {
            this.interval = interval;
        }
        
        public int getBatchSize() {
            return batchSize;
        }
        
        public void setBatchSize(int batchSize) {
            this.batchSize = batchSize;
        }
    }
    
    public String getSceneId() {
        return sceneId;
    }
    
    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }
    
    public DataSourceType getDataSourceType() {
        return dataSourceType;
    }
    
    public void setDataSourceType(DataSourceType dataSourceType) {
        this.dataSourceType = dataSourceType;
    }
    
    public void setDataSourceType(String code) {
        this.dataSourceType = DataSourceType.fromCode(code);
    }
    
    public Map<String, Object> getJsonConfig() {
        return jsonConfig;
    }
    
    public void setJsonConfig(Map<String, Object> jsonConfig) {
        this.jsonConfig = jsonConfig;
    }
    
    public Map<String, Object> getDatabaseConfig() {
        return databaseConfig;
    }
    
    public void setDatabaseConfig(Map<String, Object> databaseConfig) {
        this.databaseConfig = databaseConfig;
    }
    
    public Map<String, Object> getDingtalkConfig() {
        return dingtalkConfig;
    }
    
    public void setDingtalkConfig(Map<String, Object> dingtalkConfig) {
        this.dingtalkConfig = dingtalkConfig;
    }
    
    public Map<String, Object> getFeishuConfig() {
        return feishuConfig;
    }
    
    public void setFeishuConfig(Map<String, Object> feishuConfig) {
        this.feishuConfig = feishuConfig;
    }
    
    public Map<String, Object> getWecomConfig() {
        return wecomConfig;
    }
    
    public void setWecomConfig(Map<String, Object> wecomConfig) {
        this.wecomConfig = wecomConfig;
    }
    
    public SyncConfig getSyncConfig() {
        return syncConfig;
    }
    
    public void setSyncConfig(SyncConfig syncConfig) {
        this.syncConfig = syncConfig;
    }
    
    public Map<String, Object> getCapabilities() {
        return capabilities;
    }
    
    public void setCapabilities(Map<String, Object> capabilities) {
        this.capabilities = capabilities;
    }
    
    public Map<String, Object> getActiveConfig() {
        switch (dataSourceType) {
            case JSON:
                return jsonConfig;
            case DATABASE:
                return databaseConfig;
            case DINGTALK:
                return dingtalkConfig;
            case FEISHU:
                return feishuConfig;
            case WECOM:
                return wecomConfig;
            default:
                return jsonConfig;
        }
    }
    
    public Set<String> getPresetCapabilities() {
        Set<String> result = new java.util.HashSet<String>();
        switch (dataSourceType) {
            case JSON:
                result.add("org-query");
                result.add("person-query");
                result.add("role-query");
                break;
            case DATABASE:
                result.add("org-query");
                result.add("org-admin");
                result.add("person-query");
                result.add("person-admin");
                result.add("role-query");
                result.add("role-admin");
                result.add("user-auth");
                break;
            case DINGTALK:
            case FEISHU:
            case WECOM:
                result.add("org-query");
                result.add("org-sync");
                result.add("person-query");
                result.add("person-sync");
                result.add("role-query");
                break;
            default:
                result.add("org-query");
                result.add("person-query");
                result.add("role-query");
                break;
        }
        return result;
    }
    
    @Override
    public String toString() {
        return "OrgDataSourceSceneConfig{" +
            "sceneId='" + sceneId + '\'' +
            ", dataSourceType=" + dataSourceType +
            ", syncEnabled=" + syncConfig.enabled +
            '}';
    }
}
