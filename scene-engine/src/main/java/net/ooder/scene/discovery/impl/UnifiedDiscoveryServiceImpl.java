package net.ooder.scene.discovery.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import net.ooder.scene.discovery.UnifiedDiscoveryService;
import net.ooder.scene.discovery.cache.JsonFileCacheManager;
import net.ooder.skills.api.SkillPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 统一发现服务实现
 *
 * <p>支持Gitee和GitHub的技能发现</p>
 *
 * <h3>支持的 skill-index.yaml 格式：</h3>
 * <pre>
 * 格式1：直接 skills 列表（旧格式）
 * skills:
 *   - id: skill-xxx
 *     name: 技能名称
 *
 * 格式2：includes 引用（v2.3.1 标准格式）
 * apiVersion: ooder.io/v1
 * kind: SkillIndex
 * spec:
 *   includes:
 *     - skills/*.yaml
 *     - scenes/*.yaml
 * </pre>
 *
 * <h3>路径拼接规则：</h3>
 * <pre>
 * 仓库结构：
 * ooderCN/skills/           # 仓库 (repo=skills)
 * ├── skill-index.yaml      # 索引文件（根目录）
 * ├── skills/               # 技能子目录
 *
 * 配置示例：
 * - skillsPath=""          → 获取 skill-index.yaml (根目录)
 * - skillsPath="skills"    → 获取 skills/skill-index.yaml
 * </pre>
 *
 * @author ooder
 * @since 2.3.1
 */
public class UnifiedDiscoveryServiceImpl implements UnifiedDiscoveryService {

    private static final Logger logger = LoggerFactory.getLogger(UnifiedDiscoveryServiceImpl.class);

    private final Map<String, Object> giteeConfig = new ConcurrentHashMap<>();
    private final Map<String, Object> githubConfig = new ConcurrentHashMap<>();
    private JsonFileCacheManager cacheManager;
    private long giteeCacheTtl = 3600000;
    private long githubCacheTtl = 3600000;
    
    private static final String GITEE_API_BASE = "https://gitee.com/api/v5";
    private static final String GITHUB_API_BASE = "https://api.github.com";

    public UnifiedDiscoveryServiceImpl() {
        this.cacheManager = new JsonFileCacheManager();
    }

    /**
     * 配置Gitee
     */
    public void configureGitee(String token, String owner, String repo, String branch, String skillsPath) {
        giteeConfig.put("token", token);
        giteeConfig.put("owner", owner);
        giteeConfig.put("repo", repo);
        giteeConfig.put("branch", branch != null ? branch : "main");
        giteeConfig.put("skillsPath", normalizePath(skillsPath));
        giteeConfig.put("_currentPlatform", "gitee");
        
        logger.info("Gitee configured: owner={}, repo={}, branch={}, skillsPath={}", 
            owner, repo, branch, skillsPath);
    }

    /**
     * 配置GitHub
     */
    public void configureGithub(String token, String owner, String repo) {
        githubConfig.put("token", token);
        githubConfig.put("owner", owner);
        githubConfig.put("repo", repo);
        githubConfig.put("_currentPlatform", "github");
        
        logger.info("GitHub configured: owner={}, repo={}", owner, repo);
    }

    /**
     * 设置Gitee缓存TTL
     */
    public void setGiteeCacheTtl(long ttlMs) {
        this.giteeCacheTtl = ttlMs;
    }

    /**
     * 设置GitHub缓存TTL
     */
    public void setGithubCacheTtl(long ttlMs) {
        this.githubCacheTtl = ttlMs;
    }

    /**
     * 设置缓存配置
     */
    public void setCacheConfig(String dir, long ttlMs, int maxEntries) {
        this.cacheManager = new JsonFileCacheManager(dir, maxEntries);
        logger.info("Cache configured: dir={}, ttl={}ms, maxEntries={}", dir, ttlMs, maxEntries);
    }

