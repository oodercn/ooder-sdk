package net.ooder.scene.discovery;

import java.util.ArrayList;
import java.util.List;

/**
 * GitHub 发现服务配置
 *
 * <p>封装 GitHub 发现服务的所有配置项，支持灵活的配置方式。</p>
 *
 * <h3>配置项：</h3>
 * <ul>
 *   <li>token - GitHub API 访问令牌</li>
 *   <li>owner - 仓库所有者</li>
 *   <li>repo - 仓库名称</li>
 *   <li>branch - 分支名称</li>
 *   <li>skillsPath - 技能路径</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 3.0.2
 * @since 3.0.2
 */
public class GithubDiscoveryConfig {

    private String token;
    private String owner;
    private String repo;
    private String branch = "main";
    private String skillsPath;
    private long cacheTtl = 3600000;

    public GithubDiscoveryConfig() {
    }

    public GithubDiscoveryConfig(String token, String owner, String repo) {
        this.token = token;
        this.owner = owner;
        this.repo = repo;
    }

    public GithubDiscoveryConfig(String token, String owner, String repo, String branch, String skillsPath) {
        this(token, owner, repo);
        this.branch = branch != null ? branch : "main";
        this.skillsPath = skillsPath;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getSkillsPath() {
        return skillsPath;
    }

    public void setSkillsPath(String skillsPath) {
        this.skillsPath = skillsPath;
    }

    public long getCacheTtl() {
        return cacheTtl;
    }

    public void setCacheTtl(long cacheTtl) {
        this.cacheTtl = cacheTtl;
    }

    @Override
    public String toString() {
        return "GithubDiscoveryConfig{" +
                "owner='" + owner + '\'' +
                ", repo='" + repo + '\'' +
                ", branch='" + branch + '\'' +
                ", skillsPath='" + skillsPath + '\'' +
                '}';
    }
}
