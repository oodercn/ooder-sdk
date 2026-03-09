package net.ooder.sdk.llm.installation;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 安装上下文
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallationContext {

    /**
     * 安装ID
     */
    private String installId;

    /**
     * 场景ID
     */
    private String sceneId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 安装状态
     */
    @Builder.Default
    private InstallationStatus status = InstallationStatus.PENDING;

    /**
     * 安装步骤列表
     */
    @Builder.Default
    private List<InstallationStep> steps = new ArrayList<>();

    /**
     * 变量
     */
    @Builder.Default
    private Map<String, Object> variables = new HashMap<>();

    /**
     * 检查点列表
     */
    @Builder.Default
    private List<Checkpoint> checkpoints = new ArrayList<>();

    /**
     * 创建时间
     */
    @Builder.Default
    private long createdAt = System.currentTimeMillis();

    /**
     * 更新时间
     */
    @Builder.Default
    private long updatedAt = System.currentTimeMillis();

    /**
     * 完成时间
     */
    private long completedAt;

    /**
     * 元数据
     */
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();

    /**
     * 获取当前步骤
     */
    public InstallationStep getCurrentStep() {
        for (InstallationStep step : steps) {
            if (step.getStatus() == InstallationStep.StepStatus.IN_PROGRESS) {
                return step;
            }
        }
        // 返回第一个待执行的步骤
        for (InstallationStep step : steps) {
            if (step.getStatus() == InstallationStep.StepStatus.PENDING) {
                return step;
            }
        }
        return null;
    }

    /**
     * 获取步骤
     */
    public InstallationStep getStep(String stepId) {
        for (InstallationStep step : steps) {
            if (step.getStepId().equals(stepId)) {
                return step;
            }
        }
        return null;
    }

    /**
     * 添加步骤
     */
    public void addStep(InstallationStep step) {
        steps.add(step);
        updatedAt = System.currentTimeMillis();
    }

    /**
     * 添加检查点
     */
    public void addCheckpoint(Checkpoint checkpoint) {
        checkpoints.add(checkpoint);
        updatedAt = System.currentTimeMillis();
    }

    /**
     * 获取指定步骤的检查点
     */
    public Checkpoint getCheckpoint(String stepId) {
        for (Checkpoint cp : checkpoints) {
            if (cp.getStepId().equals(stepId)) {
                return cp;
            }
        }
        return null;
    }

    /**
     * 获取最新的检查点
     */
    public Checkpoint getLatestCheckpoint() {
        if (checkpoints.isEmpty()) {
            return null;
        }
        return checkpoints.get(checkpoints.size() - 1);
    }

    /**
     * 设置变量
     */
    public void setVariable(String key, Object value) {
        variables.put(key, value);
        updatedAt = System.currentTimeMillis();
    }

    /**
     * 获取变量
     */
    public Object getVariable(String key) {
        return variables.get(key);
    }

    /**
     * 标记为完成
     */
    public void markCompleted() {
        this.status = InstallationStatus.COMPLETED;
        this.completedAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    /**
     * 标记为取消
     */
    public void markCancelled() {
        this.status = InstallationStatus.CANCELLED;
        this.updatedAt = System.currentTimeMillis();
    }

    /**
     * 标记为失败
     */
    public void markFailed() {
        this.status = InstallationStatus.FAILED;
        this.updatedAt = System.currentTimeMillis();
    }

    /**
     * 计算进度百分比
     */
    public int getProgressPercentage() {
        if (steps.isEmpty()) {
            return 0;
        }

        int completed = 0;
        for (InstallationStep step : steps) {
            if (step.getStatus() == InstallationStep.StepStatus.COMPLETED ||
                    step.getStatus() == InstallationStep.StepStatus.SKIPPED) {
                completed++;
            }
        }

        return (completed * 100) / steps.size();
    }
}
