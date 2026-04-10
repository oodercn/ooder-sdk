package net.ooder.skill.hotplug.autoconfigure;

import net.ooder.skill.hotplug.HotPlugProperties;
import net.ooder.skill.hotplug.PluginManager;
import net.ooder.skill.hotplug.classloader.ClassLoaderManager;
import net.ooder.skill.hotplug.controller.PluginController;
import net.ooder.skill.hotplug.registry.RouteRegistry;
import net.ooder.skill.hotplug.registry.ServiceRegistry;
import net.ooder.skill.hotplug.ui.UiRouteRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 热插拔自动配置
 */
@Configuration
@ConditionalOnProperty(prefix = "ooder.skill.hotplug", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(HotPlugProperties.class)
@ComponentScan(basePackages = "net.ooder.skill.hotplug")
public class HotPlugAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ClassLoaderManager classLoaderManager() {
        return new ClassLoaderManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public RouteRegistry routeRegistry() {
        return new RouteRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public ServiceRegistry serviceRegistry() {
        return new ServiceRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public UiRouteRegistry uiRouteRegistry() {
        return new UiRouteRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public PluginManager pluginManager() {
        return new PluginManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public PluginController pluginController() {
        return new PluginController();
    }
}
