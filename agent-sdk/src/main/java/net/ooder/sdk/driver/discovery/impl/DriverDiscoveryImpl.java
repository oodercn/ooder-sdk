package net.ooder.sdk.driver.discovery.impl;

import net.ooder.sdk.core.driver.Driver;
import net.ooder.sdk.driver.discovery.DriverDiscovery;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Driver 发现服务实现
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class DriverDiscoveryImpl implements DriverDiscovery {

    private final List<Class<? extends Driver>> registeredDrivers = new CopyOnWriteArrayList<>();

    @Override
    public List<Class<? extends Driver>> discoverDrivers(String driverType) {
        List<Class<? extends Driver>> result = new ArrayList<>();
        for (Class<? extends Driver> driverClass : registeredDrivers) {
            if (isDriverType(driverClass, driverType)) {
                result.add(driverClass);
            }
        }
        return result;
    }

    @Override
    public List<Class<? extends Driver>> discoverAllDrivers() {
        return new ArrayList<>(registeredDrivers);
    }

    @Override
    public void registerDriver(Class<? extends Driver> driverClass) {
        if (!registeredDrivers.contains(driverClass)) {
            registeredDrivers.add(driverClass);
        }
    }

    @Override
    public int scanPackage(String packagePath) {
        // 简化实现：实际应该使用类扫描工具
        return 0;
    }

    private boolean isDriverType(Class<? extends Driver> driverClass, String driverType) {
        // 简化实现：根据类名或注解判断
        return driverClass.getSimpleName().toLowerCase().contains(driverType.toLowerCase());
    }
}
