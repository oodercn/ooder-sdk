package net.ooder.sdk.nexus.spi;

import net.ooder.sdk.southbound.protocol.CollaborationProtocol;
import net.ooder.sdk.southbound.protocol.DiscoveryProtocol;
import net.ooder.sdk.southbound.protocol.LoginProtocol;
import net.ooder.sdk.southbound.protocol.RoleProtocol;

/**
 * SPI 接口，用于提供协议实现。
 * Engine 层通过实现此接口，向 SDK 提供具体的协议实现。
 */
public interface ProtocolProvider {
    
    /**
     * 获取登录协议实现
     */
    LoginProtocol getLoginProtocol();
    
    /**
     * 获取发现协议实现
     */
    DiscoveryProtocol getDiscoveryProtocol();
    
    /**
     * 获取协作协议实现
     */
    CollaborationProtocol getCollaborationProtocol();
    
    /**
     * 获取角色协议实现
     */
    RoleProtocol getRoleProtocol();
    
    /**
     * 获取提供者名称
     */
    default String getProviderName() {
        return getClass().getSimpleName();
    }
    
    /**
     * 获取提供者优先级，数值越大优先级越高
     */
    default int getPriority() {
        return 0;
    }
}
