package net.ooder.sdk.core.driver.loader.impl;

import net.ooder.sdk.core.driver.Driver;
import net.ooder.sdk.core.driver.loader.InterfaceDriverLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 接口 Driver 加载器实现
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class InterfaceDriverLoaderImpl implements InterfaceDriverLoader {

    private final Map<String, Driver> interfaceDrivers = new ConcurrentHashMap<>();

    @Override
    public Driver loadInterfaceDriver(String interfaceId) {
        return interfaceDrivers.get(interfaceId);
    }

    @Override
    public void registerInterfaceDriver(String interfaceId, Driver driver) {
        interfaceDrivers.put(interfaceId, driver);
    }

    @Override
    public List<Driver> getAllInterfaceDrivers() {
        return new ArrayList<>(interfaceDrivers.values());
    }

    @Override
    public boolean hasInterfaceDriver(String interfaceId) {
        return interfaceDrivers.containsKey(interfaceId);
    }
}
