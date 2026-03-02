package net.ooder.scene.discovery.storage;

import net.ooder.engine.ConnectInfo;

/**
 * VFS路径规划策略
 * 
 * 定义发现模块在VFS中的存储路径结构：
 * - 本地缓存：/discovery/cache/{source}/{skillId}/{version}/
 * - 用户存储：/discovery/user/{userId}/{source}/{skillId}/{version}/
 * - 安装包：/discovery/packages/{skillId}/{version}/
 * - 元数据：/discovery/metadata/{type}/
 * - 历史记录：/discovery/history/
 * - 安装状态：/discovery/install/{installId}/
 * 
 * @author ooder Team
 * @since 2.3
 */
public class VfsPathStrategy {

    private static final String BASE_PATH = "/discovery";
    private static final String CACHE_PATH = BASE_PATH + "/cache";
    private static final String USER_PATH = BASE_PATH + "/user";
    private static final String PACKAGE_PATH = BASE_PATH + "/packages";
    private static final String METADATA_PATH = BASE_PATH + "/metadata";
    private static final String HISTORY_PATH = BASE_PATH + "/history";
    private static final String INSTALL_PATH = BASE_PATH + "/install";

    /**
     * 获取本地缓存路径
     * 
     * @param source 来源（github/gitee/local/udp/skillcenter）
     * @param skillId 技能ID
     * @param version 版本
     * @return VFS路径
     */
    public static String getCachePath(String source, String skillId, String version) {
        return String.format("%s/%s/%s/%s", CACHE_PATH, sanitize(source), sanitize(skillId), sanitize(version));
    }

    /**
     * 获取用户存储路径
     * 
     * @param userId 用户ID
     * @param source 来源
     * @param skillId 技能ID
     * @param version 版本
     * @return VFS路径
     */
    public static String getUserPath(String userId, String source, String skillId, String version) {
        return String.format("%s/%s/%s/%s/%s", USER_PATH, sanitize(userId), sanitize(source), sanitize(skillId), sanitize(version));
    }

    /**
     * 获取用户发现配置路径
     * 
     * @param userId 用户ID
     * @return VFS路径
     */
    public static String getUserConfigPath(String userId) {
        return String.format("%s/%s/config", USER_PATH, sanitize(userId));
    }

    /**
     * 获取用户ConnectInfo存储路径
     * 
     * @param userId 用户ID
     * @param source 来源（github/gitee）
     * @return VFS路径
     */
    public static String getConnectInfoPath(String userId, String source) {
        return String.format("%s/%s/connect/%s", USER_PATH, sanitize(userId), sanitize(source));
    }

    /**
     * 获取安装包路径
     * 
     * @param skillId 技能ID
     * @param version 版本
     * @return VFS路径
     */
    public static String getPackagePath(String skillId, String version) {
        return String.format("%s/%s/%s", PACKAGE_PATH, sanitize(skillId), sanitize(version));
    }

    /**
     * 获取技能元数据路径
     * 
     * @param skillId 技能ID
     * @return VFS路径
     */
    public static String getSkillMetadataPath(String skillId) {
        return String.format("%s/skills/%s", METADATA_PATH, sanitize(skillId));
    }

    /**
     * 获取发现历史路径
     * 
     * @return VFS路径
     */
    public static String getHistoryPath() {
        return HISTORY_PATH;
    }

    /**
     * 获取特定历史记录路径
     * 
     * @param historyId 历史记录ID
     * @return VFS路径
     */
    public static String getHistoryItemPath(String historyId) {
        return String.format("%s/%s.json", HISTORY_PATH, sanitize(historyId));
    }

    /**
     * 获取安装任务路径
     * 
     * @param installId 安装任务ID
     * @return VFS路径
     */
    public static String getInstallPath(String installId) {
        return String.format("%s/%s", INSTALL_PATH, sanitize(installId));
    }

    /**
     * 获取安装状态文件路径
     * 
     * @param installId 安装任务ID
     * @return VFS路径
     */
    public static String getInstallStatusPath(String installId) {
        return String.format("%s/%s/status.json", INSTALL_PATH, sanitize(installId));
    }

    /**
     * 获取安装进度文件路径
     * 
     * @param installId 安装任务ID
     * @return VFS路径
     */
    public static String getInstallProgressPath(String installId) {
        return String.format("%s/%s/progress.json", INSTALL_PATH, sanitize(installId));
    }

