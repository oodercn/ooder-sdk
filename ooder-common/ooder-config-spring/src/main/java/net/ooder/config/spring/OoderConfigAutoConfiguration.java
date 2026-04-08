package net.ooder.config.spring;

import net.ooder.config.core.ConfigRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(ConfigRegistry.class)
@EnableConfigurationProperties(OoderConfigProperties.class)
public class OoderConfigAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public ConfigRegistry configRegistry(OoderConfigProperties properties) {
        ConfigRegistry registry = ConfigRegistry.getInstance();
        registry.setActiveConfig(properties);
        return registry;
    }
}
