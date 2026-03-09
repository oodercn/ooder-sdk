package net.ooder.sdk.llm.installation;

import lombok.Getter;

/**
 * 安装状态枚举
 */
@Getter
public enum InstallationStatus {

    PENDING("pending", "待开始"),
    IN_PROGRESS("in_progress", "进行中"),
    CHECKPOINT_SAVED("checkpoint_saved", "检查点已保存"),
    COMPLETED("completed", "已完成"),
    CANCELLED("cancelled", "已取消"),
    FAILED("failed", "失败"),
    RECOVERING("recovering", "恢复中");

    private final String code;
    private final String description;

    InstallationStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取状态
     */
    public static InstallationStatus fromCode(String code) {
        for (InstallationStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return PENDING;
    }

    /**
     * 是否已完成
     */
    public boolean isCompleted() {
        return this == COMPLETED;
    }

    /**
     * 是否已结束（完成、取消或失败）
     */
    public boolean isEnded() {
        return this == COMPLETED || this == CANCELLED || this == FAILED;
    }

    /**
     * 是否进行中
     */
    public boolean isInProgress() {
        return this == IN_PROGRESS || this == CHECKPOINT_SAVED || this == RECOVERING;
    }
}
