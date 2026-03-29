package net.ooder.scene.skill.source;

/**
 * 技能安装来源类型枚举
 *
 * <p>定义技能安装的各种来源类型，用于区分技能的获取方式</p>
 *
 * @author Ooder Team
 * @version 3.0
 * @since 3.0.1
 */
public enum InstallSource {

    DOWNLOAD("download", "我下载的"),
    SHARE("share", "别人分享的"),
    DELEGATE("delegate", "领导委派的"),
    PUSH("push", "系统推送的"),
    REGISTRY("registry", "已注册的"),
    DEV("dev", "开发中的"),
    INSTALL("install", "已安装的");

    private final String code;
    private final String displayName;

    InstallSource(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static InstallSource fromCode(String code) {
        if (code == null) {
            return REGISTRY;
        }
        for (InstallSource source : values()) {
            if (source.code.equals(code)) {
                return source;
            }
        }
        return REGISTRY;
    }
}
