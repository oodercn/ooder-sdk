package net.ooder.scene.driver.enterprise;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Enterprise 驱动自动配置
 *
 * <p>适用于企业级环境：高可用、分布式部署</p>
 *
 * <p>启用条件：</p>
 * <pre>
 * scene.engine.driver: enterprise
 * </pre>
 *
 * <p>依赖：</p>
 * <ul>
 *   <li>分布式数据库</li>
 *   <li>分布式缓存</li>
 *   <li>分布式向量库</li>
 *   <li>多模型 LLM 路由</li>
 * </ul>
 *
 * @author Ooder Team
 * @since 3.0.0
 */
@Configuration
@ComponentScan(basePackages = "net.ooder.scene.driver.enterprise")
@ConditionalOnProperty(prefix = "scene.engine", name = "driver", havingValue = "enterprise")
public class EnterpriseDriverAutoConfiguration {

}
