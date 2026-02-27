package net.ooder.scene.core.provider;

import net.ooder.scene.core.PageResult;
import net.ooder.scene.core.Result;
import net.ooder.scene.provider.*;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 核心Provider测试
 */
public class CoreProviderTest {

    private SystemProviderImpl systemProvider;
    private LogProviderImpl logProvider;
    private ConfigProviderImpl configProvider;

    @BeforeEach
    public void setup() {
        systemProvider = new SystemProviderImpl();
        logProvider = new LogProviderImpl();
        configProvider = new ConfigProviderImpl();
        
        systemProvider.initialize(null);
        logProvider.initialize(null);
        configProvider.initialize(null);
        
        systemProvider.start();
        logProvider.start();
        configProvider.start();
    }

    @AfterEach
    public void teardown() {
        systemProvider.stop();
        logProvider.stop();
        configProvider.stop();
    }

    // ==================== SystemProvider Tests ====================

    @Test
    public void testSystemProviderInitialization() {
        assertTrue(systemProvider.isInitialized());
        assertTrue(systemProvider.isRunning());
        assertEquals("system-provider", systemProvider.getProviderName());
        assertEquals("1.0.0", systemProvider.getVersion());
        assertEquals(1000, systemProvider.getPriority());
    }

    @Test
    public void testGetSystemInfo() {
        Result<SystemInfo> result = systemProvider.getSystemInfo();
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        SystemInfo info = result.getData();
        assertNotNull(info.getJavaVersion());
        assertNotNull(info.getOsName());
        assertNotNull(info.getOsArch());
        assertTrue(info.getAvailableProcessors() > 0);
    }

    @Test
    public void testGetSystemStatus() {
        Result<SystemStatus> result = systemProvider.getSystemStatus();
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        SystemStatus status = result.getData();
        assertEquals("running", status.getStatus());
        assertTrue(status.getCpuUsage() >= 0);
        assertTrue(status.getMemoryUsage() >= 0);
        assertTrue(status.getThreadCount() > 0);
    }

    @Test
    public void testGetSystemLoad() {
        Result<SystemLoad> result = systemProvider.getSystemLoad();
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        SystemLoad load = result.getData();
        assertTrue(load.getCpuLoad() >= 0);
        assertTrue(load.getMemoryUsage() >= 0);
        assertTrue(load.getThreadCount() > 0);
    }

    @Test
    public void testListServices() {
        Result<PageResult<ServiceInfo>> result = systemProvider.listServices(1, 10);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        PageResult<ServiceInfo> pageResult = result.getData();
        assertTrue(pageResult.getTotal() > 0);
        assertFalse(pageResult.getItems().isEmpty());
    }

    @Test
    public void testGetService() {
        Result<ServiceInfo> result = systemProvider.getService("scene-engine");
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        ServiceInfo service = result.getData();
        assertEquals("scene-engine", service.getServiceName());
        assertEquals("running", service.getStatus());
    }

    @Test
    public void testGetServiceNotFound() {
        Result<ServiceInfo> result = systemProvider.getService("non-existent-service");
        
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }

    @Test
    public void testGetResourceUsage() {
        Result<List<ResourceUsage>> result = systemProvider.getResourceUsage();
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertFalse(result.getData().isEmpty());
    }

    @Test
    public void testGetEnvironmentVariables() {
        Result<Map<String, String>> result = systemProvider.getEnvironmentVariables();
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertFalse(result.getData().isEmpty());
    }

    @Test
    public void testGetSystemProperties() {
        Result<Map<String, String>> result = systemProvider.getSystemProperties();
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertFalse(result.getData().isEmpty());
        assertTrue(result.getData().containsKey("java.version"));
    }

    // ==================== LogProvider Tests ====================

    @Test
    public void testLogProviderInitialization() {
        assertTrue(logProvider.isInitialized());
        assertTrue(logProvider.isRunning());
        assertEquals("log-provider", logProvider.getProviderName());
        assertEquals("1.0.0", logProvider.getVersion());
        assertEquals(1000, logProvider.getPriority());
    }

    @Test
    public void testWriteLog() {
        Result<Boolean> result = logProvider.writeLog("INFO", "Test log message", "test-source");
        
        assertTrue(result.isSuccess());
        assertTrue(result.getData());
    }

