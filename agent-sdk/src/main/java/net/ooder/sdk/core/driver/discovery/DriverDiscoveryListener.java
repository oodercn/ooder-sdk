package net.ooder.sdk.core.driver.discovery;

import net.ooder.sdk.core.driver.Driver;

/**
 * Driver 发现监听器接口
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface DriverDiscoveryListener {

    /**
     * 当发现新的 Driver 时触发
     *
     * @param driverClass Driver 类
     */
    void onDriverDiscovered(Class<? extends Driver> driverClass);

    /**
     * 当 Driver 被移除时触发
     *
     * @param driverClass Driver 类
     */
    void onDriverRemoved(Class<? extends Driver> driverClass);
}
