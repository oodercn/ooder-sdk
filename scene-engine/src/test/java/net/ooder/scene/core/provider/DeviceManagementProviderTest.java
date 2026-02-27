package net.ooder.scene.core.provider;

import net.ooder.scene.core.Result;
import net.ooder.scene.provider.model.network.CommandResult;
import net.ooder.scene.provider.model.network.ConnectionStatus;
import net.ooder.scene.provider.model.network.SystemStatus;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DeviceManagementProviderTest {

    private DeviceManagementProviderImpl deviceProvider;

    @BeforeEach
    public void setup() {
        deviceProvider = new DeviceManagementProviderImpl();
        deviceProvider.initialize(null);
        deviceProvider.start();
    }

    @AfterEach
    public void teardown() {
        deviceProvider.stop();
    }

    @Test
    public void testProviderInitialization() {
        assertTrue(deviceProvider.isInitialized());
        assertTrue(deviceProvider.isRunning());
        assertEquals("device-management-provider", deviceProvider.getProviderName());
        assertEquals("1.0.0", deviceProvider.getVersion());
        assertEquals(100, deviceProvider.getPriority());
    }

    @Test
    public void testConnect() {
        Map<String, Object> connectionData = new HashMap<>();
        connectionData.put("endpoint", "ssh://192.168.1.100:22");
        connectionData.put("connectionType", "ssh");
        
        Result<Boolean> result = deviceProvider.connect(connectionData);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getData());
    }

    @Test
    public void testConnectWithNullData() {
        Result<Boolean> result = deviceProvider.connect(null);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testConnectWithDefaultValues() {
        Map<String, Object> connectionData = new HashMap<>();
        
        Result<Boolean> result = deviceProvider.connect(connectionData);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getData());
    }

    @Test
    public void testDisconnect() {
        Map<String, Object> connectionData = new HashMap<>();
        connectionData.put("endpoint", "ssh://192.168.1.100:22");
        deviceProvider.connect(connectionData);
        
        Result<Boolean> result = deviceProvider.disconnect();
        
        assertTrue(result.isSuccess());
        assertTrue(result.getData());
    }

    @Test
    public void testDisconnectWhenNotConnected() {
        Result<Boolean> result = deviceProvider.disconnect();
        
        assertTrue(result.isSuccess());
        assertFalse(result.getData());
    }

    @Test
    public void testGetConnectionStatus() {
        Map<String, Object> connectionData = new HashMap<>();
        connectionData.put("endpoint", "ssh://192.168.1.100:22");
        connectionData.put("connectionType", "ssh");
        deviceProvider.connect(connectionData);
        
        Result<ConnectionStatus> result = deviceProvider.getConnectionStatus();
        
        assertTrue(result.isSuccess());
        assertTrue(result.getData().isConnected());
        assertEquals("ssh://192.168.1.100:22", result.getData().getEndpoint());
        assertEquals("ssh", result.getData().getConnectionType());
    }

    @Test
    public void testGetConnectionStatusWhenNotConnected() {
        Result<ConnectionStatus> result = deviceProvider.getConnectionStatus();
        
        assertTrue(result.isSuccess());
        assertFalse(result.getData().isConnected());
    }

    @Test
    public void testExecuteCommand() {
        Map<String, Object> connectionData = new HashMap<>();
        deviceProvider.connect(connectionData);
        
        Result<CommandResult> result = deviceProvider.executeCommand("echo test");
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertTrue(result.getData().isSuccess());
        assertTrue(result.getData().getOutput().contains("test"));
    }

    @Test
    public void testExecuteCommandWithNullCommand() {
        Map<String, Object> connectionData = new HashMap<>();
        deviceProvider.connect(connectionData);
        
        Result<CommandResult> result = deviceProvider.executeCommand((String) null);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testExecuteCommandWhenNotConnected() {
        Result<CommandResult> result = deviceProvider.executeCommand("echo test");
        
        assertTrue(result.isSuccess());
        assertFalse(result.getData().isSuccess());
        assertEquals("Not connected to device", result.getData().getError());
    }

    @Test
    public void testExecuteCommandWithParams() {
        Map<String, Object> connectionData = new HashMap<>();
        deviceProvider.connect(connectionData);
        
        Map<String, Object> params = new HashMap<>();
        params.put("timeout", 30000);
        
        Result<CommandResult> result = deviceProvider.executeCommand("echo test", params);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getData().isSuccess());
    }

    @Test
    public void testGetSystemStatus() {
        Result<SystemStatus> result = deviceProvider.getSystemStatus();
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        SystemStatus status = result.getData();
        assertTrue(status.getCpuUsage() >= 0);
        assertTrue(status.getMemoryUsed() >= 0);
        assertTrue(status.getMemoryTotal() > 0);
        assertTrue(status.getUptime() >= 0);
        assertNotNull(status.getHostname());
        assertNotNull(status.getOsVersion());
    }

    @Test
    public void testRestart() {
        Map<String, Object> connectionData = new HashMap<>();
        deviceProvider.connect(connectionData);
        
        Result<Boolean> result = deviceProvider.restart();
        
        assertTrue(result.isSuccess());
        assertTrue(result.getData());
    }

    @Test
    public void testRestartWhenNotConnected() {
        Result<Boolean> result = deviceProvider.restart();
        
        assertFalse(result.isSuccess());
    }

    @Test
    public void testShutdown() {
        Map<String, Object> connectionData = new HashMap<>();
        deviceProvider.connect(connectionData);
        
        Result<Boolean> result = deviceProvider.shutdown();
        
        assertTrue(result.isSuccess());
        assertTrue(result.getData());
        
        Result<ConnectionStatus> statusResult = deviceProvider.getConnectionStatus();
        assertFalse(statusResult.getData().isConnected());
    }

    @Test
    public void testShutdownWhenNotConnected() {
        Result<Boolean> result = deviceProvider.shutdown();
        
        assertFalse(result.isSuccess());
    }

    @Test
    public void testProviderLifecycle() {
        DeviceManagementProviderImpl provider = new DeviceManagementProviderImpl();
        
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
        DeviceManagementProviderImpl provider = new DeviceManagementProviderImpl();
        
        assertThrows(IllegalStateException.class, () -> provider.start());
    }
}
