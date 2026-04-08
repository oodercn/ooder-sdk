package net.ooder.config.core;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Properties;

public class OoderConfigTest {
    
    @Before
    public void setUp() {
        ConfigRegistry.getInstance().reset();
    }
    
    @After
    public void tearDown() {
        ConfigRegistry.getInstance().reset();
    }
    
    @Test
    public void testDefaultConfig() {
        OoderConfig config = new OoderConfig();
        
        assertNotNull(config.getJds());
        assertNotNull(config.getServer());
        assertNotNull(config.getCluster());
        assertNotNull(config.getSession());
        assertNotNull(config.getCache());
        assertNotNull(config.getUser());
    }
    
    @Test
    public void testBuilderPattern() {
        OoderConfig config = OoderConfig.builder()
            .id("test-config")
            .name("Test Configuration")
            .description("Test config description")
            .build();
        
        assertEquals("test-config", config.getId());
        assertEquals("Test Configuration", config.getName());
        assertEquals("Test config description", config.getDescription());
    }
    
    @Test
    public void testJdsConfig() {
        OoderConfig config = new OoderConfig();
        config.getJds().setHome("/opt/jds");
        config.getJds().setConfigName("production");
        
        assertEquals("/opt/jds", config.getJds().getHome());
        assertEquals("production", config.getJds().getConfigName());
        assertEquals("/opt/jds", config.getValue("jds.home"));
        assertEquals("production", config.getValue("jds.configName"));
    }
    
    @Test
    public void testServerConfig() {
        OoderConfig config = new OoderConfig();
        config.getServer().setPort(8081);
        config.getServer().setUrl("http://localhost:8081");
        config.getServer().getAdmin().setEnabled(true);
        config.getServer().getAdmin().setPort(9090);
        config.getServer().getAdmin().setKey("admin-key");
        
        assertEquals("8081", config.getValue("server.port"));
        assertEquals("http://localhost:8081", config.getValue("server.url"));
        assertEquals("true", config.getValue("server.admin.enabled"));
        assertEquals("9090", config.getValue("server.admin.port"));
        assertEquals("admin-key", config.getValue("server.admin.key"));
    }
    
    @Test
    public void testClusterConfig() {
        OoderConfig config = new OoderConfig();
        config.getCluster().setEnabled(true);
        config.getCluster().getUdp().setEnabled(true);
        config.getCluster().getUdp().setPort(8087);
        config.getCluster().getUdp().setCode("utf-8");
        
        assertEquals("true", config.getValue("cluster.enabled"));
        assertEquals("true", config.getValue("cluster.udp.enabled"));
        assertEquals("8087", config.getValue("cluster.udp.port"));
        assertEquals("utf-8", config.getValue("cluster.udp.code"));
    }
    
    @Test
    public void testSessionConfig() {
        OoderConfig config = new OoderConfig();
        config.getSession().setEnabled(true);
        config.getSession().setExpireTime(60L);
        config.getSession().setCheckInterval(10L);
        config.getSession().setSingleLogin(false);
        
        assertEquals("true", config.getValue("session.enabled"));
        assertEquals("60", config.getValue("session.ExpireTime"));
        assertEquals("10", config.getValue("session.CheckInterval"));
        assertEquals("false", config.getValue("session.singleLogin"));
    }
    
    @Test
    public void testUserConfig() {
        OoderConfig config = new OoderConfig();
        config.getUser().setServerUrl("http://192.168.1.100:8081");
        config.getUser().setSystemCode("BPM-SYSTEM");
        config.getUser().setUsername("admin");
        config.getUser().setPassword("password123");
        
        assertEquals("http://192.168.1.100:8081", config.getValue("user.serverUrl"));
        assertEquals("BPM-SYSTEM", config.getValue("user.systemCode"));
        assertEquals("admin", config.getValue("user.username"));
        assertEquals("password123", config.getValue("user.password"));
    }
    
    @Test
    public void testToProperties() {
        OoderConfig config = new OoderConfig();
        config.getJds().setHome("/opt/jds");
        config.getServer().setPort(8081);
        config.getUser().setServerUrl("http://localhost:8081");
        
        Properties props = config.toProperties();
        
        assertEquals("/opt/jds", props.getProperty("jds.home"));
        assertEquals("8081", props.getProperty("server.port"));
        assertEquals("http://localhost:8081", props.getProperty("serverUrl"));
    }
    
    @Test
    public void testConfigRegistry() {
        OoderConfig config = OoderConfig.builder()
            .id("registry-test")
            .build();
        config.getJds().setHome("/test/home");
        
        ConfigRegistry registry = ConfigRegistry.getInstance();
        registry.setActiveConfig(config);
        
        assertEquals("/test/home", registry.getValue("jds.home"));
        assertEquals(config, registry.getActiveConfig());
    }
    
    @Test
    public void testConfigRegistryTestMode() {
        ConfigRegistry registry = ConfigRegistry.getInstance();
        
        Properties testProps = new Properties();
        testProps.setProperty("test.key", "test-value");
        testProps.setProperty("jds.home", "/test/path");
        
        registry.setTestProperties(testProps);
        
        assertTrue(registry.isTestMode());
        assertEquals("test-value", registry.getValue("test.key"));
        assertEquals("/test/path", registry.getValue("jds.home"));
        
        registry.clearTestProperties();
        assertFalse(registry.isTestMode());
    }
}
