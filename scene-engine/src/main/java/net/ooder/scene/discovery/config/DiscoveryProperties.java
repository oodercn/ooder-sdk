package net.ooder.scene.discovery.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 发现服务配置属性
 *
 * @author ooder
 * @since 2.3.1
 */
@ConfigurationProperties(prefix = "scene.engine.discovery")
public class DiscoveryProperties {

    private boolean enabled = true;
    private GiteeConfig gitee = new GiteeConfig();
    private GithubConfig github = new GithubConfig();
    private CacheConfig cache = new CacheConfig();

    public static class GiteeConfig {
        private boolean enabled = true;
        private String token;
        private String defaultOwner = "ooderCN";
        private String defaultRepo = "skills";
        private String defaultBranch = "main";
        private String skillsPath = "";
        private long cacheTtlMs = 3600000;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getDefaultOwner() {
            return defaultOwner;
        }

        public void setDefaultOwner(String defaultOwner) {
            this.defaultOwner = defaultOwner;
        }

        public String getDefaultRepo() {
            return defaultRepo;
        }

        public void setDefaultRepo(String defaultRepo) {
            this.defaultRepo = defaultRepo;
        }

        public String getDefaultBranch() {
            return defaultBranch;
        }

        public void setDefaultBranch(String defaultBranch) {
            this.defaultBranch = defaultBranch;
        }

        public String getSkillsPath() {
            return skillsPath;
        }

        public void setSkillsPath(String skillsPath) {
            this.skillsPath = skillsPath;
        }

        public long getCacheTtlMs() {
            return cacheTtlMs;
        }

        public void setCacheTtlMs(long cacheTtlMs) {
            this.cacheTtlMs = cacheTtlMs;
        }
    }

    public static class GithubConfig {
        private boolean enabled = false;
        private String token;
        private String defaultOwner;
        private String defaultRepo;
        private long cacheTtlMs = 3600000;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getDefaultOwner() {
            return defaultOwner;
        }

        public void setDefaultOwner(String defaultOwner) {
            this.defaultOwner = defaultOwner;
        }

        public String getDefaultRepo() {
            return defaultRepo;
        }

        public void setDefaultRepo(String defaultRepo) {
            this.defaultRepo = defaultRepo;
        }

        public long getCacheTtlMs() {
            return cacheTtlMs;
        }

        public void setCacheTtlMs(long cacheTtlMs) {
            this.cacheTtlMs = cacheTtlMs;
        }
    }

    public static class CacheConfig {
        private boolean enabled = true;
        private long ttlMs = 3600000;
        private String dir = "./.ooder/cache/discovery";
        private int maxEntries = 100;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public long getTtlMs() {
            return ttlMs;
        }

        public void setTtlMs(long ttlMs) {
            this.ttlMs = ttlMs;
        }

        public String getDir() {
            return dir;
        }

        public void setDir(String dir) {
            this.dir = dir;
        }

        public int getMaxEntries() {
            return maxEntries;
        }

        public void setMaxEntries(int maxEntries) {
            this.maxEntries = maxEntries;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public GiteeConfig getGitee() {
        return gitee;
    }

    public void setGitee(GiteeConfig gitee) {
        this.gitee = gitee;
    }

    public GithubConfig getGithub() {
        return github;
    }

    public void setGithub(GithubConfig github) {
        this.github = github;
    }

    public CacheConfig getCache() {
        return cache;
    }

    public void setCache(CacheConfig cache) {
        this.cache = cache;
    }
}
