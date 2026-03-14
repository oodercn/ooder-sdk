package net.ooder.scene.core.install;

/**
 * 安装状态枚举
 *
 * @author Ooder Team
 * @since 2.3.1
 */
public enum InstallStatus {
    NOT_STARTED("not_started", "未开始"),
    IN_PROGRESS("in_progress", "进行中"),
    COMPLETED("completed", "已完成"),
    FAILED("failed", "失败");

    private final String code;
    private final String name;

    InstallStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