    /**
     * 获取安装日志路径
     * 
     * @param installId 安装任务ID
     * @return VFS路径
     */
    public static String getInstallLogPath(String installId) {
        return String.format("%s/%s/install.log", INSTALL_PATH, sanitize(installId));
    }

    /**
     * 获取多仓库配置路径
     * 
     * @param source 来源（github/gitee）
     * @return VFS路径
     */
    public static String getMultiRepoConfigPath(String source) {
        return String.format("%s/repos/%s/config.json", METADATA_PATH, sanitize(source));
    }

    /**
     * 获取技能清单路径
     * 
     * @param source 来源
     * @return VFS路径
     */
    public static String getSkillManifestPath(String source, String skillId, String version) {
        return String.format("%s/%s/%s/%s/skill-manifest.yaml", 
                CACHE_PATH, sanitize(source), sanitize(skillId), sanitize(version));
    }

    /**
     * 获取统一技能索引路径
     * 
     * @return VFS路径
     */
    public static String getUnifiedSkillIndexPath() {
        return String.format("%s/unified/skills.json", METADATA_PATH);
    }

    /**
     * 获取去重映射路径
     * 
     * @return VFS路径
     */
    public static String getDeduplicationMapPath() {
        return String.format("%s/unified/dedup.json", METADATA_PATH);
    }

    /**
     * 获取来源优先级配置路径
     * 
     * @return VFS路径
     */
    public static String getSourcePriorityPath() {
        return String.format("%s/unified/priority.json", METADATA_PATH);
    }

    /**
     * 获取完整性检查报告路径
     * 
     * @param skillId 技能ID
     * @param version 版本
     * @return VFS路径
     */
    public static String getIntegrityReportPath(String skillId, String version) {
        return String.format("%s/integrity/%s/%s.json", METADATA_PATH, sanitize(skillId), sanitize(version));
    }

    /**
     * 获取依赖树路径
     * 
     * @param skillId 技能ID
     * @param version 版本
     * @return VFS路径
     */
    public static String getDependencyTreePath(String skillId, String version) {
        return String.format("%s/dependencies/%s/%s/tree.json", METADATA_PATH, sanitize(skillId), sanitize(version));
    }

    /**
     * 获取已安装技能注册表路径
     * 
     * @return VFS路径
     */
    public static String getInstalledRegistryPath() {
        return String.format("%s/installed/registry.json", METADATA_PATH);
    }

    /**
     * 根据ConnectInfo获取用户存储根路径
     * 
     * @param connectInfo 连接信息
     * @return VFS路径
     */
    public static String getUserRootPath(ConnectInfo connectInfo) {
        if (connectInfo == null || connectInfo.getUserID() == null) {
            return CACHE_PATH;
        }
        return String.format("%s/%s", USER_PATH, sanitize(connectInfo.getUserID()));
    }

    /**
     * 获取临时下载路径
     * 
     * @param skillId 技能ID
     * @param version 版本
     * @return VFS路径
     */
    public static String getTempDownloadPath(String skillId, String version) {
        return String.format("%s/temp/%s/%s", BASE_PATH, sanitize(skillId), sanitize(version));
    }

    /**
     * 清理路径中的非法字符
     * 
     * @param path 原始路径
     * @return 清理后的路径
     */
    private static String sanitize(String path) {
        if (path == null) {
            return "unknown";
        }
        return path.replaceAll("[^a-zA-Z0-9_\\-\\.]", "_");
    }

    /**
     * 路径类型枚举
     */
    public enum PathType {
        CACHE,      // 本地缓存
        USER,       // 用户存储
        PACKAGE,    // 安装包
        METADATA,   // 元数据
        HISTORY,    // 历史记录
        INSTALL     // 安装任务
    }

    /**
     * 获取指定类型的基础路径
     * 
     * @param type 路径类型
     * @return VFS路径
     */
    public static String getBasePath(PathType type) {
        switch (type) {
            case CACHE:
                return CACHE_PATH;
            case USER:
                return USER_PATH;
            case PACKAGE:
                return PACKAGE_PATH;
            case METADATA:
                return METADATA_PATH;
            case HISTORY:
                return HISTORY_PATH;
            case INSTALL:
                return INSTALL_PATH;
            default:
                return BASE_PATH;
        }
    }
}