    @Test
    public void testWriteLogWithDetails() {
        Map<String, Object> details = new HashMap<>();
        details.put("key1", "value1");
        details.put("key2", 123);
        
        Result<Boolean> result = logProvider.writeLog("ERROR", "Test error message", "test-source", details);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getData());
    }

    @Test
    public void testQueryLogs() {
        logProvider.writeLog("INFO", "Test message 1", "source1");
        logProvider.writeLog("ERROR", "Test message 2", "source2");
        logProvider.writeLog("DEBUG", "Test message 3", "source1");
        
        LogQuery query = new LogQuery();
        query.setPage(1);
        query.setSize(10);
        
        Result<PageResult<LogEntry>> result = logProvider.queryLogs(query);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertTrue(result.getData().getTotal() >= 3);
    }

    @Test
    public void testQueryLogsByLevel() {
        logProvider.writeLog("INFO", "Info message", "test");
        logProvider.writeLog("ERROR", "Error message", "test");
        
        LogQuery query = new LogQuery();
        query.setLevel("ERROR");
        query.setPage(1);
        query.setSize(10);
        
        Result<PageResult<LogEntry>> result = logProvider.queryLogs(query);
        
        assertTrue(result.isSuccess());
        for (LogEntry entry : result.getData().getItems()) {
            assertEquals("ERROR", entry.getLevel());
        }
    }

    @Test
    public void testQueryLogsBySource() {
        logProvider.writeLog("INFO", "Message 1", "source-a");
        logProvider.writeLog("INFO", "Message 2", "source-b");
        
        LogQuery query = new LogQuery();
        query.setSource("source-a");
        query.setPage(1);
        query.setSize(10);
        
        Result<PageResult<LogEntry>> result = logProvider.queryLogs(query);
        
        assertTrue(result.isSuccess());
        for (LogEntry entry : result.getData().getItems()) {
            assertEquals("source-a", entry.getSource());
        }
    }

    @Test
    public void testQueryLogsByKeyword() {
        logProvider.writeLog("INFO", "This is a test message", "test");
        logProvider.writeLog("INFO", "Another message", "test");
        
        LogQuery query = new LogQuery();
        query.setKeyword("test");
        query.setPage(1);
        query.setSize(10);
        
        Result<PageResult<LogEntry>> result = logProvider.queryLogs(query);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getData().getTotal() > 0);
    }

    @Test
    public void testGetLogStatistics() {
        logProvider.writeLog("ERROR", "Error 1", "test");
        logProvider.writeLog("WARN", "Warning 1", "test");
        logProvider.writeLog("INFO", "Info 1", "test");
        
        long now = System.currentTimeMillis();
        Result<LogStatistics> result = logProvider.getStatistics(now - 60000, now + 1000);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertTrue(result.getData().getTotalCount() >= 3);
    }

    @Test
    public void testExportLogs() {
        logProvider.writeLog("INFO", "Test message", "test");
        
        LogQuery query = new LogQuery();
        query.setPage(1);
        query.setSize(10);
        
        Result<LogExportResult> result = logProvider.exportLogs(query, "json");
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("json", result.getData().getFormat());
    }

    // ==================== ConfigProvider Tests ====================

    @Test
    public void testConfigProviderInitialization() {
        assertTrue(configProvider.isInitialized());
        assertTrue(configProvider.isRunning());
        assertEquals("config-provider", configProvider.getProviderName());
        assertEquals("1.0.0", configProvider.getVersion());
        assertEquals(1000, configProvider.getPriority());
    }

    @Test
    public void testSetAndGetConfig() {
        Result<Boolean> setResult = configProvider.setConfig("test.key", "test-value");
        assertTrue(setResult.isSuccess());
        
        Result<String> getResult = configProvider.getConfig("test.key");
        assertTrue(getResult.isSuccess());
        assertEquals("test-value", getResult.getData());
    }

    @Test
    public void testGetConfigWithDefault() {
        Result<String> result = configProvider.getConfig("non.existent.key", "default-value");
        
        assertTrue(result.isSuccess());
        assertEquals("default-value", result.getData());
    }

    @Test
    public void testGetConfigAsInteger() {
        configProvider.setConfig("test.int", "123");
        
        Result<Integer> result = configProvider.getConfig("test.int", Integer.class);
        
        assertTrue(result.isSuccess());
        assertEquals(123, result.getData().intValue());
    }

    @Test
    public void testGetConfigAsBoolean() {
        configProvider.setConfig("test.bool", "true");
        
        Result<Boolean> result = configProvider.getConfig("test.bool", Boolean.class);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getData());
    }

    @Test
    public void testGetConfigAsLong() {
        configProvider.setConfig("test.long", "12345678901234");
        
        Result<Long> result = configProvider.getConfig("test.long", Long.class);
        
        assertTrue(result.isSuccess());
        assertEquals(12345678901234L, result.getData().longValue());
    }

    @Test
    public void testSetConfigsBatch() {
        Map<String, String> configs = new HashMap<>();
        configs.put("batch.key1", "value1");
        configs.put("batch.key2", "value2");
        configs.put("batch.key3", "value3");
        
        Result<Boolean> result = configProvider.setConfigs(configs);
        
        assertTrue(result.isSuccess());
        
        assertEquals("value1", configProvider.getConfig("batch.key1").getData());
        assertEquals("value2", configProvider.getConfig("batch.key2").getData());
        assertEquals("value3", configProvider.getConfig("batch.key3").getData());
    }

    @Test
    public void testDeleteConfig() {
        configProvider.setConfig("to.delete", "value");
        
        Result<Boolean> result = configProvider.deleteConfig("to.delete");
        
        assertTrue(result.isSuccess());
        assertNull(configProvider.getConfig("to.delete").getData());
    }

    @Test
    public void testHasConfig() {
        configProvider.setConfig("existing.key", "value");
        
        Result<Boolean> hasResult = configProvider.hasConfig("existing.key");
        assertTrue(hasResult.isSuccess());
        assertTrue(hasResult.getData());
        
        Result<Boolean> notHasResult = configProvider.hasConfig("non.existing.key");
        assertTrue(notHasResult.isSuccess());
        assertFalse(notHasResult.getData());
    }

    @Test
    public void testGetAllConfigs() {
        configProvider.setConfig("all.test1", "value1");
        configProvider.setConfig("all.test2", "value2");
        
        Result<Map<String, String>> result = configProvider.getAllConfigs();
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertTrue(result.getData().size() > 0);
    }

    @Test
    public void testGetConfigsByPrefix() {
        configProvider.setConfig("prefix.key1", "value1");
        configProvider.setConfig("prefix.key2", "value2");
        configProvider.setConfig("other.key", "value3");
        
        Result<Map<String, String>> result = configProvider.getConfigsByPrefix("prefix.");
        
        assertTrue(result.isSuccess());
        assertTrue(result.getData().containsKey("prefix.key1"));
        assertTrue(result.getData().containsKey("prefix.key2"));
        assertFalse(result.getData().containsKey("other.key"));
    }

    @Test
    public void testGetConfigHistory() {
        configProvider.setConfig("history.key", "value1");
        configProvider.setConfig("history.key", "value2");
        configProvider.setConfig("history.key", "value3");
        
        Result<PageResult<ConfigHistory>> result = configProvider.getConfigHistory("history.key", 1, 10);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getData().getTotal() >= 2);
    }

    @Test
    public void testExportConfig() {
        configProvider.setConfig("export.key", "export-value");
        
        Result<ConfigExportResult> result = configProvider.exportConfig("json");
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("json", result.getData().getFormat());
        assertTrue(result.getData().getConfigCount() > 0);
    }

    @Test
    public void testExportConfigAsProperties() {
        Result<ConfigExportResult> result = configProvider.exportConfig("properties");
        
        assertTrue(result.isSuccess());
        assertEquals("properties", result.getData().getFormat());
    }

    @Test
    public void testExportConfigAsYaml() {
        Result<ConfigExportResult> result = configProvider.exportConfig("yaml");
        
        assertTrue(result.isSuccess());
        assertEquals("yaml", result.getData().getFormat());
    }

    @Test
    public void testImportConfig() {
        String jsonContent = "{\"imported.key1\": \"value1\", \"imported.key2\": \"value2\"}";
        
        Result<Integer> result = configProvider.importConfig(jsonContent, "json");
        
        assertTrue(result.isSuccess());
        assertEquals(2, result.getData().intValue());
        assertEquals("value1", configProvider.getConfig("imported.key1").getData());
        assertEquals("value2", configProvider.getConfig("imported.key2").getData());
    }

    @Test
    public void testGetConfigGroups() {
        configProvider.setConfig("group1.key1", "value1");
        configProvider.setConfig("group1.key2", "value2");
        configProvider.setConfig("group2.key1", "value3");
        
        Result<List<ConfigGroup>> result = configProvider.getConfigGroups();
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertTrue(result.getData().size() > 0);
    }

    @Test
    public void testGetConfigGroup() {
        configProvider.setConfig("testgroup.key1", "value1");
        configProvider.setConfig("testgroup.key2", "value2");
        
        Result<ConfigGroup> result = configProvider.getConfigGroup("testgroup");
        
        assertTrue(result.isSuccess());
        assertEquals("testgroup", result.getData().getGroupName());
        assertTrue(result.getData().getConfigCount() >= 2);
    }
}
