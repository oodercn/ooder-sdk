package net.ooder.scene.protocol;

import net.ooder.sdk.nexus.spi.ProtocolProvider;
import net.ooder.sdk.southbound.protocol.CollaborationProtocol;
import net.ooder.sdk.southbound.protocol.DiscoveryProtocol;
import net.ooder.sdk.southbound.protocol.LoginProtocol;
import net.ooder.sdk.southbound.protocol.RoleProtocol;
import net.ooder.sdk.southbound.protocol.impl.CollaborationProtocolImpl;
import net.ooder.sdk.southbound.protocol.impl.DiscoveryProtocolImpl;
import net.ooder.sdk.southbound.protocol.impl.LoginProtocolImpl;
import net.ooder.sdk.southbound.protocol.impl.RoleProtocolImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Engine 层提供的 ProtocolProvider 实现。
 * 通过 SPI 机制向 SDK 提供协议实现。
 */
public class EngineProtocolProvider implements ProtocolProvider {
    
    private static final Logger log = LoggerFactory.getLogger(EngineProtocolProvider.class);
    
    private final LoginProtocol loginProtocol;
    private final DiscoveryProtocol discoveryProtocol;
    private final CollaborationProtocol collaborationProtocol;
    private final RoleProtocol roleProtocol;
    
    public EngineProtocolProvider() {
        log.info("Initializing EngineProtocolProvider");
        
        // 使用 SDK 的协议实现（或 Engine 自己的实现）
        this.loginProtocol = new LoginProtocolImpl();
        this.discoveryProtocol = new DiscoveryProtocolImpl();
        this.collaborationProtocol = new CollaborationProtocolImpl();
        this.roleProtocol = new RoleProtocolImpl();
        
        log.info("EngineProtocolProvider initialized successfully");
    }
    
    @Override
    public LoginProtocol getLoginProtocol() {
        return loginProtocol;
    }
    
    @Override
    public DiscoveryProtocol getDiscoveryProtocol() {
        return discoveryProtocol;
    }
    
    @Override
    public CollaborationProtocol getCollaborationProtocol() {
        return collaborationProtocol;
    }
    
    @Override
    public RoleProtocol getRoleProtocol() {
        return roleProtocol;
    }
    
    @Override
    public String getProviderName() {
        return "Engine-Protocol-Provider";
    }
    
    @Override
    public int getPriority() {
        return 100; // Engine 提供的实现优先级高于 SDK 默认实现
    }
}
