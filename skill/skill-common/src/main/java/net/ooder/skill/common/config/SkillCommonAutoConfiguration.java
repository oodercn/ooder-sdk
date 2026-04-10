package net.ooder.skill.common.config;

import net.ooder.skill.common.api.AuthApi;
import net.ooder.skill.common.api.ConfigApi;
import net.ooder.skill.common.discovery.DiscoveryOrchestrator;
import net.ooder.skill.common.discovery.LocalSkillDiscoverer;
import net.ooder.skill.common.discovery.SkillIndexLoader;
import net.ooder.skill.common.service.AuthService;
import net.ooder.skill.common.service.OrgService;
import net.ooder.skill.common.storage.JsonStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "skill.common.enabled", havingValue = "true", matchIfMissing = true)
public class SkillCommonAutoConfiguration {

    @Value("${app.storage.path:./data}")
    private String storagePath;

    @Bean
    @ConditionalOnMissingBean(JsonStorageService.class)
    public JsonStorageService jsonStorageService() {
        JsonStorageService service = new JsonStorageService(storagePath);
        service.init();
        return service;
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthService authService() {
        return new AuthService();
    }

    @Bean
    @ConditionalOnMissingBean
    public OrgService orgService(JsonStorageService storage) {
        OrgService service = new OrgService(storage);
        service.init();
        return service;
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthApi authApi() {
        return new AuthApi();
    }

    @Bean
    @ConditionalOnMissingBean
    public ConfigApi configApi() {
        return new ConfigApi();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public SkillIndexLoader skillIndexLoader() {
        return new SkillIndexLoader();
    }
    
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "ooder.discovery.local.enabled", havingValue = "true", matchIfMissing = true)
    public LocalSkillDiscoverer localSkillDiscoverer() {
        LocalSkillDiscoverer discoverer = new LocalSkillDiscoverer();
        return discoverer;
    }
    
    @Bean
    @ConditionalOnMissingBean
    public DiscoveryOrchestrator discoveryOrchestrator(SkillIndexLoader skillIndexLoader) {
        DiscoveryOrchestrator orchestrator = new DiscoveryOrchestrator();
        return orchestrator;
    }
}
