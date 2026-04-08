package net.ooder.config.core;

import java.io.Serializable;

public class ClusterConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private boolean enabled = false;
    private UdpConfig udp = new UdpConfig();
    
    public ClusterConfig() {
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public UdpConfig getUdp() {
        return udp;
    }
    
    public void setUdp(UdpConfig udp) {
        this.udp = udp;
    }
    
    public String getValue(String key) {
        if (key.startsWith("udp.")) {
            return udp.getValue(key.substring(4));
        }
        switch (key) {
            case "enabled":
                return String.valueOf(enabled);
            default:
                return null;
        }
    }
    
    public static class UdpConfig implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        private boolean enabled = true;
        private Integer port = 8087;
        private String code = "utf-8";
        private String serverIP;
        private boolean self = true;
        
        public UdpConfig() {
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
        
        public String getCode() {
            return code;
        }
        
        public void setCode(String code) {
            this.code = code;
        }
        
        public String getServerIP() {
            return serverIP;
        }
        
        public void setServerIP(String serverIP) {
            this.serverIP = serverIP;
        }
        
        public boolean isSelf() {
            return self;
        }
        
        public void setSelf(boolean self) {
            this.self = self;
        }
        
        public String getValue(String key) {
            switch (key) {
                case "enabled":
                    return String.valueOf(enabled);
                case "port":
                    return port != null ? String.valueOf(port) : null;
                case "code":
                    return code;
                case "serverIP":
                    return serverIP;
                case "self":
                    return String.valueOf(self);
                default:
                    return null;
            }
        }
    }
}
