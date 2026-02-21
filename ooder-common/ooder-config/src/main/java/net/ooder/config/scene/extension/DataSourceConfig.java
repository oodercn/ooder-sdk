package net.ooder.config.scene.extension;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DataSourceConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    public static final String TYPE_DATABASE = "database";
    public static final String TYPE_DINGTALK = "dingtalk";
    public static final String TYPE_FEISHU = "feishu";
    public static final String TYPE_WECOM = "wecom";
    public static final String TYPE_LDAP = "ldap";
    public static final String TYPE_JSON = "json";
    
    private String type = TYPE_DATABASE;
    private boolean enabled = true;
    private String prefix;
    private Map<String, Object> config = new HashMap<String, Object>();
    private int priority = 0;
    
    public DataSourceConfig() {
    }
    
    public DataSourceConfig(String type) {
        this.type = type;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    public Map<String, Object> getConfig() {
        return config;
    }
    
    public void setConfig(Map<String, Object> config) {
        this.config = config != null ? config : new HashMap<String, Object>();
    }
    
    public Object get(String key) {
        return config.get(key);
    }
    
    public DataSourceConfig set(String key, Object value) {
        this.config.put(key, value);
        return this;
    }
    
    public String getString(String key) {
        Object value = config.get(key);
        return value != null ? value.toString() : null;
    }
    
    public String getString(String key, String defaultValue) {
        String value = getString(key);
        return value != null ? value : defaultValue;
    }
    
    public int getInt(String key, int defaultValue) {
        Object value = config.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
    
    public boolean getBoolean(String key, boolean defaultValue) {
        Object value = config.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return defaultValue;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public boolean isDatabase() {
        return TYPE_DATABASE.equals(type);
    }
    
    public boolean isDingTalk() {
        return TYPE_DINGTALK.equals(type);
    }
    
    public boolean isFeishu() {
        return TYPE_FEISHU.equals(type);
    }
    
    public boolean isWeCom() {
        return TYPE_WECOM.equals(type);
    }
    
    public boolean isLdap() {
        return TYPE_LDAP.equals(type);
    }
    
    public boolean isJson() {
        return TYPE_JSON.equals(type);
    }
    
    public boolean isThirdParty() {
        return isDingTalk() || isFeishu() || isWeCom() || isLdap();
    }
    
    public static DataSourceConfig database() {
        return new DataSourceConfig(TYPE_DATABASE);
    }
    
    public static DataSourceConfig dingTalk() {
        return new DataSourceConfig(TYPE_DINGTALK);
    }
    
    public static DataSourceConfig feishu() {
        return new DataSourceConfig(TYPE_FEISHU);
    }
    
    public static DataSourceConfig weCom() {
        return new DataSourceConfig(TYPE_WECOM);
    }
    
    public static DataSourceConfig ldap() {
        return new DataSourceConfig(TYPE_LDAP);
    }
    
    public static DataSourceConfig json() {
        return new DataSourceConfig(TYPE_JSON);
    }
    
    @Override
    public String toString() {
        return "DataSourceConfig{" +
            "type='" + type + '\'' +
            ", enabled=" + enabled +
            ", prefix='" + prefix + '\'' +
            '}';
    }
}
