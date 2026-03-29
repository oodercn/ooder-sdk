package net.ooder.scene.core.spi.user;

import java.util.List;
import java.util.Map;

/**
 * 用户服务接口 - MVP实现此接口
 * 
 * <p>提供用户信息查询和验证功能，供执行器调用。</p>
 * 
 * @author SE SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
public interface UserService {

    /**
     * 批量获取用户信息
     * 
     * @param userIds 用户ID列表
     * @return 用户ID -> 用户信息映射
     */
    Map<String, UserInfo> getUsers(List<String> userIds);

    /**
     * 获取单个用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息，不存在返回null
     */
    UserInfo getUser(String userId);

    /**
     * 验证用户是否存在
     * 
     * @param userId 用户ID
     * @return true=存在
     */
    boolean userExists(String userId);

    /**
     * 批量验证用户是否存在
     * 
     * @param userIds 用户ID列表
     * @return 不存在的用户ID列表
     */
    List<String> validateUsers(List<String> userIds);
}
