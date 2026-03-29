package net.ooder.scene.config;

import net.ooder.scene.core.activation.executor.ConfirmParticipantsExecutor;
import net.ooder.scene.core.activation.executor.SelectPushTargetsExecutor;
import net.ooder.scene.core.spi.ServiceLocator;
import net.ooder.scene.core.spi.org.OrganizationService;
import net.ooder.scene.core.spi.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 执行器自动配置类
 *
 * <p>提供执行器的 Spring Boot 自动配置支持，自动注入服务依赖。</p>
 *
 * <p>MVP可以通过实现 ServiceLocator 接口来提供 UserService 和 OrganizationService。</p>
 *
 * <p>默认启用，可通过配置禁用：</p>
 * <pre>
 * scene.executor.auto-config.enabled: false
 * </pre>
 *
 * @author SE SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "scene.executor.auto-config", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ExecutorAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ConfirmParticipantsExecutor confirmParticipantsExecutor(
            @Autowired(required = false) UserService userService) {
        ConfirmParticipantsExecutor executor = new ConfirmParticipantsExecutor();
        if (userService != null) {
            executor.setUserService(userService);
        }
        return executor;
    }

    @Bean
    @ConditionalOnMissingBean
    public SelectPushTargetsExecutor selectPushTargetsExecutor(
            @Autowired(required = false) OrganizationService organizationService) {
        SelectPushTargetsExecutor executor = new SelectPushTargetsExecutor();
        if (organizationService != null) {
            executor.setOrganizationService(organizationService);
        }
        return executor;
    }

    @Bean
    @ConditionalOnMissingBean
    public ServiceLocator serviceLocator(
            @Autowired(required = false) UserService userService,
            @Autowired(required = false) OrganizationService organizationService) {
        return new DefaultServiceLocator(userService, organizationService);
    }

    private static class DefaultServiceLocator implements ServiceLocator {
        private final UserService userService;
        private final OrganizationService organizationService;

        DefaultServiceLocator(UserService userService, OrganizationService organizationService) {
            this.userService = userService;
            this.organizationService = organizationService;
        }

        @Override
        public UserService getUserService() {
            return userService;
        }

        @Override
        public OrganizationService getOrganizationService() {
            return organizationService;
        }
    }
}
