package net.ooder.scene.provider.model.config;

import java.util.List;
import java.util.Map;

public class NetworkConfig {
    
    private String hostname;
    private String gateway;
    private String subnet;
    private List<String> dnsServers;
    private boolean dhcpEnabled;
    private String ipAddress;
    private Map<String, Object> extra;
    
    public String getHostname() {
        return hostname;
    }
    
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
    
    public String getGateway() {
        return gateway;
    }
    
    public void setGateway(String gateway) {
        this.gateway = gateway;
    }
    
    public String getSubnet() {
        return subnet;
    }
    
    public void setSubnet(String subnet) {
        this.subnet = subnet;
    }
    
    public List<String> getDnsServers() {
        return dnsServers;
    }
    
    public void setDnsServers(List<String> dnsServers) {
        this.dnsServers = dnsServers;
    }
    
    public boolean isDhcpEnabled() {
        return dhcpEnabled;
    }
    
    public void setDhcpEnabled(boolean dhcpEnabled) {
        this.dhcpEnabled = dhcpEnabled;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public Map<String, Object> getExtra() {
        return extra;
    }
    
    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }
}
