package net.ooder.scene.core.spi;

import net.ooder.scene.core.spi.org.OrganizationService;
import net.ooder.scene.core.spi.user.UserService;

/**
 * 服务定位器接口 - MVP实现此接口
 * 
 * <p>SE SDK通过SPI机制获取服务实现。</p>
 * <p>MVP通过实现此接口，将用户服务和组织服务注入到执行器中。</p>
 * 
 * @author SE SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
public interface ServiceLocator {

    /**
     * 获取用户服务
     * 
     * @return 用户服务实例，可能为null
     */
    UserService getUserService();

    /**
     * 获取组织服务
     * 
     * @return 组织服务实例，可能为null
     */
    OrganizationService getOrganizationService();
}
