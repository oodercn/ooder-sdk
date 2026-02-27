package net.ooder.scene.core.driver;

import net.ooder.sdk.core.InterfaceDefinition;

/**
 * Driver 接口
 * 继承自 agent-sdk 的核心接口，保持兼容性
 * @deprecated 请使用 net.ooder.sdk.core.driver.Driver
 */
@Deprecated
public interface Driver extends net.ooder.sdk.core.driver.Driver {
    
    // 所有方法都从父接口继承
    // 这里保持空实现，仅用于向后兼容
}
