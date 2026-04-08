package net.ooder.config.core;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReaderTest {
    
    @Before
    public void setUp() {
        ConfigRegistry.getInstance().reset();
    }
    
    @After
    public void tearDown() {
        ConfigRegistry.getInstance().reset();
    }
    
    @Test
    public void testPropertiesConfigReader() {
        Properties props = new Properties();
        props.setProperty("jds.home", "/opt/jds");
        props.setProperty("server.port", "8081");
        props.setProperty("serverUrl", "http://localhost:8081");
        props.setProperty("udpServer.enabled", "true");
        props.setProperty("udpServer.port", "8087");
        props.setProperty("session.enabled", "true");
        props.setProperty("session.ExpireTime", "30");
        props.setProperty("singleLogin", "true");
        
        PropertiesConfigReader reader = new PropertiesConfigReader();
        OoderConfig config = reader.read(props);
        
        assertEquals("/opt/jds", config.getJds().getHome());
        assertEquals(Integer.valueOf(8081), config.getServer().getPort());
        assertEquals("http://localhost:8081", config.getUser().getServerUrl());
        assertTrue(config.getCluster().getUdp().isEnabled());
        assertEquals(Integer.valueOf(8087), config.getCluster().getUdp().getPort());
        assertTrue(config.getSession().isEnabled());
        assertEquals(30L, config.getSession().getExpireTime());
        assertTrue(config.getSession().isSingleLogin());
    }
    
    @Test
    public void testPropertiesConfigReaderWithInputStream() throws IOException {
        String propsContent = 
            "jds.home=/opt/jds\n" +
            "server.port=9090\n" +
            "serverUrl=http://192.168.1.100:8081\n" +
            "udpServer.enabled=false\n" +
            "session.ExpireTime=60\n";
        
        ByteArrayInputStream bais = new ByteArrayInputStream(propsContent.getBytes());
        
        PropertiesConfigReader reader = new PropertiesConfigReader();
        OoderConfig config = reader.read(bais);
        
        assertEquals("/opt/jds", config.getJds().getHome());
        assertEquals(Integer.valueOf(9090), config.getServer().getPort());
        assertEquals("http://192.168.1.100:8081", config.getUser().getServerUrl());
        assertFalse(config.getCluster().getUdp().isEnabled());
        assertEquals(60L, config.getSession().getExpireTime());
    }
    
    @Test
    public void testYamlConfigReaderWithSnakeYaml() {
        String yamlContent = 
            "ooder:\n" +
            "  jds:\n" +
            "    home: /opt/jds\n" +
            "    config-name: production\n" +
            "  server:\n" +
            "    port: 8081\n" +
            "    admin:\n" +
            "      enabled: true\n" +
            "      port: 9090\n" +
            "  cluster:\n" +
            "    enabled: true\n" +
            "    udp:\n" +
            "      port: 8087\n" +
            "      code: utf-8\n" +
            "  session:\n" +
            "    expire-time: 30\n" +
            "    single-login: true\n" +
            "  user:\n" +
            "    server-url: http://localhost:8081\n" +
            "    system-code: TEST-SYSTEM\n";
        
        ByteArrayInputStream bais = new ByteArrayInputStream(yamlContent.getBytes());
        
        YamlConfigReader reader = new YamlConfigReader();
        OoderConfig config = reader.read(bais);
        
        assertEquals("/opt/jds", config.getJds().getHome());
        assertEquals("production", config.getJds().getConfigName());
        assertEquals(Integer.valueOf(8081), config.getServer().getPort());
        assertTrue(config.getServer().getAdmin().isEnabled());
        assertEquals(Integer.valueOf(9090), config.getServer().getAdmin().getPort());
        assertTrue(config.getCluster().isEnabled());
        assertEquals(Integer.valueOf(8087), config.getCluster().getUdp().getPort());
        assertEquals("utf-8", config.getCluster().getUdp().getCode());
        assertEquals(30L, config.getSession().getExpireTime());
        assertTrue(config.getSession().isSingleLogin());
        assertEquals("http://localhost:8081", config.getUser().getServerUrl());
        assertEquals("TEST-SYSTEM", config.getUser().getSystemCode());
    }
    
    @Test
    public void testConfigRegistryIntegration() {
        Properties props = new Properties();
        props.setProperty("jds.home", "/integration/test");
        props.setProperty("server.port", "8888");
        
        PropertiesConfigReader reader = new PropertiesConfigReader();
        OoderConfig config = reader.read(props);
        
        ConfigRegistry registry = ConfigRegistry.getInstance();
        registry.setActiveConfig(config);
        
        assertEquals("/integration/test", registry.getValue("jds.home"));
        assertEquals("8888", registry.getValue("server.port"));
        
        Properties testProps = new Properties();
        testProps.setProperty("jds.home", "/override/test");
        registry.setTestProperties(testProps);
        
        assertEquals("/override/test", registry.getValue("jds.home"));
        
        registry.clearTestProperties();
        assertEquals("/integration/test", registry.getValue("jds.home"));
    }
}
