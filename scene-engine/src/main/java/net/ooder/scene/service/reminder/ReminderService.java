package net.ooder.scene.service.reminder;

import java.util.List;

/**
 * 提醒服务接口
 *
 * <p>提供场景提醒能力，支持定时提醒和自动提醒。</p>
 *
 * <h3>使用场景：</h3>
 * <ul>
 *   <li>场景激活后自动创建提醒任务</li>
 *   <li>定时提醒用户提交日志</li>
 *   <li>管理提醒任务</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface ReminderService {

    /**
     * 创建提醒任务
     *
     * @param config 提醒配置
     * @return 提醒任务
     */
    ReminderTask createReminder(ReminderConfig config);

    /**
     * 创建场景默认提醒
     *
     * @param sceneId 场景ID
     * @param userId 用户ID
     * @return 提醒任务
     */
    ReminderTask createDefaultReminder(String sceneId, String userId);

    /**
     * 取消提醒任务
     *
     * @param reminderId 提醒ID
     * @return 是否成功
     */
    boolean cancelReminder(String reminderId);

    /**
     * 暂停提醒任务
     *
     * @param reminderId 提醒ID
     * @return 是否成功
     */
    boolean pauseReminder(String reminderId);

    /**
     * 恢复提醒任务
     *
     * @param reminderId 提醒ID
     * @return 是否成功
     */
    boolean resumeReminder(String reminderId);

    /**
     * 更新提醒配置
     *
     * @param reminderId 提醒ID
     * @param config 新配置
     * @return 是否成功
     */
    boolean updateReminder(String reminderId, ReminderConfig config);

    /**
     * 获取用户的提醒列表
     *
     * @param userId 用户ID
     * @return 提醒任务列表
     */
    List<ReminderTask> getUserReminders(String userId);

    /**
     * 获取场景的提醒列表
     *
     * @param sceneId 场景ID
     * @return 提醒任务列表
     */
    List<ReminderTask> getSceneReminders(String sceneId);

    /**
     * 获取提醒详情
     *
     * @param reminderId 提醒ID
     * @return 提醒任务
     */
    ReminderTask getReminder(String reminderId);

    /**
     * 手动触发提醒
     *
     * @param reminderId 提醒ID
     */
    void triggerReminder(String reminderId);

    /**
     * 获取提醒历史记录
     *
     * @param reminderId 提醒ID
     * @param limit 限制数量
     * @return 历史记录列表
     */
    List<ReminderHistory> getReminderHistory(String reminderId, int limit);
}
