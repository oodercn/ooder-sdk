package net.ooder.scene.core.provider;

import net.ooder.scene.core.Result;
import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.provider.NetworkConfigProvider;
import net.ooder.scene.provider.model.network.IPAddress;
import net.ooder.scene.provider.model.network.IPBlacklist;
import net.ooder.scene.provider.model.network.NetworkSetting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class NetworkConfigProviderImpl implements NetworkConfigProvider {

    private static final String PROVIDER_NAME = "network-config-provider";
    private static final String VERSION = "1.0.0";

    private boolean initialized = false;
    private boolean running = false;
    private SceneEngine engine;

    private final Map<String, NetworkSetting> networkSettings = new ConcurrentHashMap<>();
    private final Map<String, IPAddress> ipAddresses = new ConcurrentHashMap<>();
    private final Map<String, IPBlacklist> ipBlacklist = new ConcurrentHashMap<>();

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public void initialize(SceneEngine engine) {
        this.engine = engine;
        initializeDefaultSettings();
        this.initialized = true;
    }

    @Override
    public void start() {
        if (!initialized) {
            throw new IllegalStateException("Provider not initialized");
        }
        this.running = true;
    }

    @Override
    public void stop() {
        this.running = false;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPriority() {
        return 100;
    }

    private void initializeDefaultSettings() {
        NetworkSetting basicSetting = new NetworkSetting();
        basicSetting.setSettingId(UUID.randomUUID().toString());
        basicSetting.setSettingType("basic");
        basicSetting.setConfig(createBasicConfig());
        basicSetting.setUpdatedAt(System.currentTimeMillis());
        basicSetting.setUpdatedBy("system");
        networkSettings.put("basic", basicSetting);

        NetworkSetting dnsSetting = new NetworkSetting();
        dnsSetting.setSettingId(UUID.randomUUID().toString());
        dnsSetting.setSettingType("dns");
        dnsSetting.setConfig(createDnsConfig());
        dnsSetting.setUpdatedAt(System.currentTimeMillis());
        dnsSetting.setUpdatedBy("system");
        networkSettings.put("dns", dnsSetting);

        NetworkSetting dhcpSetting = new NetworkSetting();
        dhcpSetting.setSettingId(UUID.randomUUID().toString());
        dhcpSetting.setSettingType("dhcp");
        dhcpSetting.setConfig(createDhcpConfig());
        dhcpSetting.setUpdatedAt(System.currentTimeMillis());
        dhcpSetting.setUpdatedBy("system");
        networkSettings.put("dhcp", dhcpSetting);
    }

    private Map<String, Object> createBasicConfig() {
        java.util.HashMap<String, Object> config = new java.util.HashMap<>();
        config.put("hostname", "scene-engine");
        config.put("gateway", "192.168.1.1");
        config.put("subnet", "255.255.255.0");
        return config;
    }

    private Map<String, Object> createDnsConfig() {
        java.util.HashMap<String, Object> config = new java.util.HashMap<>();
        config.put("primary", "8.8.8.8");
        config.put("secondary", "8.8.4.4");
        return config;
    }

    private Map<String, Object> createDhcpConfig() {
        java.util.HashMap<String, Object> config = new java.util.HashMap<>();
        config.put("enabled", true);
        config.put("startIP", "192.168.1.100");
        config.put("endIP", "192.168.1.200");
        config.put("leaseTime", 86400);
        return config;
    }

    @Override
    public Result<NetworkSetting> getNetworkSetting(String settingType) {
        if (settingType == null || settingType.isEmpty()) {
            return Result.badRequest("Setting type is required");
        }

        NetworkSetting setting = networkSettings.get(settingType);
        if (setting == null) {
            return Result.notFound("Network setting not found: " + settingType);
        }

        return Result.success(setting);
    }

    @Override
    public Result<List<NetworkSetting>> getAllNetworkSettings() {
        List<NetworkSetting> settings = new ArrayList<>(networkSettings.values());
        return Result.success(settings);
    }

    @Override
    public Result<NetworkSetting> updateNetworkSetting(String settingType, Map<String, Object> data) {
        if (settingType == null || settingType.isEmpty()) {
            return Result.badRequest("Setting type is required");
        }

        if (data == null) {
            return Result.badRequest("Setting data is required");
        }

        NetworkSetting setting = networkSettings.get(settingType);
        if (setting == null) {
            setting = new NetworkSetting();
            setting.setSettingId(UUID.randomUUID().toString());
            setting.setSettingType(settingType);
        }

        setting.setConfig(data);
        setting.setUpdatedAt(System.currentTimeMillis());
        setting.setUpdatedBy("user");

        networkSettings.put(settingType, setting);

        return Result.success(setting);
    }

    @Override
    public Result<List<IPAddress>> getIPAddresses(String type, String status) {
        List<IPAddress> result = new ArrayList<>(ipAddresses.values());

        if (type != null && !type.isEmpty()) {
            result = result.stream()
                    .filter(ip -> type.equals(ip.getType()))
                    .collect(Collectors.toList());
        }

        if (status != null && !status.isEmpty()) {
            result = result.stream()
                    .filter(ip -> status.equals(ip.getStatus()))
                    .collect(Collectors.toList());
        }

        return Result.success(result);
    }

    @Override
    public Result<IPAddress> addStaticIPAddress(Map<String, Object> ipData) {
        if (ipData == null) {
            return Result.badRequest("IP data is required");
        }

        String address = (String) ipData.get("address");
        if (address == null || address.isEmpty()) {
            return Result.badRequest("IP address is required");
        }

        IPAddress ip = new IPAddress();
        ip.setId(UUID.randomUUID().toString());
        ip.setAddress(address);
        ip.setType("static");
        ip.setStatus("active");

        if (ipData.containsKey("macAddress")) {
            ip.setMacAddress((String) ipData.get("macAddress"));
        }
        if (ipData.containsKey("hostname")) {
            ip.setHostname((String) ipData.get("hostname"));
        }
        if (ipData.containsKey("interfaceName")) {
            ip.setInterfaceName((String) ipData.get("interfaceName"));
        }

        ip.setCreatedAt(System.currentTimeMillis());
        ip.setLastSeenAt(System.currentTimeMillis());

        ipAddresses.put(ip.getId(), ip);

        return Result.success(ip);
    }

    @Override
    public Result<IPAddress> deleteIPAddress(String ipId) {
        if (ipId == null || ipId.isEmpty()) {
            return Result.badRequest("IP ID is required");
        }

        IPAddress removed = ipAddresses.remove(ipId);
        if (removed == null) {
            return Result.notFound("IP address not found: " + ipId);
        }

        return Result.success(removed);
    }

    @Override
    public Result<List<IPBlacklist>> getIPBlacklist() {
        List<IPBlacklist> list = new ArrayList<>(ipBlacklist.values());
        return Result.success(list);
    }

    @Override
    public Result<IPBlacklist> addIPToBlacklist(Map<String, Object> data) {
        if (data == null) {
            return Result.badRequest("Blacklist data is required");
        }

        String address = (String) data.get("address");
        if (address == null || address.isEmpty()) {
            return Result.badRequest("IP address is required");
        }

        IPBlacklist entry = new IPBlacklist();
        entry.setId(UUID.randomUUID().toString());
        entry.setAddress(address);

        if (data.containsKey("reason")) {
            entry.setReason((String) data.get("reason"));
        }
        if (data.containsKey("addedBy")) {
            entry.setAddedBy((String) data.get("addedBy"));
        } else {
            entry.setAddedBy("user");
        }

        entry.setCreatedAt(System.currentTimeMillis());

        if (data.containsKey("permanent")) {
            entry.setPermanent((Boolean) data.get("permanent"));
        } else {
            entry.setPermanent(true);
        }

        if (data.containsKey("expiresAt")) {
            Object expiresAt = data.get("expiresAt");
            if (expiresAt instanceof Number) {
                entry.setExpiresAt(((Number) expiresAt).longValue());
            }
        }

        ipBlacklist.put(entry.getId(), entry);

        return Result.success(entry);
    }

    @Override
    public Result<IPBlacklist> removeIPFromBlacklist(String id) {
        if (id == null || id.isEmpty()) {
            return Result.badRequest("Blacklist entry ID is required");
        }

        IPBlacklist removed = ipBlacklist.remove(id);
        if (removed == null) {
            return Result.notFound("Blacklist entry not found: " + id);
        }

        return Result.success(removed);
    }
}
