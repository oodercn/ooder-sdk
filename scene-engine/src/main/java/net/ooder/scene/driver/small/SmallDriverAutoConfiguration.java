package net.ooder.scene.driver.small;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Small 驱动自动配置
 *
 * <p>适用于小型环境：小团队、单服务部署</p>
 *
 * <p>启用条件：</p>
 * <pre>
 * scene.engine.driver: small
 * </pre>
 *
 * <p>依赖：</p>
 * <ul>
 *   <li>MySQL 或 PostgreSQL</li>
 *   <li>Redis (可选)</li>
 *   <li>Milvus Lite 或 Chroma</li>
 * </ul>
 *
 * @author Ooder Team
 * @since 3.0.0
 */
@Configuration
@ComponentScan(basePackages = "net.ooder.scene.driver.small")
@ConditionalOnProperty(prefix = "scene.engine", name = "driver", havingValue = "small")
public class SmallDriverAutoConfiguration {

}
