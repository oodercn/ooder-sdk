package net.ooder.scene.core.provider;

import net.ooder.scene.core.Result;
import net.ooder.scene.provider.model.network.IPAddress;
import net.ooder.scene.provider.model.network.IPBlacklist;
import net.ooder.scene.provider.model.network.NetworkSetting;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class NetworkConfigProviderTest {

    private NetworkConfigProviderImpl networkConfigProvider;

    @BeforeEach
    public void setup() {
        networkConfigProvider = new NetworkConfigProviderImpl();
        networkConfigProvider.initialize(null);
        networkConfigProvider.start();
    }

    @AfterEach
    public void teardown() {
        networkConfigProvider.stop();
    }

    @Test
    public void testProviderInitialization() {
        assertTrue(networkConfigProvider.isInitialized());
        assertTrue(networkConfigProvider.isRunning());
        assertEquals("network-config-provider", networkConfigProvider.getProviderName());
        assertEquals("1.0.0", networkConfigProvider.getVersion());
        assertEquals(100, networkConfigProvider.getPriority());
    }

    @Test
    public void testGetNetworkSettingBasic() {
        Result<NetworkSetting> result = networkConfigProvider.getNetworkSetting("basic");
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("basic", result.getData().getSettingType());
        assertNotNull(result.getData().getConfig());
    }

    @Test
    public void testGetNetworkSettingDns() {
        Result<NetworkSetting> result = networkConfigProvider.getNetworkSetting("dns");
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("dns", result.getData().getSettingType());
    }

    @Test
    public void testGetNetworkSettingDhcp() {
        Result<NetworkSetting> result = networkConfigProvider.getNetworkSetting("dhcp");
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("dhcp", result.getData().getSettingType());
    }

    @Test
    public void testGetNetworkSettingNotFound() {
        Result<NetworkSetting> result = networkConfigProvider.getNetworkSetting("non-existent");
        
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }

    @Test
    public void testGetNetworkSettingWithNullType() {
        Result<NetworkSetting> result = networkConfigProvider.getNetworkSetting(null);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testGetAllNetworkSettings() {
        Result<List<NetworkSetting>> result = networkConfigProvider.getAllNetworkSettings();
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertTrue(result.getData().size() >= 3);
    }

    @Test
    public void testUpdateNetworkSetting() {
        Map<String, Object> newConfig = new HashMap<>();
        newConfig.put("hostname", "new-hostname");
        newConfig.put("gateway", "192.168.2.1");
        
        Result<NetworkSetting> result = networkConfigProvider.updateNetworkSetting("basic", newConfig);
        
        assertTrue(result.isSuccess());
        assertEquals("new-hostname", result.getData().getConfig().get("hostname"));
        assertEquals("192.168.2.1", result.getData().getConfig().get("gateway"));
    }

    @Test
    public void testUpdateNetworkSettingCreateNew() {
        Map<String, Object> newConfig = new HashMap<>();
        newConfig.put("custom-key", "custom-value");
        
        Result<NetworkSetting> result = networkConfigProvider.updateNetworkSetting("custom", newConfig);
        
        assertTrue(result.isSuccess());
        assertEquals("custom", result.getData().getSettingType());
        assertEquals("custom-value", result.getData().getConfig().get("custom-key"));
    }

    @Test
    public void testUpdateNetworkSettingWithNullType() {
        Map<String, Object> newConfig = new HashMap<>();
        
        Result<NetworkSetting> result = networkConfigProvider.updateNetworkSetting(null, newConfig);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testUpdateNetworkSettingWithNullData() {
        Result<NetworkSetting> result = networkConfigProvider.updateNetworkSetting("basic", null);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testGetIPAddressesEmpty() {
        Result<List<IPAddress>> result = networkConfigProvider.getIPAddresses(null, null);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    public void testAddStaticIPAddress() {
        Map<String, Object> ipData = new HashMap<>();
        ipData.put("address", "192.168.1.50");
        ipData.put("macAddress", "00:11:22:33:44:55");
        ipData.put("hostname", "test-host");
        ipData.put("interfaceName", "eth0");
        
        Result<IPAddress> result = networkConfigProvider.addStaticIPAddress(ipData);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        IPAddress ip = result.getData();
        assertNotNull(ip.getId());
        assertEquals("192.168.1.50", ip.getAddress());
        assertEquals("static", ip.getType());
        assertEquals("active", ip.getStatus());
        assertEquals("00:11:22:33:44:55", ip.getMacAddress());
        assertEquals("test-host", ip.getHostname());
        assertEquals("eth0", ip.getInterfaceName());
    }

    @Test
    public void testAddStaticIPAddressWithNullData() {
        Result<IPAddress> result = networkConfigProvider.addStaticIPAddress(null);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testAddStaticIPAddressWithoutAddress() {
        Map<String, Object> ipData = new HashMap<>();
        ipData.put("macAddress", "00:11:22:33:44:55");
        
        Result<IPAddress> result = networkConfigProvider.addStaticIPAddress(ipData);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testGetIPAddressesAfterAdd() {
        Map<String, Object> ipData1 = new HashMap<>();
        ipData1.put("address", "192.168.1.51");
        networkConfigProvider.addStaticIPAddress(ipData1);
        
        Map<String, Object> ipData2 = new HashMap<>();
        ipData2.put("address", "192.168.1.52");
        networkConfigProvider.addStaticIPAddress(ipData2);
        
        Result<List<IPAddress>> result = networkConfigProvider.getIPAddresses(null, null);
        
        assertTrue(result.isSuccess());
        assertEquals(2, result.getData().size());
    }

    @Test
    public void testGetIPAddressesByType() {
        Map<String, Object> ipData = new HashMap<>();
        ipData.put("address", "192.168.1.53");
        networkConfigProvider.addStaticIPAddress(ipData);
        
        Result<List<IPAddress>> result = networkConfigProvider.getIPAddresses("static", null);
        
        assertTrue(result.isSuccess());
        for (IPAddress ip : result.getData()) {
            assertEquals("static", ip.getType());
        }
    }

    @Test
    public void testGetIPAddressesByStatus() {
        Map<String, Object> ipData = new HashMap<>();
        ipData.put("address", "192.168.1.54");
        networkConfigProvider.addStaticIPAddress(ipData);
        
        Result<List<IPAddress>> result = networkConfigProvider.getIPAddresses(null, "active");
        
        assertTrue(result.isSuccess());
        for (IPAddress ip : result.getData()) {
            assertEquals("active", ip.getStatus());
        }
    }

    @Test
    public void testDeleteIPAddress() {
        Map<String, Object> ipData = new HashMap<>();
        ipData.put("address", "192.168.1.55");
        
        Result<IPAddress> addResult = networkConfigProvider.addStaticIPAddress(ipData);
        String ipId = addResult.getData().getId();
        
        Result<IPAddress> deleteResult = networkConfigProvider.deleteIPAddress(ipId);
        
        assertTrue(deleteResult.isSuccess());
        assertEquals("192.168.1.55", deleteResult.getData().getAddress());
        
        Result<List<IPAddress>> listResult = networkConfigProvider.getIPAddresses(null, null);
        for (IPAddress ip : listResult.getData()) {
            assertNotEquals(ipId, ip.getId());
        }
    }

    @Test
    public void testDeleteIPAddressNotFound() {
        Result<IPAddress> result = networkConfigProvider.deleteIPAddress("non-existent-id");
        
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }

    @Test
    public void testDeleteIPAddressWithNullId() {
        Result<IPAddress> result = networkConfigProvider.deleteIPAddress(null);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testGetIPBlacklistEmpty() {
        Result<List<IPBlacklist>> result = networkConfigProvider.getIPBlacklist();
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    public void testAddIPToBlacklist() {
        Map<String, Object> blacklistData = new HashMap<>();
        blacklistData.put("address", "10.0.0.1");
        blacklistData.put("reason", "Malicious activity");
        blacklistData.put("addedBy", "admin");
        blacklistData.put("permanent", true);
        
        Result<IPBlacklist> result = networkConfigProvider.addIPToBlacklist(blacklistData);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        IPBlacklist entry = result.getData();
        assertNotNull(entry.getId());
        assertEquals("10.0.0.1", entry.getAddress());
        assertEquals("Malicious activity", entry.getReason());
        assertEquals("admin", entry.getAddedBy());
        assertTrue(entry.isPermanent());
        assertTrue(entry.getCreatedAt() > 0);
    }

    @Test
    public void testAddIPToBlacklistWithNullData() {
        Result<IPBlacklist> result = networkConfigProvider.addIPToBlacklist(null);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testAddIPToBlacklistWithoutAddress() {
        Map<String, Object> blacklistData = new HashMap<>();
        blacklistData.put("reason", "Test");
        
        Result<IPBlacklist> result = networkConfigProvider.addIPToBlacklist(blacklistData);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testAddIPToBlacklistWithExpiry() {
        Map<String, Object> blacklistData = new HashMap<>();
        blacklistData.put("address", "10.0.0.2");
        blacklistData.put("reason", "Temporary block");
        blacklistData.put("permanent", false);
        blacklistData.put("expiresAt", System.currentTimeMillis() + 86400000);
        
        Result<IPBlacklist> result = networkConfigProvider.addIPToBlacklist(blacklistData);
        
        assertTrue(result.isSuccess());
        assertFalse(result.getData().isPermanent());
        assertTrue(result.getData().getExpiresAt() > 0);
    }

    @Test
    public void testGetIPBlacklistAfterAdd() {
        Map<String, Object> data1 = new HashMap<>();
        data1.put("address", "10.0.0.3");
        networkConfigProvider.addIPToBlacklist(data1);
        
        Map<String, Object> data2 = new HashMap<>();
        data2.put("address", "10.0.0.4");
        networkConfigProvider.addIPToBlacklist(data2);
        
        Result<List<IPBlacklist>> result = networkConfigProvider.getIPBlacklist();
        
        assertTrue(result.isSuccess());
        assertEquals(2, result.getData().size());
    }

    @Test
    public void testRemoveIPFromBlacklist() {
        Map<String, Object> blacklistData = new HashMap<>();
        blacklistData.put("address", "10.0.0.5");
        
        Result<IPBlacklist> addResult = networkConfigProvider.addIPToBlacklist(blacklistData);
        String entryId = addResult.getData().getId();
        
        Result<IPBlacklist> removeResult = networkConfigProvider.removeIPFromBlacklist(entryId);
        
        assertTrue(removeResult.isSuccess());
        assertEquals("10.0.0.5", removeResult.getData().getAddress());
        
        Result<List<IPBlacklist>> listResult = networkConfigProvider.getIPBlacklist();
        for (IPBlacklist entry : listResult.getData()) {
            assertNotEquals(entryId, entry.getId());
        }
    }

    @Test
    public void testRemoveIPFromBlacklistNotFound() {
        Result<IPBlacklist> result = networkConfigProvider.removeIPFromBlacklist("non-existent-id");
        
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }

    @Test
    public void testRemoveIPFromBlacklistWithNullId() {
        Result<IPBlacklist> result = networkConfigProvider.removeIPFromBlacklist(null);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testProviderLifecycle() {
        NetworkConfigProviderImpl provider = new NetworkConfigProviderImpl();
        
        assertFalse(provider.isInitialized());
        assertFalse(provider.isRunning());
        
        provider.initialize(null);
        assertTrue(provider.isInitialized());
        assertFalse(provider.isRunning());
        
        provider.start();
        assertTrue(provider.isRunning());
        
        provider.stop();
        assertFalse(provider.isRunning());
    }

    @Test
    public void testStartWithoutInitialize() {
        NetworkConfigProviderImpl provider = new NetworkConfigProviderImpl();
        
        assertThrows(IllegalStateException.class, () -> provider.start());
    }
}
