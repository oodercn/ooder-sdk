package net.ooder.scene.provider;

import net.ooder.scene.core.UserSettings;

/**
 * 用户设置提供者接口
 * 管理用户设置
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface UserSettingsProvider extends BaseProvider {

    /**
     * 获取用户设置
     * @param userId 用户ID
     * @return 用户设置
     */
    UserSettings getSettings(String userId);

    /**
     * 更新用户设置
     * @param userId 用户ID
     * @param settings 用户设置
     */
    void updateSettings(String userId, UserSettings settings);
}
