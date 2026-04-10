package net.ooder.skill.common.sdk.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

import jakarta.annotation.PostConstruct;

/**
 * Skill SDK 2.3 自动配置类
 * 
 * @author Skills Team
 * @version 2.3.0
 * @since 2026-02-24
 */
@Configuration
@EnableScheduling
@EnableAspectJAutoProxy
public class SkillSdkAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SkillSdkAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("Skill SDK 2.3 AutoConfiguration initialized");
    }

    @Bean
    @ConditionalOnMissingBean
    public SkillsHealthIndicator skillsHealthIndicator() {
        return new SkillsHealthIndicator();
    }

    @Bean
    @ConditionalOnMissingBean
    public SkillsMetricsConfigurer skillsMetricsConfigurer() {
        return new SkillsMetricsConfigurer();
    }

    public static class SkillsHealthIndicator {
        
        public void checkSkillHealth(String skillId) {
            log.debug("Checking health for skill: {}", skillId);
        }
    }

    public static class SkillsMetricsConfigurer {
        
        public void configureSkillMetrics(String skillId) {
            log.debug("Configuring metrics for skill: {}", skillId);
        }
    }
}
