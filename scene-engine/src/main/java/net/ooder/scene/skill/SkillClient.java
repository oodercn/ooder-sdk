package net.ooder.scene.skill;

import net.ooder.scene.core.*;
import net.ooder.scene.skill.runtime.SkillRuntime;
import net.ooder.scene.skill.state.SkillLifecycleState;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Skill客户端 - 统一对外服务接口
 *
 * <p>这是应用层与Skill系统交互的唯一入口</p>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface SkillClient {

    // ==================== 安装管理 ====================

    /**
     * 安装Skill
     */
    SkillInstallResult install(String skillId, Map<String, Object> config);

    /**
     * 卸载Skill
     */
    SkillUninstallResult uninstall(String skillId, boolean removeData);

    // ==================== 生命周期管理 ====================

    /**
     * 启动Skill
     */
    boolean start(String skillId);

    /**
     * 停止Skill
     */
    boolean stop(String skillId);

    /**
     * 重启Skill
     */
    boolean restart(String skillId);

    /**
     * 销毁Skill（强制清理）
     */
    void destroy(String skillId);

    /**
     * 获取Skill状态
     */
    SkillLifecycleState getStatus(String skillId);

    // ==================== 调用执行 ====================

    /**
     * 同步调用能力
     */
    Object invoke(String skillId, String capability, Map<String, Object> params);

    /**
     * 异步调用能力
     */
    CompletableFuture<Object> invokeAsync(String skillId, String capability, Map<String, Object> params);

    /**
     * 批量调用
     */
    List<SkillRuntime.InvokeResult> batchInvoke(List<SkillRuntime.InvokeRequest> requests);

    // ==================== 查询 ====================

    /**
     * 获取Skill信息
     */
    SkillRuntime.SkillInfo getSkill(String skillId);

    /**
     * 列出所有Skill
     */
    List<SkillRuntime.SkillInfo> listSkills();
}
