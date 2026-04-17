package net.ooder.skill.capability.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.capability")
@ConditionalOnProperty(name = "skill.capability.enabled", havingValue = "true", matchIfMissing = true)
public class CapabilityAutoConfiguration {

}
