package net.ooder.skill.role.config;

import net.ooder.skill.role.service.*;
import net.ooder.skill.role.service.impl.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.role")
@ConditionalOnProperty(name = "skill.role.enabled", havingValue = "true", matchIfMissing = true)
public class RoleAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(RoleService.class)
    public RoleService roleService() {
        return new RoleServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(PermissionService.class)
    public PermissionService permissionService() {
        return new PermissionServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(RoleMenuService.class)
    public RoleMenuService roleMenuService() {
        return new RoleMenuServiceImpl();
    }
}
