package net.ooder.scene.config;

import net.ooder.scene.core.SceneClient;
import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.core.impl.SceneClientImpl;
import net.ooder.scene.skill.knowledge.KnowledgeBindingService;
import net.ooder.scene.skill.notification.NotificationService;
import net.ooder.scene.skill.install.CapabilityInstallLifecycle;
import net.ooder.scene.session.SessionInfo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Scene Engine 自动配置类
 *
 * <p>提供 Spring Boot 自动配置支持，自动注入：</p>
 * <ul>
 *   <li>SceneClient - 场景客户端</li>
 *   <li>NotificationService - 通知服务</li>
 *   <li>KnowledgeBindingService - 知识库绑定服务</li>
 *   <li>CapabilityInstallLifecycle - 能力安装生命周期</li>
 * </ul>
 *
 * @author ooder
 * @since 2.3.2
 */
@Configuration
@ConditionalOnClass(SceneClient.class)
public class SceneEngineAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({SessionInfo.class, SceneEngine.class})
    public SceneClient sceneClient(SessionInfo session, SceneEngine engine) {
        return new SceneClientImpl(session, engine);
    }

    @Bean
    @ConditionalOnMissingBean
    public NotificationService notificationService() {
        return new DefaultNotificationServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public KnowledgeBindingService knowledgeBindingService() {
        return new DefaultKnowledgeBindingServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public CapabilityInstallLifecycle capabilityInstallLifecycle() {
        return new DefaultCapabilityInstallLifecycle();
    }
}
