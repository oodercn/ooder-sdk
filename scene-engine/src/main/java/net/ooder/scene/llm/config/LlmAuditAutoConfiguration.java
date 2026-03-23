package net.ooder.scene.llm.config;

import net.ooder.scene.llm.audit.LlmAuditService;
import net.ooder.scene.llm.audit.impl.JsonLlmAuditServiceImpl;
import net.ooder.scene.llm.stats.LlmStatsAggregationService;
import net.ooder.scene.llm.stats.impl.JsonLlmStatsAggregationServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * LLM 审计自动配置类
 * 
 * <p>提供 LLM 调用审计服务的 Spring Boot 自动配置。</p>
 * 
 * <h3>配置项：</h3>
 * <pre>
 * scene.engine.llm.audit.enabled: true (默认启用)
 * scene.engine.llm.audit.data-path: data/llm-audit (默认路径)
 * scene.engine.llm.audit.max-log-size: 10000 (最大日志条数)
 * </pre>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
@Configuration
@ConditionalOnClass(LlmAuditService.class)
@ConditionalOnProperty(name = "scene.engine.llm.audit.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(LlmAuditProperties.class)
public class LlmAuditAutoConfiguration {
    
    private static final Logger log = LoggerFactory.getLogger(LlmAuditAutoConfiguration.class);
    
    @Bean
    @ConditionalOnMissingBean(LlmAuditService.class)
    public LlmAuditService llmAuditService(LlmAuditProperties properties) {
        String dataPath = properties.getDataPath();
        log.info("Initializing LlmAuditService with dataPath: {}", dataPath);
        
        JsonLlmAuditServiceImpl service = new JsonLlmAuditServiceImpl(dataPath);
        
        log.info("LlmAuditService initialized successfully");
        return service;
    }
    
    @Bean
    @ConditionalOnMissingBean(LlmStatsAggregationService.class)
    @ConditionalOnProperty(name = "scene.engine.llm.audit.stats-enabled", havingValue = "true", matchIfMissing = true)
    public LlmStatsAggregationService llmStatsAggregationService(LlmAuditService llmAuditService) {
        log.info("Initializing LlmStatsAggregationService");
        return new JsonLlmStatsAggregationServiceImpl(llmAuditService);
    }
}