    @Override
    public CompletableFuture<List<SkillPackage>> discoverSkills(String repositoryUrl) {
        return discoverSkills(repositoryUrl, null);
    }

    @Override
    public CompletableFuture<List<SkillPackage>> discoverSkills(String repositoryUrl, String skillsPath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Discovering skills from: {}", repositoryUrl);
                
                if (repositoryUrl.contains("gitee.com")) {
                    return discoverFromGitee(repositoryUrl, skillsPath);
                } else if (repositoryUrl.contains("github.com")) {
                    return discoverFromGithub(repositoryUrl, skillsPath);
                } else {
                    logger.warn("Unsupported repository URL: {}", repositoryUrl);
                    return new ArrayList<>();
                }
            } catch (Exception e) {
                logger.error("Failed to discover skills from: " + repositoryUrl, e);
                return new ArrayList<>();
            }
        });
    }

    @Override
    public CompletableFuture<SkillPackage> discoverSkill(String repositoryUrl, String skillName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<SkillPackage> skills = discoverSkills(repositoryUrl).get();
                return skills.stream()
                    .filter(skill -> skillName.equals(skill.getName()))
                    .findFirst()
                    .orElse(null);
            } catch (Exception e) {
                logger.error("Failed to discover skill: " + skillName, e);
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<String> getSkillManifest(String repositoryUrl, String skillName) {
        return CompletableFuture.supplyAsync(() -> {
            return "";
        });
    }

    @Override
    public CompletableFuture<List<ReleaseInfo>> getReleases(String repositoryUrl) {
        return CompletableFuture.supplyAsync(() -> {
            return new ArrayList<>();
        });
    }

    @Override
    public CompletableFuture<ReleaseInfo> getLatestRelease(String repositoryUrl) {
        return CompletableFuture.supplyAsync(() -> {
            return null;
        });
    }

    @Override
    public CompletableFuture<Boolean> refreshCache(String repositoryUrl) {
        return CompletableFuture.supplyAsync(() -> {
            cacheManager.invalidate(repositoryUrl);
            return true;
        });
    }

    @Override
    public void clearAllCache() {
        cacheManager.clearAll();
        logger.info("All discovery cache cleared");
    }

    @Override
    public CacheStatus getCacheStatus(String repositoryUrl) {
        CacheStatus status = new CacheStatus();
        status.setCached(cacheManager.exists(repositoryUrl));
        return status;
    }

    @Override
    public void setCacheConfig(CacheConfig config) {
        this.cacheManager = new JsonFileCacheManager(
            config.getCacheDir(),
            config.getMaxCacheEntries()
        );
    }

    private List<SkillPackage> discoverFromGitee(String repositoryUrl, String skillsPath) {
        try {
            String owner = (String) giteeConfig.getOrDefault("owner", extractOwner(repositoryUrl));
            String repo = (String) giteeConfig.getOrDefault("repo", extractRepo(repositoryUrl));
            String token = (String) giteeConfig.get("token");
            String branch = (String) giteeConfig.getOrDefault("branch", "main");
            String basePath = skillsPath != null ? normalizePath(skillsPath) : (String) giteeConfig.get("skillsPath");
            
            String cacheKey = buildCacheKey("gitee", owner, repo, basePath);
            
            if (cacheManager.exists(cacheKey)) {
                logger.info("Using cached skills for: {}/{}", owner, repo);
                return cacheManager.get(cacheKey);
            }
            
            logger.info("Discovering from Gitee: owner={}, repo={}, branch={}, basePath={}", 
                owner, repo, branch, basePath);
            
            List<SkillPackage> skills = fetchSkillsFromGitee(owner, repo, branch, basePath, token);
            
            cacheManager.put(cacheKey, skills, giteeCacheTtl);
            logger.info("Discovered {} skills from Gitee", skills.size());
            
            return skills;
        } catch (Exception e) {
            logger.error("Failed to discover from Gitee: " + repositoryUrl, e);
            return new ArrayList<>();
        }
    }

    private List<SkillPackage> fetchSkillsFromGitee(String owner, String repo, String branch, 
            String basePath, String token) {
        try {
            String indexPath = buildIndexPath(basePath);
            String indexUrl = String.format(
                "https://gitee.com/api/v5/repos/%s/%s/contents/%s?ref=%s",
                owner, repo, indexPath, branch
            );
            
            if (token != null && !token.isEmpty()) {
                indexUrl += "&access_token=" + token;
            }
            
            logger.debug("Fetching skill-index from: {}", indexUrl.replaceAll("access_token=[^&]+", "access_token=***"));
            
            String jsonResponse = fetchUrlContent(indexUrl);
            if (jsonResponse == null) {
                logger.warn("skill-index.yaml not found at path: {}", indexPath);
                return new ArrayList<>();
            }
            
            String yamlContent = decodeGiteeContent(jsonResponse);
            if (yamlContent == null) {
                logger.warn("Failed to decode skill-index.yaml content from Gitee");
                return new ArrayList<>();
            }
            
            return parseSkillIndex(yamlContent);
            
        } catch (Exception e) {
            logger.error("Failed to fetch skills from Gitee: {}/{} - {}", owner, repo, e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @SuppressWarnings("unchecked")
    private String decodeGiteeContent(String jsonResponse) {
        try {
            JSONObject responseMap = JSON.parseObject(jsonResponse);
            
            String encoding = responseMap.getString("encoding");
            String content = responseMap.getString("content");
            
            if (content == null) {
                logger.error("Gitee API response missing 'content' field");
                return null;
            }
            
            if ("base64".equals(encoding)) {
                content = content.replace("\n", "").replace("\r", "");
                byte[] decodedBytes = Base64.getDecoder().decode(content);
                return new String(decodedBytes, StandardCharsets.UTF_8);
            }
            
            return content;
            
        } catch (Exception e) {
            logger.error("Failed to decode Gitee content: {}", e.getMessage());
            return null;
        }
    }

    private List<SkillPackage> discoverFromGithub(String repositoryUrl, String skillsPath) {
        try {
            String owner = (String) githubConfig.getOrDefault("owner", extractOwner(repositoryUrl));
            String repo = (String) githubConfig.getOrDefault("repo", extractRepo(repositoryUrl));
            String token = (String) githubConfig.get("token");
            String basePath = skillsPath != null ? normalizePath(skillsPath) : "";
            
            String cacheKey = buildCacheKey("github", owner, repo, basePath);
            
            if (cacheManager.exists(cacheKey)) {
                logger.info("Using cached skills for: {}/{}", owner, repo);
                return cacheManager.get(cacheKey);
            }
            
            logger.info("Discovering from GitHub: owner={}, repo={}, basePath={}", owner, repo, basePath);
            
            List<SkillPackage> skills = fetchSkillsFromGithub(owner, repo, basePath, token);
            
            cacheManager.put(cacheKey, skills, githubCacheTtl);
            logger.info("Discovered {} skills from GitHub", skills.size());
            
            return skills;
        } catch (Exception e) {
            logger.error("Failed to discover from GitHub: " + repositoryUrl, e);
            return new ArrayList<>();
        }
    }

    private List<SkillPackage> fetchSkillsFromGithub(String owner, String repo, 
            String basePath, String token) {
        try {
            String indexPath = buildIndexPath(basePath);
            String indexUrl = String.format(
                "https://api.github.com/repos/%s/%s/contents/%s",
                owner, repo, indexPath
            );
            
            logger.debug("Fetching skill-index from: {}", indexUrl);
            
            Map<String, String> headers = new HashMap<>();
            if (token != null && !token.isEmpty()) {
                headers.put("Authorization", "token " + token);
            }
            headers.put("Accept", "application/vnd.github.v3.raw");
            
            String content = fetchUrlContentWithHeaders(indexUrl, headers);
            if (content == null) {
                logger.warn("skill-index.yaml not found at path: {}", indexPath);
                return new ArrayList<>();
            }
            
            return parseSkillIndex(content);
            
        } catch (Exception e) {
            logger.error("Failed to fetch skills from GitHub: {}/{} - {}", owner, repo, e.getMessage());
            return new ArrayList<>();
        }
    }

    private String buildIndexPath(String basePath) {
        if (basePath == null || basePath.isEmpty()) {
            return "skill-index.yaml";
        }
        return basePath + "/skill-index.yaml";
    }

    private String buildCacheKey(String platform, String owner, String repo, String basePath) {
        return String.format("%s:%s/%s/%s", platform, owner, repo, basePath != null ? basePath : "");
    }

    private String normalizePath(String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }
        path = path.trim();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    private String fetchUrlContent(String urlStr) {
        return fetchUrlContentWithHeaders(urlStr, Collections.emptyMap());
    }

    private String fetchUrlContentWithHeaders(String urlStr, Map<String, String> headers) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(30000);
            
            for (Map.Entry<String, String> header : headers.entrySet()) {
                conn.setRequestProperty(header.getKey(), header.getValue());
            }
            
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                logger.warn("HTTP request failed: {} - {}", responseCode, urlStr);
                return null;
            }
            
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
            
            return content.toString();
            
        } catch (Exception e) {
            logger.error("Failed to fetch URL: {} - {}", urlStr, e.getMessage());
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<SkillPackage> parseSkillIndex(String content) {
        List<SkillPackage> skills = new ArrayList<>();
        
        try {
            JSONObject indexData = JSON.parseObject(content);
            
            JSONObject spec = indexData.getJSONObject("spec");
            if (spec != null && spec.containsKey("includes")) {
                JSONArray includesArray = spec.getJSONArray("includes");
                List<String> includes = includesArray.toJavaList(String.class);
                logger.info("Detected includes format with {} patterns", includes.size());
                return resolveIncludes(includes, indexData);
            }
            
            JSONArray skillsArray = indexData.getJSONArray("skills");
            if (skillsArray != null) {
                for (int i = 0; i < skillsArray.size(); i++) {
                    JSONObject skillData = skillsArray.getJSONObject(i);
                    SkillPackage skill = createSkillPackage(skillData);
                    if (skill != null) {
                        skills.add(skill);
                    }
                }
            }
            
            logger.debug("Parsed {} skills from skill-index.yaml", skills.size());
            
        } catch (Exception e) {
            logger.error("Failed to parse skill-index.yaml: {}", e.getMessage());
        }
        
        return skills;
    }
    
    @SuppressWarnings("unchecked")
    private List<SkillPackage> resolveIncludes(List<String> includes, JSONObject indexData) {
        List<SkillPackage> allSkills = new ArrayList<>();
        
        String platform = (String) giteeConfig.getOrDefault("_currentPlatform", "gitee");
        String owner = (String) giteeConfig.get("owner");
        String repo = (String) giteeConfig.get("repo");
        String branch = (String) giteeConfig.getOrDefault("branch", "main");
        String token = (String) giteeConfig.get("token");
        
        if (platform.equals("github")) {
            owner = (String) githubConfig.get("owner");
            repo = (String) githubConfig.get("repo");
            token = (String) githubConfig.get("token");
        }
        
        for (String include : includes) {
            try {
                List<SkillPackage> resolved;
                
                if (include.contains("*")) {
                    resolved = resolveWildcardInclude(include, platform, owner, repo, branch, token);
                } else {
                    resolved = resolveSingleInclude(include, platform, owner, repo, branch, token);
                }
                
                allSkills.addAll(resolved);
                logger.debug("Resolved include '{}': found {} skills", include, resolved.size());
                
            } catch (Exception e) {
                logger.warn("Failed to resolve include '{}': {}", include, e.getMessage());
            }
        }
        
        logger.info("Resolved {} total skills from {} includes", allSkills.size(), includes.size());
        return allSkills;
    }
    
    private List<SkillPackage> resolveWildcardInclude(String pattern, String platform, 
            String owner, String repo, String branch, String token) {
        List<SkillPackage> skills = new ArrayList<>();
        
        String dirPath = extractDirectory(pattern);
        String filePattern = extractFilePattern(pattern);
        
        try {
            List<String> files = listDirectoryFiles(platform, owner, repo, branch, dirPath, token);
            
            for (String file : files) {
                if (matchesPattern(file, filePattern)) {
                    String filePath = dirPath.isEmpty() ? file : dirPath + "/" + file;
                    List<SkillPackage> fileSkills = fetchAndParseYamlFile(platform, owner, repo, 
                            branch, filePath, token);
                    skills.addAll(fileSkills);
                }
            }
            
        } catch (Exception e) {
            logger.warn("Failed to resolve wildcard pattern '{}': {}", pattern, e.getMessage());
        }
        
        return skills;
    }
    
    private List<SkillPackage> resolveSingleInclude(String filePath, String platform,
            String owner, String repo, String branch, String token) {
        try {
            return fetchAndParseYamlFile(platform, owner, repo, branch, filePath, token);
        } catch (Exception e) {
            logger.warn("Failed to resolve single include '{}': {}", filePath, e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private List<String> listDirectoryFiles(String platform, String owner, String repo,
            String branch, String dirPath, String token) {
        List<String> files = new ArrayList<>();
        
        try {
            String apiUrl;
            Map<String, String> headers = new HashMap<>();
            
            if ("gitee".equals(platform)) {
                apiUrl = String.format("%s/repos/%s/%s/contents/%s?ref=%s",
                        GITEE_API_BASE, owner, repo, dirPath, branch);
                if (token != null && !token.isEmpty()) {
                    apiUrl += "&access_token=" + token;
                }
            } else {
                apiUrl = String.format("%s/repos/%s/%s/contents/%s?ref=%s",
                        GITHUB_API_BASE, owner, repo, dirPath, branch);
                if (token != null && !token.isEmpty()) {
                    headers.put("Authorization", "token " + token);
                }
                headers.put("Accept", "application/vnd.github.v3+json");
            }
            
            String jsonResponse = fetchUrlContentWithHeaders(apiUrl, headers);
            if (jsonResponse == null) {
                return files;
            }
            
            JSONArray items = JSON.parseArray(jsonResponse);
            for (int i = 0; i < items.size(); i++) {
                JSONObject item = items.getJSONObject(i);
                String type = item.getString("type");
                if ("file".equals(type)) {
                    files.add(item.getString("name"));
                }
            }
            
            logger.debug("Listed {} files in directory: {}", files.size(), dirPath);
            
        } catch (Exception e) {
            logger.warn("Failed to list directory '{}': {}", dirPath, e.getMessage());
        }
        
        return files;
    }
    
    @SuppressWarnings("unchecked")
    private List<SkillPackage> fetchAndParseYamlFile(String platform, String owner, String repo,
            String branch, String filePath, String token) {
        List<SkillPackage> skills = new ArrayList<>();
        
        try {
            String apiUrl;
            Map<String, String> headers = new HashMap<>();
            
            if ("gitee".equals(platform)) {
                apiUrl = String.format("%s/repos/%s/%s/contents/%s?ref=%s",
                        GITEE_API_BASE, owner, repo, filePath, branch);
                if (token != null && !token.isEmpty()) {
                    apiUrl += "&access_token=" + token;
                }
            } else {
                apiUrl = String.format("%s/repos/%s/%s/contents/%s?ref=%s",
                        GITHUB_API_BASE, owner, repo, filePath, branch);
                if (token != null && !token.isEmpty()) {
                    headers.put("Authorization", "token " + token);
                }
                headers.put("Accept", "application/vnd.github.v3.raw");
            }
            
            String content;
            if ("gitee".equals(platform)) {
                String jsonResponse = fetchUrlContent(apiUrl);
                if (jsonResponse == null) {
                    return skills;
                }
                content = decodeGiteeContent(jsonResponse);
            } else {
                content = fetchUrlContentWithHeaders(apiUrl, headers);
            }
            
            if (content == null) {
                return skills;
            }
            
            JSONObject yamlData = JSON.parseObject(content);
            
            if (yamlData.containsKey("skills")) {
                JSONArray skillsArray = yamlData.getJSONArray("skills");
                for (int i = 0; i < skillsArray.size(); i++) {
                    JSONObject skillData = skillsArray.getJSONObject(i);
                    SkillPackage skill = createSkillPackage(skillData);
                    if (skill != null) {
                        skills.add(skill);
                    }
                }
            } else if (yamlData.containsKey("id") || yamlData.containsKey("metadata")) {
                JSONObject skillData = yamlData.containsKey("metadata") 
                        ? yamlData.getJSONObject("metadata") 
                        : yamlData;
                
                JSONObject spec = yamlData.getJSONObject("spec");
                if (spec != null) {
                    skillData.putAll(spec);
                }
                
                SkillPackage skill = createSkillPackage(skillData);
                if (skill != null) {
                    skills.add(skill);
                }
            }
            
            logger.debug("Parsed {} skills from file: {}", skills.size(), filePath);
            
        } catch (Exception e) {
            logger.warn("Failed to fetch/parse file '{}': {}", filePath, e.getMessage());
        }
        
        return skills;
    }
    
    private String extractDirectory(String pattern) {
        int lastSlash = pattern.lastIndexOf('/');
        if (lastSlash > 0) {
            return pattern.substring(0, lastSlash);
        }
        return "";
    }
    
    private String extractFilePattern(String pattern) {
        int lastSlash = pattern.lastIndexOf('/');
        if (lastSlash >= 0) {
            return pattern.substring(lastSlash + 1);
        }
        return pattern;
    }
    
    private boolean matchesPattern(String fileName, String pattern) {
        if (pattern.equals("*")) {
            return true;
        }
        if (pattern.startsWith("*.")) {
            String ext = pattern.substring(1);
            return fileName.endsWith(ext);
        }
        if (pattern.endsWith(".*")) {
            String prefix = pattern.substring(0, pattern.length() - 1);
            return fileName.startsWith(prefix);
        }
        if (pattern.contains("*")) {
            String regex = pattern.replace(".", "\\.").replace("*", ".*");
            return fileName.matches(regex);
        }
        return fileName.equals(pattern);
    }

    @SuppressWarnings("unchecked")
    private SkillPackage createSkillPackage(JSONObject skillData) {
        try {
            String skillId = skillData.getString("id");
            String name = skillData.getString("name");
            String version = skillData.getString("version");
            String description = skillData.getString("description");
            String category = skillData.getString("category");
            
            if (skillId == null || name == null) {
                return null;
            }
            
            SkillPackage skill = new SkillPackage();
            skill.setSkillId(skillId);
            skill.setName(name);
            skill.setVersion(version != null ? version : "1.0.0");
            skill.setDescription(description);
            skill.setCategory(category);
            
            JSONArray tagsArray = skillData.getJSONArray("tags");
            if (tagsArray != null) {
                List<String> tags = new ArrayList<>();
                for (int i = 0; i < tagsArray.size(); i++) {
                    tags.add(String.valueOf(tagsArray.get(i)));
                }
                skill.setTags(tags);
            }
            
            return skill;
            
        } catch (Exception e) {
            logger.warn("Failed to create SkillPackage: {}", e.getMessage());
            return null;
        }
    }

    private String extractOwner(String repositoryUrl) {
        String[] parts = repositoryUrl.split("/");
        if (parts.length >= 4) {
            return parts[3];
        }
        return "";
    }

    private String extractRepo(String repositoryUrl) {
        String[] parts = repositoryUrl.split("/");
        if (parts.length >= 5) {
            return parts[4].replace(".git", "");
        }
        return "";
    }
}
