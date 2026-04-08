package net.ooder.scene.discovery.adapter;

/**
 * 发现方法枚举
 *
 * <p>定义所有支持的技能发现方法</p>
 *
 * <h3>发现方法类型：</h3>
 * <ul>
 *   <li>LOCAL - 本地文件系统发现</li>
 *   <li>INDEX - 本地索引发现</li>
 *   <li>GITEE - Gitee 仓库发现</li>
 *   <li>GITHUB - GitHub 仓库发现</li>
 *   <li>P2P_UDP - UDP 广播发现</li>
 *   <li>P2P_MDNS - mDNS 服务发现</li>
 *   <li>SKILL_CENTER - 技能中心发现</li>
 *   <li>HYBRID - 混合模式发现</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public enum DiscoveryMethod {

    LOCAL("本地文件系统", "local"),
    INDEX("本地索引", "index"),
    GITEE("Gitee 仓库", "gitee"),
    GITHUB("GitHub 仓库", "github"),
    P2P_UDP("UDP 广播", "udp"),
    P2P_MDNS("mDNS 发现", "mdns"),
    SKILL_CENTER("技能中心", "skill-center"),
    HYBRID("混合模式", "hybrid");

    private final String displayName;
    private final String code;

    DiscoveryMethod(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCode() {
        return code;
    }

    public static DiscoveryMethod fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (DiscoveryMethod method : values()) {
            if (method.code.equalsIgnoreCase(code)) {
                return method;
            }
        }
        return null;
    }

    public static DiscoveryMethod fromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return LOCAL;
        }
        String lowerUrl = url.toLowerCase();
        if (lowerUrl.contains("gitee.com") || lowerUrl.startsWith("gitee://")) {
            return GITEE;
        }
        if (lowerUrl.contains("github.com") || lowerUrl.startsWith("github://")) {
            return GITHUB;
        }
        return LOCAL;
    }

    public boolean isRemote() {
        return this == GITEE || this == GITHUB || this == SKILL_CENTER;
    }

    public boolean isP2P() {
        return this == P2P_UDP || this == P2P_MDNS;
    }

    public boolean isLocal() {
        return this == LOCAL || this == INDEX;
    }
}
