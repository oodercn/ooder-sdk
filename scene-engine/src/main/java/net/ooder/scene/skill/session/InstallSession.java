package net.ooder.scene.skill.session;

import net.ooder.scene.skill.model.RichSkill;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 安装会话（状态机）
 *
 * <p>管理Skill安装的生命周期，提供状态控制和恢复能力</p>
 *
 * <p>设计原则：</p>
 * <ul>
 *   <li>有状态：维护安装状态和进度</li>
 *   <li>状态机：PENDING -> INSTALLING -> (PAUSED/FAILED) -> COMPLETED</li>
 *   <li>可恢复：支持暂停后恢复，失败后重试</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class InstallSession {

    private final String sessionId;
    private final RichSkill mainSkill;
    private final List<InstallTask> tasks;
    private InstallState state;
    private final long createTime;
    private long lastUpdateTime;
    private String currentTaskId;
    private String errorMessage;

    // 控制标志
    private final AtomicBoolean paused = new AtomicBoolean(false);
    private final AtomicBoolean cancelled = new AtomicBoolean(false);

    public InstallSession(String sessionId, RichSkill mainSkill) {
        this.sessionId = sessionId;
        this.mainSkill = mainSkill;
        this.tasks = new ArrayList<>();
        this.state = InstallState.PENDING;
        this.createTime = System.currentTimeMillis();
        this.lastUpdateTime = createTime;
    }

    /**
     * 获取会话ID
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * 获取主Skill
     */
    public RichSkill getMainSkill() {
        return mainSkill;
    }

    /**
     * 获取任务列表
     */
    public List<InstallTask> getTasks() {
        return new ArrayList<>(tasks);
    }

    /**
     * 添加任务
     */
    public void addTask(InstallTask task) {
        tasks.add(task);
        updateLastTime();
    }

    /**
     * 获取当前状态
     */
    public InstallState getState() {
        return state;
    }

    /**
     * 更新状态
     */
    private void updateState(InstallState newState) {
        this.state = newState;
        updateLastTime();
    }

    /**
     * 开始安装
     *
     * 状态转换：PENDING -> INSTALLING
     */
    public void start() {
        if (state != InstallState.PENDING) {
            throw new IllegalStateException("Cannot start from state: " + state);
        }
        updateState(InstallState.INSTALLING);
    }

    /**
     * 暂停安装
     *
     * 状态转换：INSTALLING -> PAUSED
     */
    public void pause() {
        if (state != InstallState.INSTALLING) {
            return;
        }
        paused.set(true);
        updateState(InstallState.PAUSED);
    }

    /**
     * 恢复安装
     *
     * 状态转换：PAUSED -> INSTALLING
     */
    public void resume() {
        if (state != InstallState.PAUSED && state != InstallState.FAILED) {
            throw new IllegalStateException("Cannot resume from state: " + state);
        }
        paused.set(false);
        updateState(InstallState.INSTALLING);
    }

    /**
     * 标记任务失败
     *
     * 状态转换：INSTALLING -> FAILED
     */
    public void fail(String errorMessage) {
        this.errorMessage = errorMessage;
        updateState(InstallState.FAILED);
    }

    /**
     * 标记完成
     *
     * 状态转换：INSTALLING -> COMPLETED
     */
    public void complete() {
        updateState(InstallState.COMPLETED);
    }

    /**
     * 取消安装
     *
     * 状态转换：任意 -> CANCELLED
     */
    public void cancel() {
        cancelled.set(true);
        updateState(InstallState.CANCELLED);
    }

    /**
     * 检查是否已暂停
     */
    public boolean isPaused() {
        return paused.get();
    }

    /**
     * 检查是否已取消
     */
    public boolean isCancelled() {
        return cancelled.get();
    }

    /**
     * 检查是否可以恢复
     */
    public boolean canResume() {
        return state == InstallState.PAUSED || state == InstallState.FAILED;
    }

    /**
     * 检查是否可以重试
     */
    public boolean canRetry() {
        return state == InstallState.FAILED;
    }

    /**
     * 获取当前任务ID
     */
    public String getCurrentTaskId() {
        return currentTaskId;
    }

    /**
     * 设置当前任务
     */
    public void setCurrentTaskId(String currentTaskId) {
        this.currentTaskId = currentTaskId;
        updateLastTime();
    }

    /**
     * 获取创建时间
     */
    public long getCreateTime() {
        return createTime;
    }

    /**
     * 获取最后更新时间
     */
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * 获取错误信息
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * 获取进度百分比
     */
    public int getProgressPercent() {
        if (tasks.isEmpty()) {
            return 0;
        }
        long completed = tasks.stream().filter(t -> t.getState() == InstallTask.TaskState.COMPLETED).count();
        return (int) (completed * 100 / tasks.size());
    }

    /**
     * 生成安装报告
     */
    public InstallReport generateReport() {
        InstallReport report = new InstallReport();
        report.setSessionId(sessionId);
        report.setMainSkillId(mainSkill.getSkillId());
        report.setState(state);
        report.setProgressPercent(getProgressPercent());
        report.setCreateTime(createTime);
        report.setCompleteTime(state == InstallState.COMPLETED ? System.currentTimeMillis() : 0);
        report.setTasks(tasks);
        report.setErrorMessage(errorMessage);
        return report;
    }

    private void updateLastTime() {
        this.lastUpdateTime = System.currentTimeMillis();
    }

    /**
     * 安装状态枚举
     */
    public enum InstallState {
        PENDING,      // 等待开始
        INSTALLING,   // 安装中
        PAUSED,       // 已暂停
        FAILED,       // 失败
        COMPLETED,    // 完成
        CANCELLED     // 已取消
    }

    /**
     * 安装任务
     */
    public static class InstallTask {
        private String taskId;
        private String skillId;
        private TaskState state;
        private String errorMessage;
        private long startTime;
        private long endTime;

        public String getTaskId() { return taskId; }
        public void setTaskId(String taskId) { this.taskId = taskId; }
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public TaskState getState() { return state; }
        public void setState(TaskState state) { this.state = state; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        public long getEndTime() { return endTime; }
        public void setEndTime(long endTime) { this.endTime = endTime; }

        public enum TaskState {
            PENDING, INSTALLING, COMPLETED, FAILED
        }
    }

    /**
     * 安装报告
     */
    public static class InstallReport {
        private String sessionId;
        private String mainSkillId;
        private InstallState state;
        private int progressPercent;
        private long createTime;
        private long completeTime;
        private List<InstallTask> tasks;
        private String errorMessage;

        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public String getMainSkillId() { return mainSkillId; }
        public void setMainSkillId(String mainSkillId) { this.mainSkillId = mainSkillId; }
        public InstallState getState() { return state; }
        public void setState(InstallState state) { this.state = state; }
        public int getProgressPercent() { return progressPercent; }
        public void setProgressPercent(int progressPercent) { this.progressPercent = progressPercent; }
        public long getCreateTime() { return createTime; }
        public void setCreateTime(long createTime) { this.createTime = createTime; }
        public long getCompleteTime() { return completeTime; }
        public void setCompleteTime(long completeTime) { this.completeTime = completeTime; }
        public List<InstallTask> getTasks() { return tasks; }
        public void setTasks(List<InstallTask> tasks) { this.tasks = tasks; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }
}
