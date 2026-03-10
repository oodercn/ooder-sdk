package net.ooder.scene.provider;

import java.util.Map;

/**
 * 用户设置提供者接口
 *
 * @author Ooder Team
 * @version 2.3.1
 */
public interface UserSettingsProvider {

    /**
     * 获取用户设置
     * @param userId 用户ID
     * @return 用户设置
     */
    Map<String, Object> getUserSettings(String userId);

    /**
     * 更新用户设置
     * @param userId 用户ID
     * @param settings 设置
     * @return 更新结果
     */
    boolean updateUserSettings(String userId, Map<String, Object> settings);

    /**
     * 获取用户偏好
     * @param userId 用户ID
     * @param key 键
     * @return 偏好值
     */
    Object getUserPreference(String userId, String key);

    /**
     * 设置用户偏好
     * @param userId 用户ID
     * @param key 键
     * @param value 值
     * @return 设置结果
     */
    boolean setUserPreference(String userId, String key, Object value);
}
