package net.ooder.scene.skill.coordinator;

import net.ooder.scene.skill.model.RichSkill;
import net.ooder.scene.skill.session.InstallSession;
import net.ooder.skills.api.SkillInstaller;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 安装协调器
 *
 * <p>SceneEngine层核心组件，负责：</p>
 * <ul>
 *   <li>管理安装会话（InstallSession）</li>
 *   <li>协调SDK Installer执行安装</li>
 *   <li>处理安装状态机转换</li>
 *   <li>支持暂停、恢复、重试</li>
 * </ul>
 *
 * <p>设计原则：</p>
 * <ul>
 *   <li>有状态控制：管理安装会话和状态机</li>
 *   <li>聚合器：协调多个SDK Installer调用</li>
 *   <li>编排器：拓扑排序、依赖安装顺序</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class InstallCoordinator {

    private final SkillInstaller skillInstaller;
    private final Map<String, InstallSession> sessions;

    public InstallCoordinator(SkillInstaller skillInstaller) {
        this.skillInstaller = skillInstaller;
        this.sessions = new ConcurrentHashMap<>();
    }

    /**
     * 安装Skill（统一入口）
     *
     * <p>控制逻辑：</p>
     * <ol>
     *   <li>创建InstallSession（状态机）</li>
     *   <li>拓扑排序依赖</li>
     *   <li>逐个调用SDK Installer</li>
     *   <li>更新状态机</li>
     * </ol>
     *
     * @param skill 要安装的RichSkill
     * @return 安装会话ID
     */
    public String install(RichSkill skill) {
        // 创建会话（状态机）
        String sessionId = generateSessionId(skill.getSkillId());
        InstallSession session = new InstallSession(sessionId, skill);
        sessions.put(sessionId, session);

        // 异步执行安装
        CompletableFuture.runAsync(() -> executeInstall(session));

        return sessionId;
    }

    /**
     * 执行安装（内部方法）
     */
    private void executeInstall(InstallSession session) {
        try {
            // 开始安装
            session.start();

            // 获取主Skill
            RichSkill mainSkill = session.getMainSkill();

            // 创建安装计划（拓扑排序）
            RichSkill.InstallPlan plan = mainSkill.createInstallPlan();

            // 添加主Skill任务
            InstallSession.InstallTask mainTask = createTask(mainSkill.getSkillId());
            session.addTask(mainTask);

            // 安装依赖（如果有）
            if (plan.getDependencies() != null) {
                for (RichSkill dep : plan.getDependencies()) {
                    if (session.isCancelled()) {
                        break;
                    }

                    while (session.isPaused()) {
                        Thread.sleep(1000);
                    }

                    InstallSession.InstallTask depTask = createTask(dep.getSkillId());
                    session.addTask(depTask);
                    session.setCurrentTaskId(depTask.getTaskId());

                    // 调用SDK Installer（贫血模型调用）
                    installSkill(dep);

                    depTask.setState(InstallSession.InstallTask.TaskState.COMPLETED);
                    depTask.setEndTime(System.currentTimeMillis());
                }
            }

            // 安装主Skill
            if (!session.isCancelled()) {
                session.setCurrentTaskId(mainTask.getTaskId());
                installSkill(mainSkill);
                mainTask.setState(InstallSession.InstallTask.TaskState.COMPLETED);
                mainTask.setEndTime(System.currentTimeMillis());
            }

            // 完成
            if (!session.isCancelled()) {
                session.complete();
            }

        } catch (Exception e) {
            session.fail(e.getMessage());
        }
    }

    /**
     * 调用SDK Installer（贫血模型）
     */
    private void installSkill(RichSkill skill) throws Exception {
        // 调用SDK层Installer进行实际安装
        // 注意：需要外部注入具体的 SkillInstaller 实现
        // skillInstaller.install(skill.getRawPackage(), SkillInstaller.InstallMode.DEFAULT);
        
        // 当前为简化实现，模拟安装耗时
        // 实际项目中应该调用：skillInstaller.install(skillPackage, installMode)
        Thread.sleep(1000);
    }

    /**
     * 暂停安装
     *
     * @param sessionId 会话ID
     * @return 是否成功
     */
    public boolean pause(String sessionId) {
        InstallSession session = sessions.get(sessionId);
        if (session == null) {
            return false;
        }
        session.pause();
        return true;
    }

    /**
     * 恢复安装
     *
     * @param sessionId 会话ID
     * @return 是否成功
     */
    public boolean resume(String sessionId) {
        InstallSession session = sessions.get(sessionId);
        if (session == null || !session.canResume()) {
            return false;
        }

        session.resume();

        // 重新执行安装
        CompletableFuture.runAsync(() -> executeInstall(session));
        return true;
    }

    /**
     * 取消安装
     *
     * @param sessionId 会话ID
     * @return 是否成功
     */
    public boolean cancel(String sessionId) {
        InstallSession session = sessions.get(sessionId);
        if (session == null) {
            return false;
        }
        session.cancel();
        return true;
    }

    /**
     * 重试安装
     *
     * @param sessionId 会话ID
     * @return 是否成功
     */
    public boolean retry(String sessionId) {
        InstallSession session = sessions.get(sessionId);
        if (session == null || !session.canRetry()) {
            return false;
        }

        session.resume();

        // 重新执行安装
        CompletableFuture.runAsync(() -> executeInstall(session));
        return true;
    }

    /**
     * 获取安装进度
     *
     * @param sessionId 会话ID
     * @return 进度百分比（0-100）
     */
    public int getProgress(String sessionId) {
        InstallSession session = sessions.get(sessionId);
        if (session == null) {
            return 0;
        }
        return session.getProgressPercent();
    }

    /**
     * 获取安装状态
     *
     * @param sessionId 会话ID
     * @return 安装状态
     */
    public InstallSession.InstallState getState(String sessionId) {
        InstallSession session = sessions.get(sessionId);
        if (session == null) {
            return null;
        }
        return session.getState();
    }

    /**
     * 获取安装报告
     *
     * @param sessionId 会话ID
     * @return 安装报告
     */
    public InstallSession.InstallReport getReport(String sessionId) {
        InstallSession session = sessions.get(sessionId);
        if (session == null) {
            return null;
        }
        return session.generateReport();
    }

    /**
     * 获取所有会话
     *
     * @return 会话列表
     */
    public List<InstallSession> getAllSessions() {
        return new ArrayList<>(sessions.values());
    }

    /**
     * 获取活跃会话
     *
     * @return 活跃会话列表
     */
    public List<InstallSession> getActiveSessions() {
        return sessions.values().stream()
            .filter(s -> s.getState() == InstallSession.InstallState.INSTALLING
                || s.getState() == InstallSession.InstallState.PAUSED)
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 清理已完成会话
     */
    public void cleanupCompletedSessions() {
        sessions.entrySet().removeIf(entry -> {
            InstallSession.InstallState state = entry.getValue().getState();
            return state == InstallSession.InstallState.COMPLETED
                || state == InstallSession.InstallState.CANCELLED;
        });
    }

    /**
     * 生成会话ID
     */
    private String generateSessionId(String skillId) {
        return skillId + "_" + System.currentTimeMillis();
    }

    /**
     * 创建安装任务
     */
    private InstallSession.InstallTask createTask(String skillId) {
        InstallSession.InstallTask task = new InstallSession.InstallTask();
        task.setTaskId(UUID.randomUUID().toString());
        task.setSkillId(skillId);
        task.setState(InstallSession.InstallTask.TaskState.PENDING);
        task.setStartTime(System.currentTimeMillis());
        return task;
    }
}
