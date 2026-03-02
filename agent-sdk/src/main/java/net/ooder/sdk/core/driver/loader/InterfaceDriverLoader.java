package net.ooder.sdk.core.driver.loader;

import net.ooder.sdk.core.driver.Driver;

import java.util.List;

/**
 * 接口 Driver 加载器接口
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface InterfaceDriverLoader {

    /**
     * 加载接口 Driver
     *
     * @param interfaceId 接口ID
     * @return Driver 实例
     */
    Driver loadInterfaceDriver(String interfaceId);

    /**
     * 注册接口 Driver
     *
     * @param interfaceId 接口ID
     * @param driver      Driver 实例
     */
    void registerInterfaceDriver(String interfaceId, Driver driver);

    /**
     * 获取所有接口 Drivers
     *
     * @return Driver 列表
     */
    List<Driver> getAllInterfaceDrivers();

    /**
     * 检查接口 Driver 是否存在
     *
     * @param interfaceId 接口ID
     * @return 是否存在
     */
    boolean hasInterfaceDriver(String interfaceId);
}
