package net.ooder.scene.driver.tiny;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Tiny 驱动自动配置
 *
 * <p>适用于微型环境：开发测试、单机部署</p>
 *
 * <p>启用条件：</p>
 * <pre>
 * scene.engine.driver: tiny
 * </pre>
 *
 * @author Ooder Team
 * @since 3.0.0
 */
@Configuration
@ComponentScan(basePackages = "net.ooder.scene.driver.tiny")
@ConditionalOnProperty(prefix = "scene.engine", name = "driver", havingValue = "tiny")
public class TinyDriverAutoConfiguration {

}
