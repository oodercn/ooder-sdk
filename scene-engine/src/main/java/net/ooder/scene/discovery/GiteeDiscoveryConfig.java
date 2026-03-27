package net.ooder.scene.discovery;

import java.util.ArrayList;
import java.util.List;

/**
 * Gitee 发现服务配置
 *
 * <p>封装 Gitee 发现服务的所有配置项，支持灵活的配置方式。</p>
 *
 * <h3>配置项：</h3>
 * <ul>
 *   <li>token - Gitee API 访问令牌</li>
 *   <li>owner - 仓库所有者</li>
 *   <li>repo - 仓库名称</li>
 *   <li>branch - 分支名称</li>
 *   <li>skillsPath - 技能路径</li>
 *   <li>indexFileName - 索引文件名（默认 skill-index.yaml）</li>
 *   <li>recursive - 是否递归遍历子目录</li>
 *   <li>fallbackIndexFiles - 备选索引文件列表</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class GiteeDiscoveryConfig {

    private String token;
    private String owner;
    private String repo;
    private String branch = "main";
    private String skillsPath;
    private String indexFileName = "skill-index.yaml";
    private boolean recursive = false;
    private List<String> fallbackIndexFiles = new ArrayList<>();
    private long cacheTtl = 3600000;

    public GiteeDiscoveryConfig() {
        fallbackIndexFiles.add("index.yaml");
        fallbackIndexFiles.add("skill-index.yaml");
    }

    public GiteeDiscoveryConfig(String token, String owner, String repo) {
        this();
        this.token = token;
        this.owner = owner;
        this.repo = repo;
    }

    public GiteeDiscoveryConfig(String token, String owner, String repo, String branch, String skillsPath) {
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

    public String getIndexFileName() {
        return indexFileName;
    }

    public void setIndexFileName(String indexFileName) {
        this.indexFileName = indexFileName;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    public List<String> getFallbackIndexFiles() {
        return fallbackIndexFiles;
    }

    public void setFallbackIndexFiles(List<String> fallbackIndexFiles) {
        this.fallbackIndexFiles = fallbackIndexFiles;
    }

    public void addFallbackIndexFile(String fileName) {
        if (fileName != null && !fallbackIndexFiles.contains(fileName)) {
            fallbackIndexFiles.add(fileName);
        }
    }

    public long getCacheTtl() {
        return cacheTtl;
    }

    public void setCacheTtl(long cacheTtl) {
        this.cacheTtl = cacheTtl;
    }

    @Override
    public String toString() {
        return "GiteeDiscoveryConfig{" +
                "owner='" + owner + '\'' +
                ", repo='" + repo + '\'' +
                ", branch='" + branch + '\'' +
                ", skillsPath='" + skillsPath + '\'' +
                ", indexFileName='" + indexFileName + '\'' +
                ", recursive=" + recursive +
                '}';
    }
}
