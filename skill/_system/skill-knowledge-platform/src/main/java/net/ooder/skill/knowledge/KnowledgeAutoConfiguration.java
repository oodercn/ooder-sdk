package net.ooder.skill.knowledge;

import net.ooder.scene.skill.knowledge.KnowledgeOrganizationService;
import net.ooder.scene.skill.knowledge.impl.IntegratedKnowledgeOrganizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.knowledge")
public class KnowledgeAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(KnowledgeOrganizationService.class)
    public KnowledgeOrganizationService knowledgeOrganizationService() {
        log.info("[KnowledgeAutoConfiguration] Creating KnowledgeOrganizationService bean");
        return new IntegratedKnowledgeOrganizationService(null);
    }
}
