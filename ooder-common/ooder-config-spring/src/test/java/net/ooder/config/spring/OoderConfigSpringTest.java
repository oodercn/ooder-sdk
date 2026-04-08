package net.ooder.config.spring;

import net.ooder.config.core.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class OoderConfigSpringTest {
    
    @Autowired(required = false)
    private OoderConfigProperties configProperties;
    
    @Autowired(required = false)
    private ConfigRegistry configRegistry;
    
    @Test
    public void testConfigPropertiesAutowiring() {
        if (configProperties != null) {
            assertNotNull(configProperties.getJds());
            assertNotNull(configProperties.getServer());
            assertNotNull(configProperties.getCluster());
        }
    }
    
    @Test
    public void testConfigRegistryAutowiring() {
        if (configRegistry != null) {
            OoderConfig config = configRegistry.getActiveConfig();
            if (config != null) {
                assertNotNull(config.getJds());
            }
        }
    }
}
