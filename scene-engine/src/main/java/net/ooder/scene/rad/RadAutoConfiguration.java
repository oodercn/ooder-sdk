package net.ooder.scene.rad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * RAD 集成自动配置
 *
 * <p>启用条件：</p>
 * <pre>
 * scene.engine.rad.enabled: true
 * </pre>
 *
 * @author Ooder Team
 * @since 3.0.0
 */
@Configuration
@ComponentScan(basePackages = "net.ooder.scene.rad")
@ConditionalOnProperty(prefix = "scene.engine.rad", name = "enabled", havingValue = "true")
public class RadAutoConfiguration {

}
