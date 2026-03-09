package net.ooder.sdk.llm.installation;

import java.util.List;
import java.util.Map;

/**
 * 安装上下文管理器
 */
public interface InstallationContextManager {

    /**
     * 创建安装上下文
     * @param installId 安装ID
     * @param sceneId 场景ID
     * @return 安装上下文
     */
    InstallationContext createInstallationContext(String installId, String sceneId);

    /**
     * 创建安装上下文（带用户ID）
     * @param installId 安装ID
     * @param sceneId 场景ID
     * @param userId 用户ID
     * @return 安装上下文
     */
    InstallationContext createInstallationContext(String installId, String sceneId, String userId);

    /**
     * 获取安装上下文
     * @param installId 安装ID
     * @return 安装上下文
     */
    InstallationContext getInstallationContext(String installId);

    /**
     * 更新安装上下文
     * @param context 安装上下文
     */
    void updateInstallationContext(InstallationContext context);

    /**
     * 保存检查点
     * @param installId 安装ID
     * @param stepId 步骤ID
     * @param state 状态
     */
    void saveCheckpoint(String installId, String stepId, Map<String, Object> state);

    /**
     * 保存检查点（带名称）
     * @param installId 安装ID
     * @param stepId 步骤ID
     * @param checkpointName 检查点名称
     * @param state 状态
     */
    void saveCheckpoint(String installId, String stepId, String checkpointName, Map<String, Object> state);

    /**
     * 恢复检查点
     * @param installId 安装ID
     * @param stepId 步骤ID
     * @return 状态
     */
    Map<String, Object> restoreCheckpoint(String installId, String stepId);

    /**
     * 恢复检查点（按检查点ID）
     * @param installId 安装ID
     * @param checkpointId 检查点ID
     * @return 状态
     */
    Map<String, Object> restoreCheckpointById(String installId, String checkpointId);

    /**
     * 获取检查点列表
     * @param installId 安装ID
     * @return 检查点列表
     */
    List<Checkpoint> getCheckpoints(String installId);

    /**
     * 完成安装
     * @param installId 安装ID
     */
    void completeInstallation(String installId);

    /**
     * 完成安装（带结果）
     * @param installId 安装ID
     * @param result 结果
     */
    void completeInstallation(String installId, Object result);

    /**
     * 取消安装
     * @param installId 安装ID
     */
    void cancelInstallation(String installId);

    /**
     * 取消安装（带原因）
     * @param installId 安装ID
     * @param reason 原因
     */
    void cancelInstallation(String installId, String reason);

    /**
     * 标记安装失败
     * @param installId 安装ID
     * @param errorMessage 错误信息
     */
    void failInstallation(String installId, String errorMessage);

    /**
     * 添加安装步骤
     * @param installId 安装ID
     * @param step 安装步骤
     */
    void addInstallationStep(String installId, InstallationStep step);

    /**
     * 更新安装步骤
     * @param installId 安装ID
     * @param step 安装步骤
     */
    void updateInstallationStep(String installId, InstallationStep step);

    /**
     * 获取安装进度
     * @param installId 安装ID
     * @return 进度百分比（0-100）
     */
    int getInstallationProgress(String installId);

    /**
     * 获取所有安装上下文
     * @return 安装上下文列表
     */
    List<InstallationContext> listAllContexts();

    /**
     * 获取指定状态的安装上下文
     * @param status 状态
     * @return 安装上下文列表
     */
    List<InstallationContext> listContextsByStatus(InstallationStatus status);

    /**
     * 删除安装上下文
     * @param installId 安装ID
     */
    void deleteInstallationContext(String installId);

    /**
     * 检查安装是否存在
     * @param installId 安装ID
     * @return 是否存在
     */
    boolean hasInstallation(String installId);
}
