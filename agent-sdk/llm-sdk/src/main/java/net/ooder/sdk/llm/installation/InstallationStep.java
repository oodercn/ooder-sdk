package net.ooder.sdk.llm.installation;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * 安装步骤
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallationStep {

    /**
     * 步骤ID
     */
    private String stepId;

    /**
     * 步骤名称
     */
    private String name;

    /**
     * 步骤描述
     */
    private String description;

    /**
     * 步骤顺序
     */
    private int order;

    /**
     * 步骤状态
     */
    @Builder.Default
    private StepStatus status = StepStatus.PENDING;

    /**
     * 开始时间
     */
    private long startTime;

    /**
     * 结束时间
     */
    private long endTime;

    /**
     * 执行结果
     */
    private Object result;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 步骤变量
     */
    private Map<String, Object> variables;

    /**
     * 步骤类型
     */
    private String stepType;

    /**
     * 步骤状态枚举
     */
    public enum StepStatus {
        PENDING,        // 待执行
        IN_PROGRESS,    // 执行中
        COMPLETED,      // 已完成
        FAILED,         // 失败
        SKIPPED         // 已跳过
    }

    /**
     * 标记为开始
     */
    public void markStarted() {
        this.status = StepStatus.IN_PROGRESS;
        this.startTime = System.currentTimeMillis();
    }

    /**
     * 标记为完成
     */
    public void markCompleted(Object result) {
        this.status = StepStatus.COMPLETED;
        this.result = result;
        this.endTime = System.currentTimeMillis();
    }

    /**
     * 标记为失败
     */
    public void markFailed(String errorMessage) {
        this.status = StepStatus.FAILED;
        this.errorMessage = errorMessage;
        this.endTime = System.currentTimeMillis();
    }

    /**
     * 标记为跳过
     */
    public void markSkipped() {
        this.status = StepStatus.SKIPPED;
        this.endTime = System.currentTimeMillis();
    }

    /**
     * 获取执行耗时
     */
    public long getExecutionTime() {
        if (startTime > 0 && endTime > 0) {
            return endTime - startTime;
        }
        return 0;
    }
}
