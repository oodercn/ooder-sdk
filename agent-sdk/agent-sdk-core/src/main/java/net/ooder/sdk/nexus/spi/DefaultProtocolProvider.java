package net.ooder.sdk.nexus.spi;

import net.ooder.sdk.southbound.protocol.CollaborationProtocol;
import net.ooder.sdk.southbound.protocol.DiscoveryProtocol;
import net.ooder.sdk.southbound.protocol.LoginProtocol;
import net.ooder.sdk.southbound.protocol.RoleProtocol;
import net.ooder.sdk.southbound.protocol.impl.CollaborationProtocolImpl;
import net.ooder.sdk.southbound.protocol.impl.DiscoveryProtocolImpl;
import net.ooder.sdk.southbound.protocol.impl.LoginProtocolImpl;
import net.ooder.sdk.southbound.protocol.impl.RoleProtocolImpl;

/**
 * SDK 内置的默认 ProtocolProvider 实现。
 * 当没有通过 SPI 找到外部实现时，使用此默认实现。
 */
public class DefaultProtocolProvider implements ProtocolProvider {
    
    private final LoginProtocol loginProtocol;
    private final DiscoveryProtocol discoveryProtocol;
    private final CollaborationProtocol collaborationProtocol;
    private final RoleProtocol roleProtocol;
    
    public DefaultProtocolProvider() {
        this.loginProtocol = new LoginProtocolImpl();
        this.discoveryProtocol = new DiscoveryProtocolImpl();
        this.collaborationProtocol = new CollaborationProtocolImpl();
        this.roleProtocol = new RoleProtocolImpl();
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
        return "SDK-Default";
    }
    
    @Override
    public int getPriority() {
        return -1; // 默认实现的优先级最低
    }
}
