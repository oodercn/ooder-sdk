package net.ooder.config.scene.extension;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataSourceConfigTest {
    
    private DataSourceConfig config;
    
    @Before
    public void setUp() {
        config = new DataSourceConfig();
    }
    
    @Test
    public void testDefaultValues() {
        assertEquals(DataSourceConfig.TYPE_DATABASE, config.getType());
        assertTrue(config.isEnabled());
        assertNull(config.getPrefix());
        assertNotNull(config.getConfig());
    }
    
    @Test
    public void testSetType() {
        config.setType(DataSourceConfig.TYPE_DINGTALK);
        assertEquals(DataSourceConfig.TYPE_DINGTALK, config.getType());
        assertTrue(config.isDingTalk());
    }
    
    @Test
    public void testTypeCheckMethods() {
        config.setType(DataSourceConfig.TYPE_DATABASE);
        assertTrue(config.isDatabase());
        
        config.setType(DataSourceConfig.TYPE_DINGTALK);
        assertTrue(config.isDingTalk());
        assertTrue(config.isThirdParty());
        
        config.setType(DataSourceConfig.TYPE_FEISHU);
        assertTrue(config.isFeishu());
        
        config.setType(DataSourceConfig.TYPE_WECOM);
        assertTrue(config.isWeCom());
        
        config.setType(DataSourceConfig.TYPE_LDAP);
        assertTrue(config.isLdap());
        
        config.setType(DataSourceConfig.TYPE_JSON);
        assertTrue(config.isJson());
    }
    
    @Test
    public void testConfigMap() {
        config.set("appKey", "test-key");
        config.set("appSecret", "test-secret");
        
        assertEquals("test-key", config.getString("appKey"));
        assertEquals("test-key", config.get("appKey"));
    }
    
    @Test
    public void testGetStringWithDefault() {
        assertEquals("default", config.getString("nonexistent", "default"));
    }
    
    @Test
    public void testGetInt() {
        config.set("timeout", 5000);
        assertEquals(5000, config.getInt("timeout", 0));
        
        config.set("timeoutStr", "3000");
        assertEquals(3000, config.getInt("timeoutStr", 0));
        
        assertEquals(100, config.getInt("nonexistent", 100));
    }
    
    @Test
    public void testGetBoolean() {
        config.set("enabled", true);
        assertTrue(config.getBoolean("enabled", false));
        
        config.set("enabledStr", "true");
        assertTrue(config.getBoolean("enabledStr", false));
        
        assertFalse(config.getBoolean("nonexistent", false));
    }
    
    @Test
    public void testStaticFactoryMethods() {
        DataSourceConfig db = DataSourceConfig.database();
        assertEquals(DataSourceConfig.TYPE_DATABASE, db.getType());
        
        DataSourceConfig dingtalk = DataSourceConfig.dingTalk();
        assertEquals(DataSourceConfig.TYPE_DINGTALK, dingtalk.getType());
        
        DataSourceConfig feishu = DataSourceConfig.feishu();
        assertEquals(DataSourceConfig.TYPE_FEISHU, feishu.getType());
        
        DataSourceConfig wecom = DataSourceConfig.weCom();
        assertEquals(DataSourceConfig.TYPE_WECOM, wecom.getType());
        
        DataSourceConfig ldap = DataSourceConfig.ldap();
        assertEquals(DataSourceConfig.TYPE_LDAP, ldap.getType());
        
        DataSourceConfig json = DataSourceConfig.json();
        assertEquals(DataSourceConfig.TYPE_JSON, json.getType());
    }
    
    @Test
    public void testPriority() {
        config.setPriority(10);
        assertEquals(10, config.getPriority());
    }
    
    @Test
    public void testToString() {
        String str = config.toString();
        assertTrue(str.contains("database"));
        assertTrue(str.contains("enabled=true"));
    }
}
