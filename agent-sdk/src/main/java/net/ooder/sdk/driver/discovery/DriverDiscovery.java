package net.ooder.sdk.driver.discovery;

import net.ooder.sdk.core.driver.Driver;

import java.util.List;

/**
 * Driver 发现服务接口
 * 用于发现和注册 Driver
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface DriverDiscovery {

    /**
     * 发现指定类型的 Driver
     *
     * @param driverType Driver 类型
     * @return Driver 类列表
     */
    List<Class<? extends Driver>> discoverDrivers(String driverType);

    /**
     * 发现所有 Driver
     *
     * @return Driver 类列表
     */
    List<Class<? extends Driver>> discoverAllDrivers();

    /**
     * 注册 Driver
     *
     * @param driverClass Driver 类
     */
    void registerDriver(Class<? extends Driver> driverClass);

    /**
     * 扫描包路径发现 Driver
     *
     * @param packagePath 包路径
     * @return 发现的 Driver 数量
     */
    int scanPackage(String packagePath);
}
