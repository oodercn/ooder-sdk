package net.ooder.sdk.autoconfigure;

import net.ooder.sdk.api.OoderSDK;
import net.ooder.sdk.api.agent.AgentFactory;
import net.ooder.sdk.api.PublicAPI;
import net.ooder.sdk.core.agent.factory.AgentFactoryImpl;
import net.ooder.sdk.infra.config.SDKConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Ooder SDK Spring Boot 自动配置类
 *
 * @version 3.0.0
 * @since 3.0.0
 */
@AutoConfiguration
@ConditionalOnClass(OoderSDK.class)
@EnableConfigurationProperties(OoderSdkProperties.class)
@ConditionalOnProperty(prefix = "ooder.sdk", name = "enabled", havingValue = "true", matchIfMissing = true)
public class OoderSdkAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(OoderSdkAutoConfiguration.class);

    private final OoderSdkProperties properties;

    public OoderSdkAutoConfiguration(OoderSdkProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean
    public SDKConfiguration sdkConfiguration() {
        SDKConfiguration config = new SDKConfiguration();
        config.setAgentId(properties.getAgentId());
        config.setAgentName(properties.getAgentName());
        config.setAgentType(properties.getAgentType());
        config.setEndpoint(properties.getEndpoint());
        config.setUdpPort(properties.getUdpPort());
        config.setSkillRootPath(properties.getSkillRootPath());
        config.setSkillCenterUrl(properties.getSkillCenterUrl());
        config.setVfsUrl(properties.getVfsUrl());
        config.setStrictMode(properties.isStrictMode());
        config.setDiscoveryEnabled(properties.isDiscoveryEnabled());
        config.setHeartbeatInterval(properties.getHeartbeatInterval());
        config.setHeartbeatTimeout(properties.getHeartbeatTimeout());
        return config;
    }

    @Bean
    @ConditionalOnMissingBean
    public AgentFactory agentFactory() {
        log.info("Initializing Ooder SDK AgentFactory");
        return new AgentFactoryImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public OoderSDK ooderSDK(SDKConfiguration sdkConfiguration, AgentFactory agentFactory) {
        log.info("Initializing Ooder SDK with agentId: {}", sdkConfiguration.getAgentId());
        return OoderSDK.builder()
                .configuration(sdkConfiguration)
                .agentFactory(agentFactory)
                .build();
    }
}
