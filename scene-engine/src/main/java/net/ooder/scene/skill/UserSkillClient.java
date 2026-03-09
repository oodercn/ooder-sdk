package net.ooder.scene.skill;

import net.ooder.scene.core.*;
import net.ooder.scene.skill.runtime.SkillRuntime;
import net.ooder.scene.skill.state.SkillLifecycleState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 用户Skill客户端 - 实现用户隔离
 *
 * <p>每个用户会话对应一个UserSkillClient实例</p>
 * <p>自动绑定userId，无需手动传递</p>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class UserSkillClient implements SkillClient {

    private static final Logger log = LoggerFactory.getLogger(UserSkillClient.class);

    private final String userId;
    private final String sessionId;
    private final SkillRuntime runtime;

    public UserSkillClient(String userId, String sessionId, SkillRuntime runtime) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.runtime = runtime;
        log.debug("Created UserSkillClient for user={}, session={}", userId, sessionId);
    }

    // ==================== 安装管理 ====================

    @Override
    public SkillInstallResult install(String skillId, Map<String, Object> config) {
        log.debug("User {} installing skill: {}", userId, skillId);
        return runtime.install(userId, skillId, config);
    }

    @Override
    public SkillUninstallResult uninstall(String skillId, boolean removeData) {
        log.debug("User {} uninstalling skill: {}", userId, skillId);
        return runtime.uninstall(userId, skillId, removeData);
    }

    // ==================== 生命周期管理 ====================

    @Override
    public boolean start(String skillId) {
        log.debug("User {} starting skill: {}", userId, skillId);
        return runtime.start(userId, skillId);
    }

    @Override
    public boolean stop(String skillId) {
        log.debug("User {} stopping skill: {}", userId, skillId);
        return runtime.stop(userId, skillId);
    }

    @Override
    public boolean restart(String skillId) {
        log.debug("User {} restarting skill: {}", userId, skillId);
        return runtime.restart(userId, skillId);
    }

    @Override
    public void destroy(String skillId) {
        log.debug("User {} destroying skill: {}", userId, skillId);
        runtime.destroy(userId, skillId);
    }

    @Override
    public SkillLifecycleState getStatus(String skillId) {
        return runtime.getState(userId, skillId);
    }

    // ==================== 调用执行 ====================

    @Override
    public Object invoke(String skillId, String capability, Map<String, Object> params) {
        log.debug("User {} invoking skill: {}, capability: {}", userId, skillId, capability);
        return runtime.invoke(userId, skillId, capability, params);
    }

    @Override
    public CompletableFuture<Object> invokeAsync(String skillId, String capability, Map<String, Object> params) {
        log.debug("User {} async invoking skill: {}, capability: {}", userId, skillId, capability);
        return runtime.invokeAsync(userId, skillId, capability, params);
    }

    @Override
    public List<SkillRuntime.InvokeResult> batchInvoke(List<SkillRuntime.InvokeRequest> requests) {
        log.debug("User {} batch invoking {} requests", userId, requests.size());
        return runtime.batchInvoke(userId, requests);
    }

    // ==================== 查询 ====================

    @Override
    public SkillRuntime.SkillInfo getSkill(String skillId) {
        return runtime.getSkill(userId, skillId);
    }

    @Override
    public List<SkillRuntime.SkillInfo> listSkills() {
        return runtime.listSkills(userId);
    }

    // ==================== 用户管理 ====================

    /**
     * 用户登出时调用
     * 清理该用户的所有Skill资源
     */
    public void logout() {
        log.info("User {} logging out, cleaning up skills", userId);
        runtime.cleanupUser(userId);
    }

    /**
     * 获取用户ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 获取会话ID
     */
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public String toString() {
        return "UserSkillClient{" +
                "userId='" + userId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
