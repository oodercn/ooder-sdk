package net.ooder.scene.discovery.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import net.ooder.scene.discovery.GiteeDiscoveryConfig;
import net.ooder.scene.discovery.GithubDiscoveryConfig;
import net.ooder.scene.discovery.UnifiedDiscoveryService;
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
 * <h3>v3.0.1 更新：</h3>
 * <ul>
 *   <li>修复 YAML 解析问题 - 使用 Jackson YAML 解析器</li>
 *   <li>支持配置索引文件名 - 可配置 index.yaml 或 skill-index.yaml</li>
 *   <li>支持自动检测索引文件 - 自动尝试多个备选文件名</li>
 *   <li>支持递归目录遍历 - 支持 &#42;&#42;/*.yaml 模式</li>
 *   <li>优化空内容处理 - 添加空数组检查</li>
 * </ul>
 *
 * @author ooder
 * @since 2.3.1
 */
public class UnifiedDiscoveryServiceImpl implements UnifiedDiscoveryService {

    private static final Logger logger = LoggerFactory.getLogger(UnifiedDiscoveryServiceImpl.class);

    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    private GiteeDiscoveryConfig giteeConfig;
    private GithubDiscoveryConfig githubConfig;
    
    private static final String GITEE_API_BASE = "https://gitee.com/api/v5";
    private static final String GITHUB_API_BASE = "https://api.github.com";

    public UnifiedDiscoveryServiceImpl() {
        this.giteeConfig = new GiteeDiscoveryConfig();
        this.githubConfig = new GithubDiscoveryConfig();
    }

    public void configureGitee(GiteeDiscoveryConfig config) {
        if (config == null) {
            logger.warn("GiteeDiscoveryConfig is null, skip configuration");
            return;
        }
        this.giteeConfig = config;
        logger.info("Gitee configured: token={}", config.getToken() != null ? "***" : "null");
    }

    public void configureGithub(GithubDiscoveryConfig config) {
        if (config == null) {
            logger.warn("GithubDiscoveryConfig is null, skip configuration");
            return;
        }
        this.githubConfig = config;
        logger.info("GitHub configured: token={}", config.getToken() != null ? "***" : "null");
    }

    @Override
    public CompletableFuture<List<SkillPackage>> discoverSkills(String repositoryUrl) {
        return discoverSkills(repositoryUrl, null);
    }

