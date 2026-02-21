package net.ooder.org.datasource;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class OrgDataSourceSceneConfigTest {
    
    private OrgDataSourceSceneConfig config;
    
    @Before
    public void setUp() {
        config = new OrgDataSourceSceneConfig();
    }
    
    @Test
    public void testDefaultValues() {
        assertNull(config.getSceneId());
        assertEquals(DataSourceType.JSON, config.getDataSourceType());
        assertNotNull(config.getJsonConfig());
        assertNotNull(config.getDatabaseConfig());
        assertNotNull(config.getDingtalkConfig());
        assertNotNull(config.getFeishuConfig());
        assertNotNull(config.getWecomConfig());
        assertNotNull(config.getSyncConfig());
        assertNotNull(config.getCapabilities());
    }
    
    @Test
    public void testSetSceneId() {
        config.setSceneId("test-scene");
        assertEquals("test-scene", config.getSceneId());
    }
    
    @Test
    public void testSetDataSourceType() {
        config.setDataSourceType(DataSourceType.DINGTALK);
        assertEquals(DataSourceType.DINGTALK, config.getDataSourceType());
    }
    
    @Test
    public void testSetDataSourceTypeByCode() {
        config.setDataSourceType("feishu");
        assertEquals(DataSourceType.FEISHU, config.getDataSourceType());
    }
    
    @Test
    public void testGetActiveConfigJson() {
        config.setDataSourceType(DataSourceType.JSON);
        config.getJsonConfig().put("storagePath", "/data/org");
        
        Map<String, Object> active = config.getActiveConfig();
        assertEquals("/data/org", active.get("storagePath"));
    }
    
    @Test
    public void testGetActiveConfigDingtalk() {
        config.setDataSourceType(DataSourceType.DINGTALK);
        config.getDingtalkConfig().put("appKey", "test-key");
        
        Map<String, Object> active = config.getActiveConfig();
        assertEquals("test-key", active.get("appKey"));
    }
    
    @Test
    public void testGetActiveConfigDatabase() {
        config.setDataSourceType(DataSourceType.DATABASE);
        config.getDatabaseConfig().put("url", "jdbc:mysql://localhost:3306/orgdb");
        
        Map<String, Object> active = config.getActiveConfig();
        assertEquals("jdbc:mysql://localhost:3306/orgdb", active.get("url"));
    }
    
    @Test
    public void testSyncConfig() {
        config.getSyncConfig().setEnabled(true);
        config.getSyncConfig().setInterval(60000);
        config.getSyncConfig().setBatchSize(50);
        
        assertTrue(config.getSyncConfig().isEnabled());
        assertEquals(60000, config.getSyncConfig().getInterval());
        assertEquals(50, config.getSyncConfig().getBatchSize());
    }
    
    @Test
    public void testGetPresetCapabilities() {
        config.setDataSourceType(DataSourceType.JSON);
        Set<String> caps = config.getPresetCapabilities();
        assertNotNull(caps);
        assertTrue(caps.contains("org-query"));
        assertTrue(caps.contains("person-query"));
        
        config.setDataSourceType(DataSourceType.DATABASE);
        caps = config.getPresetCapabilities();
        assertTrue(caps.contains("org-admin"));
        assertTrue(caps.contains("user-auth"));
    }
    
    @Test
    public void testToString() {
        config.setSceneId("test-scene");
        config.setDataSourceType(DataSourceType.DINGTALK);
        
        String str = config.toString();
        assertTrue(str.contains("test-scene"));
        assertTrue(str.contains("DINGTALK"));
    }
}
