
package net.ooder.skills.core.discovery;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;

import net.ooder.skills.api.SkillDiscoverer;
import net.ooder.skills.api.SkillForm;
import net.ooder.skills.api.SkillManifest;
import net.ooder.skills.api.SkillPackage;
import net.ooder.skills.common.enums.DiscoveryMethod;
import net.ooder.skills.exception.ApiRateLimitException;
import net.ooder.skills.exception.AuthenticationException;
import net.ooder.skills.exception.DiscoveryException;
import net.ooder.skills.exception.RepositoryNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitRepositoryDiscovererAdapter implements SkillDiscoverer {
    
    private static final Logger log = LoggerFactory.getLogger(GitRepositoryDiscovererAdapter.class);
    
    private static final String GITEE_API_BASE = "https://gitee.com/api/v5";
    private static final String GITHUB_API_BASE = "https://api.github.com";
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;
    
    private final HttpClient httpClient;
    private final ObjectMapper jsonMapper;
    private final ObjectMapper yamlMapper;
    
    private long timeout = 60000;
    private DiscoveryFilter filter;
    private String defaultOwner;
    private String defaultRepo;
    private String defaultBranch = "main";
    private String githubToken;
    private String giteeToken;
    private String source = "github";
    
    private final Map<String, GitRepositoryConfig> repositoryConfigs = new ConcurrentHashMap<>();
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private long cacheTtlMs = 300000;
    
    public GitRepositoryDiscovererAdapter() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
        this.jsonMapper = new ObjectMapper();
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
    }
    
    public GitRepositoryDiscovererAdapter(String source) {
        this();
        this.source = source;
    }
    
    @Override
    public CompletableFuture<List<SkillPackage>> discover() {
        return CompletableFuture.supplyAsync(() -> {
            validateConfiguration();

            String cacheKey = buildCacheKey("discover", null);
            List<SkillPackage> cached = getFromCache(cacheKey);
            if (cached != null) {
                log.debug("Returning cached skill packages for discover()");
                return cached;
            }

            // 使用 LinkedHashMap 保持插入顺序并进行去重
            Map<String, SkillPackage> uniquePackages = new LinkedHashMap<>();

            try {
                String apiBase = getApiBase();
                String token = getToken();

                String treeUrl = buildTreeUrl(apiBase);
                log.info("Discovering skills from {} repository: {}/{}", source, defaultOwner, defaultRepo);

                JsonNode tree = fetchRepositoryTree(treeUrl, token);

                for (JsonNode node : tree) {
                    String path = node.get("path").asText();
                    if (path.endsWith("skill.yaml") || path.endsWith("skill.yml")) {
                        try {
                            SkillPackage pkg = loadSkillPackage(path, token);
                            if (pkg != null && applyFilter(pkg)) {
                                String skillId = pkg.getSkillId();

                                // 过滤掉 INTERNAL 技能，只保留 STANDALONE 和 SCENE
                                if (pkg.getForm() == SkillForm.INTERNAL) {
                                    log.debug("[Filter] Skipped internal skill: {}", skillId);
                                    continue;
                                }

                                SkillPackage existing = uniquePackages.get(skillId);

                                if (existing == null) {
                                    // 第一次遇到该技能，直接添加
                                    uniquePackages.put(skillId, pkg);
                                    log.debug("[Deduplication] Added skill: {} with form: {}", skillId, pkg.getForm());
                                } else {
                                    // 遇到重复技能，根据 skillForm 优先级决定是否替换
                                    if (shouldReplaceByForm(existing.getForm(), pkg.getForm())) {
                                        SkillForm oldForm = existing.getForm();
                                        SkillForm newForm = pkg.getForm();
                                        uniquePackages.put(skillId, pkg);
                                        log.info("[Deduplication] Replaced skill: {} with higher priority version ({} > {})",
                                            skillId, newForm, oldForm);
                                    } else {
                                        log.debug("[Deduplication] Skipped skill: {} with lower priority form: {}",
                                            skillId, pkg.getForm());
                                    }
                                }
                            }
                        } catch (Exception e) {
                            log.warn("Failed to load skill from {}: {}", path, e.getMessage());
                        }
                    }
                }

                List<SkillPackage> packages = new ArrayList<>(uniquePackages.values());
                putToCache(cacheKey, packages);
                log.info("Discovered {} skill packages from {}/{} (after deduplication)",
                    packages.size(), defaultOwner, defaultRepo);

                // 输出去重统计信息
                int totalDiscovered = uniquePackages.size();
                int duplicates = totalDiscovered - packages.size();
                if (duplicates > 0) {
                    log.info("[Deduplication] Removed {} duplicate skill(s)", duplicates);
                }

            } catch (AuthenticationException | RepositoryNotFoundException | ApiRateLimitException e) {
                log.error("Discovery failed: {}", e.getMessage());
                throw new CompletionException(e);
            } catch (Exception e) {
                log.error("Failed to discover skills from Git repository", e);
                throw new CompletionException(new DiscoveryException("Discovery failed", e));
            }

            return new ArrayList<>(uniquePackages.values());
        });
    }

    /**
     * 根据 skillForm 优先级判断是否应该用新技能替换已有技能
     * 优先级: SCENE(1) > DRIVER(2) > PROVIDER(3) > STANDALONE(4) > INTERNAL(5) > null(6)
     *
     * @param existingForm 已有技能的 form
     * @param newForm 新技能的 form
     * @return 如果新技能优先级更高则返回 true
     */
    private boolean shouldReplaceByForm(SkillForm existingForm, SkillForm newForm) {
        // 如果新技能没有 form，不替换
        if (newForm == null) {
            return false;
        }
        // 如果已有技能没有 form，使用新技能
        if (existingForm == null) {
            return true;
        }
        // 数值越小优先级越高
        return getFormPriority(newForm) < getFormPriority(existingForm);
    }

    /**
     * 获取 skillForm 的优先级数值
     * 数值越小优先级越高
     */
    private int getFormPriority(SkillForm form) {
        if (form == null) {
            return Integer.MAX_VALUE;
        }
        switch (form) {
            case SCENE:
                return 1;
            case DRIVER:
                return 2;
            case PROVIDER:
                return 3;
            case STANDALONE:
                return 4;
            case INTERNAL:
                return 5;
            default:
                return 6;
        }
    }
    
    @Override
    public CompletableFuture<SkillPackage> discover(String skillId) {
        return CompletableFuture.supplyAsync(() -> {
            validateConfiguration();
            
            if (skillId == null || skillId.isEmpty()) {
                return null;
            }
            
            String cacheKey = buildCacheKey("discover", skillId);
            List<SkillPackage> cached = getFromCache(cacheKey);
            if (cached != null && !cached.isEmpty()) {
                return cached.get(0);
            }
            
            try {
                String apiBase = getApiBase();
                String token = getToken();
                
                String searchPath = findSkillYamlPath(skillId, token);
                if (searchPath == null) {
                    log.warn("Skill not found: {}", skillId);
                    return null;
                }
                
                SkillPackage pkg = loadSkillPackage(searchPath, token);
                if (pkg != null) {
                    putToCache(cacheKey, List.of(pkg));
                }
                return pkg;
                
            } catch (Exception e) {
                log.error("Failed to discover skill: {}", skillId, e);
                throw new CompletionException(new DiscoveryException("Failed to discover skill: " + skillId, e));
            }
        });
    }
    
    @Override
    public CompletableFuture<List<SkillPackage>> discoverByScene(String sceneId) {
        return CompletableFuture.supplyAsync(() -> {
            validateConfiguration();
            
            if (sceneId == null || sceneId.isEmpty()) {
                return new ArrayList<>();
            }
            
            String cacheKey = buildCacheKey("scene", sceneId);
            List<SkillPackage> cached = getFromCache(cacheKey);
            if (cached != null) {
                return cached;
            }
            
            List<SkillPackage> result = new ArrayList<>();
            
            try {
                List<SkillPackage> allPackages = discover().join();
                for (SkillPackage pkg : allPackages) {
                    if (sceneId.equals(pkg.getSceneId())) {
                        result.add(pkg);
                    }
                }
                
                putToCache(cacheKey, result);
                
            } catch (Exception e) {
                log.error("Failed to discover skills by scene: {}", sceneId, e);
                throw new CompletionException(new DiscoveryException("Failed to discover by scene", e));
            }
            
            return result;
        });
    }
    
    @Override
    public CompletableFuture<List<SkillPackage>> search(String query) {
        return CompletableFuture.supplyAsync(() -> {
            validateConfiguration();
            
            if (query == null || query.isEmpty()) {
                return new ArrayList<>();
            }
            
            String cacheKey = buildCacheKey("search", query);
            List<SkillPackage> cached = getFromCache(cacheKey);
            if (cached != null) {
                return cached;
            }
            
            List<SkillPackage> result = new ArrayList<>();
            String lowerQuery = query.toLowerCase();
            
            try {
                List<SkillPackage> allPackages = discover().join();
                for (SkillPackage pkg : allPackages) {
                    if (matchesQuery(pkg, lowerQuery)) {
                        result.add(pkg);
                    }
                }
                
                putToCache(cacheKey, result);
                
            } catch (Exception e) {
                log.error("Failed to search skills: {}", query, e);
                throw new CompletionException(new DiscoveryException("Search failed", e));
            }
            
            return result;
        });
    }
    
    @Override
    public CompletableFuture<List<SkillPackage>> searchByCapability(String capabilityId) {
        return CompletableFuture.supplyAsync(() -> {
            validateConfiguration();
            
            if (capabilityId == null || capabilityId.isEmpty()) {
                return new ArrayList<>();
            }
            
            String cacheKey = buildCacheKey("capability", capabilityId);
            List<SkillPackage> cached = getFromCache(cacheKey);
            if (cached != null) {
                return cached;
            }
            
            List<SkillPackage> result = new ArrayList<>();
            
            try {
                List<SkillPackage> allPackages = discover().join();
                for (SkillPackage pkg : allPackages) {
                    if (hasCapability(pkg, capabilityId)) {
                        result.add(pkg);
                    }
                }
                
                putToCache(cacheKey, result);
                
            } catch (Exception e) {
                log.error("Failed to search skills by capability: {}", capabilityId, e);
                throw new CompletionException(new DiscoveryException("Search by capability failed", e));
            }
            
            return result;
        });
    }
    
    @Override
    public CompletableFuture<List<SkillPackage>> discoverByCategory(String category) {
        return discoverByCategory(category, null);
    }
    
    @Override
    public CompletableFuture<List<SkillPackage>> discoverByCategory(String category, String subCategory) {
        return CompletableFuture.supplyAsync(() -> {
            validateConfiguration();
            
            if (category == null || category.isEmpty()) {
                return new ArrayList<>();
            }
            
            String cacheKey = buildCacheKey("category", category + "/" + (subCategory != null ? subCategory : ""));
            List<SkillPackage> cached = getFromCache(cacheKey);
            if (cached != null) {
                return cached;
            }
            
            List<SkillPackage> result = new ArrayList<>();
            
            try {
                List<SkillPackage> allPackages = discover().join();
                for (SkillPackage pkg : allPackages) {
                    if (category.equals(pkg.getCategory())) {
                        if (subCategory == null || subCategory.isEmpty() || subCategory.equals(pkg.getSubCategory())) {
                            result.add(pkg);
                        }
                    }
                }
                
                putToCache(cacheKey, result);
                
            } catch (Exception e) {
                log.error("Failed to discover skills by category: {}/{}", category, subCategory, e);
                throw new CompletionException(new DiscoveryException("Discover by category failed", e));
            }
            
            return result;
        });
    }
    
    @Override
    public CompletableFuture<List<SkillPackage>> searchByTags(List<String> tags) {
        return CompletableFuture.supplyAsync(() -> {
            validateConfiguration();
            
            if (tags == null || tags.isEmpty()) {
                return new ArrayList<>();
            }
            
            String cacheKey = buildCacheKey("tags", String.join(",", tags));
            List<SkillPackage> cached = getFromCache(cacheKey);
            if (cached != null) {
                return cached;
            }
            
            List<SkillPackage> result = new ArrayList<>();
            
            try {
                List<SkillPackage> allPackages = discover().join();
                for (SkillPackage pkg : allPackages) {
                    if (hasAnyTag(pkg, tags)) {
                        result.add(pkg);
                    }
                }
                
                putToCache(cacheKey, result);
                
            } catch (Exception e) {
                log.error("Failed to search skills by tags: {}", tags, e);
                throw new CompletionException(new DiscoveryException("Search by tags failed", e));
            }
            
            return result;
        });
    }
    
    @Override
    public DiscoveryMethod getMethod() {
        if ("gitee".equalsIgnoreCase(source)) {
            return DiscoveryMethod.GITEE;
        } else if ("github".equalsIgnoreCase(source)) {
            return DiscoveryMethod.GITHUB;
        }
        return DiscoveryMethod.GIT_REPOSITORY;
    }
    
    @Override
    public boolean isAvailable() {
        return isTokenConfigured();
    }
    
    @Override
    public void setTimeout(long timeoutMs) {
        this.timeout = timeoutMs;
    }
    
    @Override
    public long getTimeout() {
        return timeout;
    }
    
    @Override
    public void setFilter(DiscoveryFilter filter) {
        this.filter = filter;
    }
    
    @Override
    public DiscoveryFilter getFilter() {
        return filter;
    }
    
    public void setDefaultOwner(String owner) {
        this.defaultOwner = owner;
    }
    
    public String getDefaultOwner() {
        return defaultOwner;
    }
    
    public void setDefaultRepo(String repo) {
        this.defaultRepo = repo;
    }
    
    public String getDefaultRepo() {
        return defaultRepo;
    }
    
    public void setDefaultBranch(String branch) {
        this.defaultBranch = branch;
    }
    
    public String getDefaultBranch() {
        return defaultBranch;
    }
    
    public void setGithubToken(String token) {
        this.githubToken = token;
    }
    
    public String getGithubToken() {
        return githubToken;
    }
    
    public void setGiteeToken(String token) {
        this.giteeToken = token;
    }
    
    public String getGiteeToken() {
        return giteeToken;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setCacheTtlMs(long cacheTtlMs) {
        this.cacheTtlMs = cacheTtlMs;
    }
    
    public long getCacheTtlMs() {
        return cacheTtlMs;
    }
    
    public void clearCache() {
        cache.clear();
        log.debug("Cache cleared");
    }
    
    public void addRepositoryConfig(String name, GitRepositoryConfig config) {
        repositoryConfigs.put(name, config);
    }
    
    public GitRepositoryConfig getRepositoryConfig(String name) {
        return repositoryConfigs.get(name);
    }
    
    private void validateConfiguration() {
        if (defaultOwner == null || defaultOwner.isEmpty()) {
            throw new DiscoveryException("Default owner not configured");
        }
        if (defaultRepo == null || defaultRepo.isEmpty()) {
            throw new DiscoveryException("Default repository not configured");
        }
        if (!isTokenConfigured()) {
            throw new DiscoveryException("Token not configured for " + source);
        }
    }
    
    private boolean isTokenConfigured() {
        if ("gitee".equalsIgnoreCase(source)) {
            return giteeToken != null && !giteeToken.isEmpty();
        } else if ("github".equalsIgnoreCase(source)) {
            return githubToken != null && !githubToken.isEmpty();
        }
        return false;
    }
    
    private String getApiBase() {
        if ("gitee".equalsIgnoreCase(source)) {
            return GITEE_API_BASE;
        }
        return GITHUB_API_BASE;
    }
    
    private String getToken() {
        if ("gitee".equalsIgnoreCase(source)) {
            return giteeToken;
        }
        return githubToken;
    }
    
    private String buildTreeUrl(String apiBase) {
        if ("gitee".equalsIgnoreCase(source)) {
            return String.format("%s/repos/%s/%s/git/trees/%s?recursive=1", 
                apiBase, defaultOwner, defaultRepo, defaultBranch);
        }
        return String.format("%s/repos/%s/%s/git/trees/%s?recursive=1", 
            apiBase, defaultOwner, defaultRepo, defaultBranch);
    }
    
    private String buildContentUrl(String apiBase, String path) {
        if ("gitee".equalsIgnoreCase(source)) {
            return String.format("%s/repos/%s/%s/contents/%s?ref=%s", 
                apiBase, defaultOwner, defaultRepo, path, defaultBranch);
        }
        return String.format("%s/repos/%s/%s/contents/%s?ref=%s", 
            apiBase, defaultOwner, defaultRepo, path, defaultBranch);
    }
    
    private JsonNode fetchRepositoryTree(String url, String token) throws Exception {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                HttpRequest request = buildRequest(url, token);
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                
                handleErrorResponse(response);
                
                JsonNode root = jsonMapper.readTree(response.body());
                if (root.has("tree")) {
                    return root.get("tree");
                }
                return root;
                
            } catch (ApiRateLimitException e) {
                throw e;
            } catch (AuthenticationException e) {
                throw e;
            } catch (RepositoryNotFoundException e) {
                throw e;
            } catch (Exception e) {
                lastException = e;
                log.warn("Attempt {}/{} failed for tree fetch: {}", attempt, MAX_RETRIES, e.getMessage());
                
                if (attempt < MAX_RETRIES) {
                    Thread.sleep(RETRY_DELAY_MS * attempt);
                }
            }
        }
        
        throw new DiscoveryException("Failed to fetch repository tree after " + MAX_RETRIES + " attempts", lastException);
    }
    
    private String fetchFileContent(String url, String token) throws Exception {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                HttpRequest request = buildRequest(url, token);
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                
                handleErrorResponse(response);
                
                JsonNode root = jsonMapper.readTree(response.body());
                
                if (root.has("content")) {
                    String content = root.get("content").asText();
                    String encoding = root.has("encoding") ? root.get("encoding").asText() : "base64";
                    
                    if ("base64".equals(encoding)) {
                        content = content.replace("\n", "").replace("\r", "");
                        return new String(Base64.getDecoder().decode(content), "UTF-8");
                    }
                    return content;
                }
                
                return response.body();
                
            } catch (ApiRateLimitException e) {
                throw e;
            } catch (AuthenticationException e) {
                throw e;
            } catch (RepositoryNotFoundException e) {
                throw e;
            } catch (Exception e) {
                lastException = e;
                log.warn("Attempt {}/{} failed for content fetch: {}", attempt, MAX_RETRIES, e.getMessage());
                
                if (attempt < MAX_RETRIES) {
                    Thread.sleep(RETRY_DELAY_MS * attempt);
                }
            }
        }
        
        throw new DiscoveryException("Failed to fetch file content after " + MAX_RETRIES + " attempts", lastException);
    }
    
    private HttpRequest buildRequest(String url, String token) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofMillis(timeout))
            .header("Accept", "application/json")
            .header("User-Agent", "OODER-Skills-Framework/3.0.1");
        
        if ("gitee".equalsIgnoreCase(source)) {
            // Gitee 使用 Authorization: token <token> header 进行认证
            builder.header("Authorization", "token " + token);
        } else {
            // GitHub 使用 Authorization: Bearer <token> header
            builder.header("Authorization", "Bearer " + token);
        }
        
        return builder.GET().build();
    }
    
    private void handleErrorResponse(HttpResponse<String> response) throws Exception {
        int statusCode = response.statusCode();
        
        if (statusCode == 200) {
            return;
        }
        
        String body = response.body();
        
        switch (statusCode) {
            case 401:
            case 403:
                throw new AuthenticationException("Authentication failed. Check your token for " + source);
            case 404:
                throw new RepositoryNotFoundException(defaultOwner, defaultRepo);
            case 429:
                long retryAfter = parseRetryAfter(response);
                throw new ApiRateLimitException(source, retryAfter);
            default:
                throw new DiscoveryException("API request failed with status " + statusCode + ": " + body);
        }
    }
    
    private long parseRetryAfter(HttpResponse<String> response) {
        String retryAfter = response.headers().firstValue("Retry-After").orElse("60");
        try {
            return Long.parseLong(retryAfter);
        } catch (NumberFormatException e) {
            return 60;
        }
    }
    
    private SkillPackage loadSkillPackage(String skillYamlPath, String token) throws Exception {
        String apiBase = getApiBase();
        String contentUrl = buildContentUrl(apiBase, skillYamlPath);
        
        log.debug("Loading skill package from: {}", skillYamlPath);
        
        String yamlContent = fetchFileContent(contentUrl, token);
        if (yamlContent == null || yamlContent.isEmpty()) {
            log.warn("Empty content for skill.yaml at: {}", skillYamlPath);
            return null;
        }
        
        return parseSkillPackage(yamlContent, skillYamlPath);
    }
    
    private SkillPackage parseSkillPackage(String yamlContent, String path) {
        try {
            SkillManifest manifest = yamlMapper.readValue(yamlContent, SkillManifest.class);

            if (manifest.getSkillId() == null || manifest.getSkillId().isEmpty()) {
                log.warn("Skill manifest missing skillId at path: {}", path);
                return null;
            }

            SkillPackage pkg = new SkillPackage();
            pkg.setSkillId(manifest.getSkillId());
            pkg.setName(manifest.getName() != null ? manifest.getSkillId() : manifest.getSkillId());
            pkg.setVersion(manifest.getVersion() != null ? manifest.getVersion() : "1.0.0");
            pkg.setDescription(manifest.getDescription());
            pkg.setTags(manifest.getTags());
            pkg.setSceneId(manifest.getSceneId());
            pkg.setManifest(manifest);

            // 从 manifest 中读取 skillType (spec.skillForm) 并转换为 SkillForm
            String skillType = manifest.getSkillType();
            if (skillType != null && !skillType.isEmpty()) {
                net.ooder.skills.api.SkillForm form = net.ooder.skills.api.SkillForm.fromCode(skillType);
                if (form != null) {
                    pkg.setForm(form);
                    log.debug("Loaded spec.skillForm: {} for skill: {}", skillType, pkg.getSkillId());
                } else {
                    log.warn("Unknown skillForm '{}' for skill: {}, using default", skillType, pkg.getSkillId());
                }
            } else {
                log.warn("Skill {} has no spec.skillForm, using fallback inference", pkg.getSkillId());
            }

            pkg.setSource(source + ":" + defaultOwner + "/" + defaultRepo);
            pkg.setResourcePath(path.substring(0, path.lastIndexOf('/')));

            log.debug("Parsed skill package: {} v{}, form: {}", pkg.getSkillId(), pkg.getVersion(), pkg.getForm());
            return pkg;

        } catch (IOException e) {
            log.warn("Failed to parse skill.yaml at {}: {}", path, e.getMessage());
            return null;
        }
    }
    
    private String findSkillYamlPath(String skillId, String token) throws Exception {
        String apiBase = getApiBase();
        String treeUrl = buildTreeUrl(apiBase);
        
        JsonNode tree = fetchRepositoryTree(treeUrl, token);
        
        for (JsonNode node : tree) {
            String nodePath = node.get("path").asText();
            if (nodePath.endsWith("skill.yaml") || nodePath.endsWith("skill.yml")) {
                String dir = nodePath.substring(0, nodePath.lastIndexOf('/'));
                String dirName = dir.contains("/") ? dir.substring(dir.lastIndexOf('/') + 1) : dir;
                
                if (skillId.equals(dirName) || nodePath.contains("/" + skillId + "/")) {
                    return nodePath;
                }
            }
        }
        
        return null;
    }
    
    private boolean applyFilter(SkillPackage pkg) {
        if (filter == null) {
            return true;
        }
        
        if (filter.getSceneId() != null && !filter.getSceneId().equals(pkg.getSceneId())) {
            return false;
        }
        if (filter.getCategory() != null && !filter.getCategory().equals(pkg.getCategory())) {
            return false;
        }
        if (filter.getSubCategory() != null && !filter.getSubCategory().equals(pkg.getSubCategory())) {
            return false;
        }
        if (filter.getVersion() != null && !filter.getVersion().equals(pkg.getVersion())) {
            return false;
        }
        if (filter.getTags() != null && !filter.getTags().isEmpty() && !hasAnyTag(pkg, filter.getTags())) {
            return false;
        }
        
        return true;
    }
    
    private boolean matchesQuery(SkillPackage pkg, String lowerQuery) {
        if (pkg.getSkillId() != null && pkg.getSkillId().toLowerCase().contains(lowerQuery)) {
            return true;
        }
        if (pkg.getName() != null && pkg.getName().toLowerCase().contains(lowerQuery)) {
            return true;
        }
        if (pkg.getDescription() != null && pkg.getDescription().toLowerCase().contains(lowerQuery)) {
            return true;
        }
        if (pkg.getCategory() != null && pkg.getCategory().toLowerCase().contains(lowerQuery)) {
            return true;
        }
        return false;
    }
    
    private boolean hasCapability(SkillPackage pkg, String capabilityId) {
        if (pkg.getCapabilities() != null) {
            for (var cap : pkg.getCapabilities()) {
                if (capabilityId.equals(cap.getCapId())) {
                    return true;
                }
            }
        }
        if (pkg.getManifest() != null && pkg.getManifest().getCapabilities() != null) {
            for (var cap : pkg.getManifest().getCapabilities()) {
                if (capabilityId.equals(cap.getCapId())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean hasAnyTag(SkillPackage pkg, List<String> tags) {
        if (pkg.getTags() == null || pkg.getTags().isEmpty()) {
            return false;
        }
        for (String tag : tags) {
            if (pkg.getTags().contains(tag)) {
                return true;
            }
        }
        return false;
    }
    
    private String buildCacheKey(String operation, String param) {
        return source + ":" + defaultOwner + "/" + defaultRepo + ":" + defaultBranch + ":" + operation + ":" + (param != null ? param : "");
    }
    
    @SuppressWarnings("unchecked")
    private List<SkillPackage> getFromCache(String key) {
        CacheEntry entry = cache.get(key);
        if (entry != null && !entry.isExpired()) {
            return new ArrayList<>((List<SkillPackage>) entry.value);
        }
        return null;
    }
    
    private void putToCache(String key, List<SkillPackage> value) {
        cache.put(key, new CacheEntry(new ArrayList<>(value), cacheTtlMs));
    }
    
    private static class CacheEntry {
        final Object value;
        final Instant expiresAt;
        
        CacheEntry(Object value, long ttlMs) {
            this.value = value;
            this.expiresAt = Instant.now().plusMillis(ttlMs);
        }
        
        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }
    
    public static class GitRepositoryConfig {
        private String owner;
        private String repo;
        private String branch = "main";
        private String token;
        private String baseUrl;
        
        public String getOwner() { return owner; }
        public void setOwner(String owner) { this.owner = owner; }
        
        public String getRepo() { return repo; }
        public void setRepo(String repo) { this.repo = repo; }
        
        public String getBranch() { return branch; }
        public void setBranch(String branch) { this.branch = branch; }
        
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    }
}
