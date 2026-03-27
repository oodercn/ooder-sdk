package net.ooder.scene.config;

import net.ooder.scene.a2a.A2AProtocolService;
import net.ooder.scene.a2a.A2AProtocolServiceImpl;
import net.ooder.scene.agent.AgentMessageBus;
import net.ooder.scene.agent.context.AgentContextManager;
import net.ooder.scene.agent.context.AgentContextManagerImpl;
import net.ooder.scene.agent.persistence.MessagePersistence;
import net.ooder.scene.message.northbound.NorthboundMessageQueue;
import net.ooder.scene.message.northbound.NorthboundMessageQueueImpl;
import net.ooder.scene.message.queue.MessageQueueService;
import net.ooder.scene.message.queue.MessageQueueServiceImpl;
import net.ooder.scene.session.unified.JsonSessionStorage;
import net.ooder.scene.session.unified.UnifiedSessionManager;
import net.ooder.scene.session.unified.UnifiedSessionManagerImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 统一接口自动配置
 *
 * <p>自动配置 SE SDK 统一接口的所有组件。</p>
 * <p>复用现有组件：AgentMessageBus、MessagePersistence</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
@Configuration
@EnableConfigurationProperties(UnifiedInterfaceProperties.class)
public class UnifiedInterfaceAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(UnifiedInterfaceAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "scene.session", name = "enabled", havingValue = "true", matchIfMissing = true)
    public UnifiedSessionManager unifiedSessionManager(UnifiedInterfaceProperties properties) {
        log.info("Initializing UnifiedSessionManager");
        
        JsonSessionStorage storage = new JsonSessionStorage(properties.getSession().getStorageRoot());
        storage.init();
        
        UnifiedSessionManagerImpl manager = new UnifiedSessionManagerImpl(storage);
        manager.setDefaultTtl(properties.getSession().getDefaultTtl());
        
        return manager;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "scene.agent", name = "enabled", havingValue = "true", matchIfMissing = true)
    public AgentContextManager agentContextManager(UnifiedInterfaceProperties properties) {
        log.info("Initializing AgentContextManager");
        
        AgentContextManagerImpl manager = new AgentContextManagerImpl();
        
        return manager;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "scene.message", name = "enabled", havingValue = "true", matchIfMissing = true)
    public MessageQueueService messageQueueService(
            @Autowired(required = false) AgentMessageBus agentMessageBus,
            @Autowired(required = false) MessagePersistence messagePersistence,
            UnifiedInterfaceProperties properties) {
        log.info("Initializing MessageQueueService");
        
        if (agentMessageBus != null && messagePersistence != null) {
            log.info("Using existing AgentMessageBus and MessagePersistence");
            return new MessageQueueServiceImpl(agentMessageBus, messagePersistence);
        } else {
            log.info("Using standalone MessageQueueService");
            return new MessageQueueServiceImpl();
        }
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "scene.a2a", name = "enabled", havingValue = "true", matchIfMissing = true)
    public A2AProtocolService a2aProtocolService(AgentContextManager agentContextManager,
                                                  MessageQueueService messageQueueService,
                                                  UnifiedInterfaceProperties properties) {
        log.info("Initializing A2AProtocolService");
        
        A2AProtocolServiceImpl service = new A2AProtocolServiceImpl(agentContextManager, messageQueueService);
        
        return service;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "scene.message", name = "enabled", havingValue = "true", matchIfMissing = true)
    public NorthboundMessageQueue northboundMessageQueue(MessageQueueService messageQueueService,
                                                          A2AProtocolService a2aProtocolService) {
        log.info("Initializing NorthboundMessageQueue");
        
        return new NorthboundMessageQueueImpl(messageQueueService, a2aProtocolService);
    }
}
