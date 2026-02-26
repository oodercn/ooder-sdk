package net.ooder.sdk.lifecycle;

/**
 * 生命周期状态枚举
 */
public enum LifecycleState {
    CREATED("已创建"),
    INITIALIZING("初始化中"),
    INITIALIZED("已初始化"),
    STARTING("启动中"),
    RUNNING("运行中"),
    STOPPING("停止中"),
    STOPPED("已停止"),
    DESTROYING("销毁中"),
    DESTROYED("已销毁"),
    ERROR("错误");

    private final String displayName;

    LifecycleState(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 检查是否处于活动状态
     */
    public boolean isActive() {
        return this == RUNNING || this == STARTING || this == INITIALIZING;
    }

    /**
     * 检查是否已终止
     */
    public boolean isTerminal() {
        return this == STOPPED || this == DESTROYED || this == ERROR;
    }

    /**
     * 检查是否可以转换到目标状态
     */
    public boolean canTransitionTo(LifecycleState targetState) {
        if (targetState == null) {
            return false;
        }

        // 允许相同状态转换
        if (this == targetState) {
            return true;
        }

        // 已终止状态不能转换
        if (this.isTerminal()) {
            return false;
        }

        // 检查状态转换规则
        switch (this) {
            case CREATED:
                return targetState == INITIALIZING || targetState == DESTROYING;
            case INITIALIZING:
                return targetState == INITIALIZED || targetState == ERROR;
            case INITIALIZED:
                return targetState == STARTING || targetState == DESTROYING;
            case STARTING:
                return targetState == RUNNING || targetState == ERROR;
            case RUNNING:
                return targetState == STOPPING || targetState == ERROR;
            case STOPPING:
                return targetState == STOPPED || targetState == ERROR;
            case STOPPED:
                return targetState == STARTING || targetState == DESTROYING;
            case DESTROYING:
                return targetState == DESTROYED || targetState == ERROR;
            default:
                return false;
        }
    }
}
