package net.ooder.scene.service;

import net.ooder.scene.discovery.coordinator.DiscoveryCoordinator;
import net.ooder.scene.skill.coordinator.InstallCoordinator;
import net.ooder.scene.skill.model.RichSkill;
import net.ooder.scene.skill.session.InstallSession;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 统一场景服务接口
 *
 * <p>scene-engine提供的统一服务入口，封装SDK底层接口，为应用层提供简洁的API。</p>
 *
 * <p>设计原则：</p>
 * <ul>
 *   <li>应用层只依赖此接口，不直接依赖SDK</li>
 *   <li>封装平台差异（GitHub/Gitee/Local）</li>
 *   <li>提供统一的服务发现、安装、管理能力</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface UnifiedSceneService {

    /**
     * 发现技能
     *
     * @param source 来源（github/gitee/local/udp/all）
     * @return 发现的技能列表
     */
    CompletableFuture<DiscoveryResult> discoverSkills(String source);

    /**
     * 发现技能（指定仓库）
     *
     * @param owner 仓库所有者
     * @param repo 仓库名称
     * @param options 发现选项
     * @return 发现的技能列表
     */
    CompletableFuture<DiscoveryResult> discoverSkills(String owner, String repo, DiscoveryOptions options);

    /**
     * 搜索技能
     *
     * @param keyword 关键词
     * @return 匹配的技能列表
     */
    CompletableFuture<List<RichSkill>> searchSkills(String keyword);

    /**
     * 获取技能详情
     *
     * @param skillId 技能ID
     * @return 技能详情
     */
    CompletableFuture<RichSkill> getSkillDetail(String skillId);

    /**
     * 安装技能
     *
     * @param skillId 技能ID
     * @return 安装会话ID
     */
    String installSkill(String skillId);

    /**
     * 安装技能（指定版本）
     *
     * @param skillId 技能ID
     * @param version 版本号
     * @return 安装会话ID
     */
    String installSkill(String skillId, String version);

    /**
     * 获取安装进度
     *
     * @param sessionId 安装会话ID
     * @return 进度百分比（0-100）
     */
    int getInstallProgress(String sessionId);

    /**
     * 获取安装状态
     *
     * @param sessionId 安装会话ID
     * @return 安装状态
     */
    InstallSession.InstallState getInstallState(String sessionId);

    /**
     * 暂停安装
     *
     * @param sessionId 安装会话ID
     * @return 是否成功
     */
    boolean pauseInstall(String sessionId);

    /**
     * 恢复安装
     *
     * @param sessionId 安装会话ID
     * @return 是否成功
     */
    boolean resumeInstall(String sessionId);

    /**
     * 取消安装
     *
     * @param sessionId 安装会话ID
     * @return 是否成功
     */
    boolean cancelInstall(String sessionId);

    /**
     * 获取安装报告
     *
     * @param sessionId 安装会话ID
     * @return 安装报告
     */
    InstallSession.InstallReport getInstallReport(String sessionId);

    /**
     * 发现选项
     */
    class DiscoveryOptions {
        private String branch = "main";
        private boolean includePrerelease = false;
        private String versionPattern;

        public String getBranch() { return branch; }
        public void setBranch(String branch) { this.branch = branch; }

        public boolean isIncludePrerelease() { return includePrerelease; }
        public void setIncludePrerelease(boolean includePrerelease) { this.includePrerelease = includePrerelease; }

        public String getVersionPattern() { return versionPattern; }
        public void setVersionPattern(String versionPattern) { this.versionPattern = versionPattern; }

        public static DiscoveryOptions defaults() {
            return new DiscoveryOptions();
        }
    }

    /**
     * 发现结果
     */
    class DiscoveryResult {
        private String source;
        private List<RichSkill> discoveredSkills;
        private int totalCount;
        private String errorMessage;

        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }

        /**
         * 获取发现的技能列表
         * @return 技能列表
         */
        public List<RichSkill> getDiscoveredSkills() { return discoveredSkills; }
        public void setDiscoveredSkills(List<RichSkill> discoveredSkills) { this.discoveredSkills = discoveredSkills; }

        /**
         * 获取发现的技能列表（旧方法，已废弃）
         * @deprecated 请使用 {@link #getDiscoveredSkills()}
         */
        @Deprecated
        public List<RichSkill> getSkills() { return getDiscoveredSkills(); }

        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }

        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }
}
