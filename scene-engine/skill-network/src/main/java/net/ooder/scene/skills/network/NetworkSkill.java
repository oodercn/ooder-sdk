package net.ooder.scene.skills.network;

import net.ooder.scene.provider.NetworkStatus;
import net.ooder.scene.provider.NetworkStats;
import net.ooder.scene.provider.NetworkLink;
import net.ooder.scene.provider.NetworkRoute;
import net.ooder.scene.provider.NetworkTopology;
import net.ooder.scene.provider.NetworkQuality;

import java.util.List;
import java.util.Map;

/**
 * NetworkSkill 网络技能接口
 * 
 * <p>提供网络管理能力，包括网络状态监控、链路管理、路由查找、拓扑管理等。</p>
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
public interface NetworkSkill {

    String getSkillId();
    
    String getSkillName();
    
    String getSkillVersion();
    
    List<String> getCapabilities();
    
    void initialize(NetworkConfig config);
    
    void start();
    
    void stop();
    
    boolean isRunning();
    
    NetworkStatus getStatus();
    
    NetworkStats getStats();
    
    List<NetworkLink> listLinks();
    
    NetworkLink getLink(String linkId);
    
    boolean disconnectLink(String linkId);
    
    boolean reconnectLink(String linkId);
    
    List<NetworkRoute> listRoutes();
    
    NetworkRoute getRoute(String routeId);
    
    NetworkRoute findRoute(String source, String target, String algorithm, int maxHops);
    
    NetworkTopology getTopology();
    
    NetworkQuality getQuality();
    
    boolean isSupport(String capability);
    
    Object invoke(String capability, Map<String, Object> params);
}