    @Override
    public CompletableFuture<List<SkillPackage>> discoverSkills(String repositoryUrl, String skillsPath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("=== Discovery Request Start ===");
                logger.info("Repository URL: {}", repositoryUrl);
                logger.info("Skills Path: {}", skillsPath);
                
                if (repositoryUrl == null || repositoryUrl.isEmpty()) {
                    logger.warn("Repository URL is null or empty");
                    return new ArrayList<>();
                }
                
                String normalizedUrl = repositoryUrl.toLowerCase().trim();
                
                if (normalizedUrl.contains("gitee.com")) {
                    logger.info("Detected platform: Gitee");
                    logger.debug("URL pattern matched: *.gitee.com*");
                    List<SkillPackage> result = discoverFromGitee(repositoryUrl, skillsPath);
                    logger.info("=== Discovery Complete: {} skills found ===", result.size());
                    return result;
                } else if (normalizedUrl.contains("github.com")) {
                    logger.info("Detected platform: GitHub");
                    logger.debug("URL pattern matched: *.github.com*");
                    List<SkillPackage> result = discoverFromGithub(repositoryUrl, skillsPath);
                    logger.info("=== Discovery Complete: {} skills found ===", result.size());
                    return result;
                } else {
                    logger.warn("Unsupported repository URL platform: {}", repositoryUrl);
                    logger.warn("Supported platforms: gitee.com, github.com");
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
        logger.info("Cache refresh requested for: {} (no-op)", repositoryUrl);
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public void clearAllCache() {
        logger.info("Clear cache requested (no-op)");
    }

    @Override
    public CacheStatus getCacheStatus(String repositoryUrl) {
        CacheStatus status = new CacheStatus();
        status.setCached(false);
        return status;
    }

    @Override
    public void setCacheConfig(CacheConfig config) {
        logger.info("Cache config set (no-op): ttl={}ms", config.getCacheTtlMs());
    }

    private List<SkillPackage> discoverFromGitee(String repositoryUrl, String skillsPath) {
        try {
            String owner = extractOwner(repositoryUrl);
            String repo = extractRepo(repositoryUrl);
            String token = giteeConfig.getToken();
            String branch = giteeConfig.getBranch() != null ? giteeConfig.getBranch() : "master";
            String basePath = skillsPath != null ? normalizePath(skillsPath) : "";
            String indexFileName = giteeConfig.getIndexFileName() != null ? giteeConfig.getIndexFileName() : "skill-index.yaml";
            boolean recursive = giteeConfig.isRecursive();
            List<String> fallbackIndexFiles = giteeConfig.getFallbackIndexFiles();
            if (fallbackIndexFiles == null || fallbackIndexFiles.isEmpty()) {
                fallbackIndexFiles = Arrays.asList("index.yaml", "skill-index.yaml");
            }
            
            logger.info("Discovering from Gitee: owner={}, repo={}, branch={}, basePath={}", 
                owner, repo, branch, basePath);
            
            List<SkillPackage> skills = fetchSkillsFromGitee(owner, repo, branch, basePath, token, 
                    indexFileName, fallbackIndexFiles, recursive);
            
            logger.info("Discovered {} skills from Gitee", skills.size());
            
            return skills;
        } catch (Exception e) {
            logger.error("Failed to discover from Gitee: " + repositoryUrl, e);
            return new ArrayList<>();
        }
    }

    private List<SkillPackage> fetchSkillsFromGitee(String owner, String repo, String branch, 
            String basePath, String token, String indexFileName, List<String> fallbackIndexFiles,
            boolean recursive) {
        try {
            String yamlContent = null;
            String usedIndexFile = null;
            
            String primaryIndexPath = buildIndexPath(basePath, indexFileName);
            yamlContent = fetchAndDecodeGiteeFile(owner, repo, branch, primaryIndexPath, token);
            
            if (yamlContent != null) {
                usedIndexFile = primaryIndexPath;
                logger.debug("Found index file: {}", primaryIndexPath);
            } else {
                for (String fallbackFile : fallbackIndexFiles) {
                    String fallbackPath = buildIndexPath(basePath, fallbackFile);
                    yamlContent = fetchAndDecodeGiteeFile(owner, repo, branch, fallbackPath, token);
                    if (yamlContent != null) {
                        usedIndexFile = fallbackPath;
                        logger.info("Using fallback index file: {}", fallbackPath);
                        break;
                    }
                }
            }
            
            if (yamlContent == null) {
                logger.warn("No index file found in path: {}", basePath);
                return new ArrayList<>();
            }
            
            return parseSkillIndex(yamlContent, recursive, basePath, "gitee", owner, repo, branch, token);
            
        } catch (Exception e) {
            logger.error("Failed to fetch skills from Gitee: {}/{} - {}", owner, repo, e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private String fetchAndDecodeGiteeFile(String owner, String repo, String branch, 
            String filePath, String token) {
        try {
            String indexUrl = String.format(
                "https://gitee.com/api/v5/repos/%s/%s/contents/%s?ref=%s",
                owner, repo, filePath, branch
            );
            
            if (token != null && !token.isEmpty()) {
                indexUrl += "&access_token=" + token;
            }
            
            logger.debug("Fetching file from: {}", indexUrl.replaceAll("access_token=[^&]+", "access_token=***"));
            
            String jsonResponse = fetchUrlContent(indexUrl);
            if (jsonResponse == null) {
                return null;
            }
            
            return decodeGiteeContent(jsonResponse);
            
        } catch (Exception e) {
            logger.debug("Failed to fetch file {}: {}", filePath, e.getMessage());
            return null;
        }
    }
    
    private String decodeGiteeContent(String jsonResponse) {
        if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
            logger.warn("Empty response from Gitee API");
            return null;
        }
        
        String trimmedResponse = jsonResponse.trim();
        
        if (trimmedResponse.startsWith("[")) {
            try {
                JSONArray arr = JSON.parseArray(jsonResponse);
                if (arr == null || arr.isEmpty()) {
                    logger.warn("Gitee API returned empty array");
                    return null;
                }
                logger.debug("Gitee API returned array with {} items, expected file content", arr.size());
                return null;
            } catch (Exception e) {
                logger.warn("Failed to parse Gitee response as array: {}", e.getMessage());
                return null;
            }
        }
        
        try {
            JSONObject responseMap = JSON.parseObject(jsonResponse);
            
            if (responseMap == null) {
                logger.warn("Gitee API response is null after parsing");
                return null;
            }
            
            String encoding = responseMap.getString("encoding");
            String content = responseMap.getString("content");
            
            if (content == null || content.isEmpty()) {
                logger.warn("Gitee API response missing 'content' field");
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
            String owner = extractOwner(repositoryUrl);
            String repo = extractRepo(repositoryUrl);
            String token = githubConfig.getToken();
            String branch = "main";
            String basePath = skillsPath != null ? normalizePath(skillsPath) : "";
            
            logger.info("Discovering from GitHub: owner={}, repo={}, branch={}, basePath={}", owner, repo, branch, basePath);
            
            List<SkillPackage> skills = fetchSkillsFromGithub(owner, repo, branch, basePath, token);
            
            logger.info("Discovered {} skills from GitHub", skills.size());
            
            return skills;
        } catch (Exception e) {
            logger.error("Failed to discover from GitHub: " + repositoryUrl, e);
            return new ArrayList<>();
        }
    }

    private List<SkillPackage> fetchSkillsFromGithub(String owner, String repo, String branch,
            String basePath, String token) {
        try {
            String indexPath = buildIndexPath(basePath, "skill-index.yaml");
            String indexUrl = String.format(
                "https://api.github.com/repos/%s/%s/contents/%s?ref=%s",
                owner, repo, indexPath, branch
            );
            
            logger.debug("Fetching skill-index from: {} (branch={})", indexUrl, branch);
            
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
            
            return parseSkillIndex(content, false, basePath, "github", owner, repo, branch, token);
            
        } catch (Exception e) {
            logger.error("Failed to fetch skills from GitHub: {}/{} - {}", owner, repo, e.getMessage());
            return new ArrayList<>();
        }
    }

    private String buildIndexPath(String basePath, String indexFileName) {
        String fileName = indexFileName != null ? indexFileName : "skill-index.yaml";
        if (basePath == null || basePath.isEmpty()) {
            return fileName;
        }
        return basePath + "/" + fileName;
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
    private List<SkillPackage> parseSkillIndex(String content, boolean recursive, String basePath, 
            String platform, String owner, String repo, String branch, String token) {
        List<SkillPackage> skills = new ArrayList<>();
        
        if (content == null || content.trim().isEmpty()) {
            logger.warn("Empty skill-index content");
            return skills;
        }
        
        try {
            Map<String, Object> indexDataMap = yamlMapper.readValue(content, 
                    new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
            JSONObject indexData = new JSONObject(indexDataMap);
            
            JSONObject spec = indexData.getJSONObject("spec");
            if (spec != null && spec.containsKey("includes")) {
                JSONArray includesArray = spec.getJSONArray("includes");
                List<String> includes = includesArray.toJavaList(String.class);
                logger.info("Detected includes format with {} patterns", includes.size());
                return resolveIncludes(includes, indexData, recursive, basePath, platform, owner, repo, branch, token);
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
    private List<SkillPackage> resolveIncludes(List<String> includes, JSONObject indexData, boolean recursive, 
            String basePath, String platform, String owner, String repo, String branch, String token) {
        List<SkillPackage> allSkills = new ArrayList<>();
        
        logger.debug("resolveIncludes: platform={}, owner={}, repo={}, branch={}", platform, owner, repo, branch);
        
        for (String include : includes) {
            try {
                String fullIncludePath = buildFullIncludePath(basePath, include);
                
                List<SkillPackage> resolved;
                
                if (fullIncludePath.contains("**")) {
                    resolved = resolveRecursiveInclude(fullIncludePath, platform, owner, repo, branch, token);
                } else if (fullIncludePath.contains("*")) {
                    resolved = resolveWildcardInclude(fullIncludePath, platform, owner, repo, branch, token, recursive);
                } else {
                    resolved = resolveSingleInclude(fullIncludePath, platform, owner, repo, branch, token);
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
    
    private String buildFullIncludePath(String basePath, String include) {
        if (basePath == null || basePath.isEmpty()) {
            return include;
        }
        if (include.startsWith("/")) {
            return include.substring(1);
        }
        return basePath + "/" + include;
    }
    
    private List<SkillPackage> resolveRecursiveInclude(String pattern, String platform, 
            String owner, String repo, String branch, String token) {
        List<SkillPackage> skills = new ArrayList<>();
        
        String basePath = extractBasePath(pattern);
        String filePattern = extractFilePattern(pattern);
        
        logger.debug("Recursive include: basePath={}, filePattern={}", basePath, filePattern);
        
        try {
            collectSkillsRecursively(platform, owner, repo, branch, basePath, filePattern, token, skills);
        } catch (Exception e) {
            logger.warn("Failed to resolve recursive pattern '{}': {}", pattern, e.getMessage());
        }
        
        return skills;
    }
    
    private void collectSkillsRecursively(String platform, String owner, String repo,
            String branch, String dirPath, String filePattern, String token, 
            List<SkillPackage> collectedSkills) {
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
            if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
                return;
            }
            
            String trimmedResponse = jsonResponse.trim();
            if (!trimmedResponse.startsWith("[")) {
                logger.debug("Response is not an array for directory: {}", dirPath);
                return;
            }
            
            JSONArray items = JSON.parseArray(jsonResponse);
            if (items == null) {
                return;
            }
            
            for (int i = 0; i < items.size(); i++) {
                JSONObject item = items.getJSONObject(i);
                String type = item.getString("type");
                String name = item.getString("name");
                
                if ("file".equals(type)) {
                    if (matchesPattern(name, filePattern)) {
                        String filePath = dirPath.isEmpty() ? name : dirPath + "/" + name;
                        List<SkillPackage> fileSkills = fetchAndParseYamlFile(platform, owner, repo, 
                                branch, filePath, token);
                        collectedSkills.addAll(fileSkills);
                    }
                } else if ("dir".equals(type)) {
                    String subDirPath = dirPath.isEmpty() ? name : dirPath + "/" + name;
                    collectSkillsRecursively(platform, owner, repo, branch, subDirPath, 
                            filePattern, token, collectedSkills);
                }
            }
            
        } catch (Exception e) {
            logger.debug("Error collecting from directory '{}': {}", dirPath, e.getMessage());
        }
    }
    
    private String extractBasePath(String pattern) {
        int doubleStarIndex = pattern.indexOf("**");
        if (doubleStarIndex > 0) {
            return pattern.substring(0, doubleStarIndex - 1);
        }
        return "";
    }
    
    private List<SkillPackage> resolveWildcardInclude(String pattern, String platform, 
            String owner, String repo, String branch, String token, boolean recursive) {
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
            
            if (recursive) {
                List<String> subDirs = listDirectorySubdirs(platform, owner, repo, branch, dirPath, token);
                for (String subDir : subDirs) {
                    String subDirPath = dirPath.isEmpty() ? subDir : dirPath + "/" + subDir;
                    List<SkillPackage> subDirSkills = resolveWildcardInclude(
                            subDirPath + "/*" + filePattern.substring(filePattern.lastIndexOf('.')),
                            platform, owner, repo, branch, token, true);
                    skills.addAll(subDirSkills);
                }
            }
            
        } catch (Exception e) {
            logger.warn("Failed to resolve wildcard pattern '{}': {}", pattern, e.getMessage());
        }
        
        return skills;
    }
    
    private List<String> listDirectorySubdirs(String platform, String owner, String repo,
            String branch, String dirPath, String token) {
        List<String> subdirs = new ArrayList<>();
        
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
            if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
                return subdirs;
            }
            
            String trimmedResponse = jsonResponse.trim();
            if (!trimmedResponse.startsWith("[")) {
                return subdirs;
            }
            
            JSONArray items = JSON.parseArray(jsonResponse);
            for (int i = 0; i < items.size(); i++) {
                JSONObject item = items.getJSONObject(i);
                String type = item.getString("type");
                if ("dir".equals(type)) {
                    subdirs.add(item.getString("name"));
                }
            }
            
            logger.debug("Listed {} subdirectories in: {}", subdirs.size(), dirPath);
            
        } catch (Exception e) {
            logger.debug("Failed to list subdirectories in '{}': {}", dirPath, e.getMessage());
        }
        
        return subdirs;
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
            if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
                return files;
            }
            
            String trimmedResponse = jsonResponse.trim();
            if (!trimmedResponse.startsWith("[")) {
                logger.debug("Response is not an array for directory: {}", dirPath);
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
            String content;
            
            if ("gitee".equals(platform)) {
                content = fetchAndDecodeGiteeFile(owner, repo, branch, filePath, token);
            } else {
                String apiUrl = String.format("%s/repos/%s/%s/contents/%s?ref=%s",
                        GITHUB_API_BASE, owner, repo, filePath, branch);
                
                Map<String, String> headers = new HashMap<>();
                if (token != null && !token.isEmpty()) {
                    headers.put("Authorization", "token " + token);
                }
                headers.put("Accept", "application/vnd.github.v3.raw");
                
                content = fetchUrlContentWithHeaders(apiUrl, headers);
            }
            
            if (content == null || content.trim().isEmpty()) {
                return skills;
            }
            
            Map<String, Object> yamlDataMap = yamlMapper.readValue(content, 
                    new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
            JSONObject yamlData = new JSONObject(yamlDataMap);
            
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
            String skillId = skillData.getString("skillId");
            if (skillId == null) {
                skillId = skillData.getString("id");
            }
            
            String name = skillData.getString("name");
            String version = skillData.getString("version");
            String description = skillData.getString("description");
            
            String category = skillData.getString("capabilityCategory");
            if (category == null) {
                category = skillData.getString("category");
            }
            
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
            
            JSONObject spec = skillData.getJSONObject("spec");
            if (spec != null) {
                Map<String, Object> metadata = new HashMap<>();
                
                String skillForm = spec.getString("skillForm");
                if (skillForm != null) {
                    metadata.put("skillForm", skillForm);
                    metadata.put("type", skillForm);
                }
                
                String sceneType = spec.getString("sceneType");
                if (sceneType != null) {
                    metadata.put("sceneType", sceneType);
                }
                
                metadata.put("spec", new HashMap<>(spec));
                
                skill.setMetadata(metadata);
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
