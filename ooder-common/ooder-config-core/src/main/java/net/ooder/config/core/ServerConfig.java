package net.ooder.config.core;

import java.io.Serializable;

public class ServerConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer port;
    private String url;
    private AdminConfig admin = new AdminConfig();
    
    public ServerConfig() {
    }
    
    public Integer getPort() {
        return port;
    }
    
    public void setPort(Integer port) {
        this.port = port;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public AdminConfig getAdmin() {
        return admin;
    }
    
    public void setAdmin(AdminConfig admin) {
        this.admin = admin;
    }
    
    public String getValue(String key) {
        if (key.startsWith("admin.")) {
            return admin.getValue(key.substring(6));
        }
        switch (key) {
            case "port":
                return port != null ? String.valueOf(port) : null;
            case "url":
                return url;
            default:
                return null;
        }
    }
    
    public static class AdminConfig implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        private boolean enabled = true;
        private Integer port = 9090;
        private String key = "jds-admin";
        private String host = "127.0.0.1";
        
        public AdminConfig() {
        }
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public Integer getPort() {
            return port;
        }
        
        public void setPort(Integer port) {
            this.port = port;
        }
        
        public String getKey() {
            return key;
        }
        
        public void setKey(String key) {
            this.key = key;
        }
        
        public String getHost() {
            return host;
        }
        
        public void setHost(String host) {
            this.host = host;
        }
        
        public String getValue(String key) {
            switch (key) {
                case "enabled":
                    return String.valueOf(enabled);
                case "StartAdminThread":
                    return String.valueOf(enabled);
                case "port":
                    return this.port != null ? String.valueOf(this.port) : null;
                case "key":
                    return this.key;
                case "host":
                    return host;
                default:
                    return null;
            }
        }
    }
}
