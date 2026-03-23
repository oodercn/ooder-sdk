package net.ooder.scene.bridge;

import net.ooder.sdk.api.scene.SceneGroupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SDK SceneGroupManager 自动配置
 *
 * <p>为 MVP 项目提供 SDK SceneGroupManager 接口的适配器实现。</p>
 *
 * <p>架构层级：桥接层 - 自动配置</p>
 *
 * <p>默认禁用，需要通过配置显式启用：</p>
 * <pre>
 * scene.bridge.sdk.enabled: true
 * </pre>
 *
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
@Configuration
@ConditionalOnClass(name = "net.ooder.sdk.api.scene.SceneGroupManager")
@ConditionalOnProperty(name = "scene.bridge.sdk.enabled", havingValue = "true", matchIfMissing = false)
public class SdkSceneGroupAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SdkSceneGroupAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(SceneGroupManager.class)
    public SceneGroupManager sdkSceneGroupManager(
            net.ooder.scene.group.SceneGroupManager seManager) {
        log.info("============================================================");
        log.info("初始化 SDK SceneGroupManager 适配器");
        log.info("适配器: SdkSceneGroupManagerAdapter");
        log.info("实现: SE SceneGroupManager -> SDK SceneGroupManager");
        log.info("============================================================");
        return new SdkSceneGroupManagerAdapter(seManager);
    }
}
