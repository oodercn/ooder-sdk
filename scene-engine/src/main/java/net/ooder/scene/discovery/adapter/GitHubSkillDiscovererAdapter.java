package net.ooder.scene.discovery.adapter;

import net.ooder.scene.discovery.CapabilityDTO;
import net.ooder.scene.discovery.GithubDiscoveryConfig;
import net.ooder.scene.discovery.UnifiedDiscoveryService;
import net.ooder.scene.discovery.api.DiscoveryRequest;
import net.ooder.scene.discovery.api.DiscoveryService;
import net.ooder.scene.discovery.api.DiscoveryResult;
import net.ooder.skills.api.SkillPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * GitHub 技能发现器适配器
 *
 * <p>将 UnifiedDiscoveryService 适配到统一的 SkillDiscovererAdapter 接口。</p>
 *
 * <h3>功能特性：</h3>
 * <ul>
 *   <li>支持 GitHub 仓库技能发现</li>
 *   <li>支持 Token 认证</li>
 *   <li>支持自定义仓库和分支配置</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
@Component
public class GitHubSkillDiscovererAdapter implements SkillDiscovererAdapter {

    private static final Logger logger = LoggerFactory.getLogger(GitHubSkillDiscovererAdapter.class);

    private final UnifiedDiscoveryService unifiedDiscoveryService;
    private final GithubDiscoveryConfig config;

    @Value("${scene.engine.discovery.github.token:}")
    private String token;

    @Value("${scene.engine.discovery.github.default-owner:}")
    private String defaultOwner;

    @Value("${scene.engine.discovery.github.default-repo:}")
    private String defaultRepo;

    @Value("${scene.engine.discovery.github.enabled:false}")
    private boolean enabled;

    @Autowired(required = false)
    public GitHubSkillDiscovererAdapter(UnifiedDiscoveryService unifiedDiscoveryService) {
        this.unifiedDiscoveryService = unifiedDiscoveryService;
        this.config = new GithubDiscoveryConfig();
    }

    public GitHubSkillDiscovererAdapter(UnifiedDiscoveryService unifiedDiscoveryService,
                                         GithubDiscoveryConfig config) {
        this.unifiedDiscoveryService = unifiedDiscoveryService;
        this.config = config != null ? config : new GithubDiscoveryConfig();
    }

    @Override
    public CompletableFuture<DiscoveryResult> discover(DiscoveryRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            DiscoveryResult result = new DiscoveryResult();
            result.setSource(getMethod().getCode());
            result.setTimestamp(System.currentTimeMillis());

            try {
                String owner = getConfigValue(config.getOwner(), defaultOwner);
                String repo = getConfigValue(config.getRepo(), defaultRepo);
                String githubToken = getConfigValue(config.getToken(), token);

                if (owner == null || owner.isEmpty() || repo == null || repo.isEmpty()) {
                    logger.warn("[GitHubDiscoverer] Owner or repo not configured");
                    result.setErrorMessage("GitHub owner or repo not configured");
                    return result;
                }

                String repositoryUrl = String.format("https://github.com/%s/%s", owner, repo);

                logger.info("[GitHubDiscoverer] Starting discovery: owner={}, repo={}", owner, repo);

                if (unifiedDiscoveryService == null) {
                    logger.warn("[GitHubDiscoverer] UnifiedDiscoveryService not available");
                    result.setErrorMessage("UnifiedDiscoveryService not available");
                    return result;
                }

                CompletableFuture<List<SkillPackage>> future =
                    unifiedDiscoveryService.discoverSkills(repositoryUrl);
                
                List<SkillPackage> packages = future.get();

                List<DiscoveryService.SkillInfo> skills = new ArrayList<>();
                for (SkillPackage pkg : packages) {
                    skills.add(convertToSkillInfo(pkg));
                }

                result.setSkills(skills);
                result.setTotalCount(skills.size());

                logger.info("[GitHubDiscoverer] Discovery completed: found {} skills", skills.size());

            } catch (Exception e) {
                logger.error("[GitHubDiscoverer] Discovery failed: {}", e.getMessage(), e);
                result.setErrorMessage(e.getMessage());
            }

            return result;
        });
    }

    @Override
    public CompletableFuture<CapabilityDTO> discoverOne(String skillId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String owner = getConfigValue(config.getOwner(), defaultOwner);
                String repo = getConfigValue(config.getRepo(), defaultRepo);

                if (owner == null || repo == null) {
                    return null;
                }

                String repositoryUrl = String.format("https://github.com/%s/%s", owner, repo);

                if (unifiedDiscoveryService == null) {
                    return null;
                }

                SkillPackage pkg = unifiedDiscoveryService.discoverSkill(repositoryUrl, skillId).get();
                if (pkg != null) {
                    return convertToCapabilityDTO(pkg);
                }
            } catch (Exception e) {
                logger.error("[GitHubDiscoverer] Failed to discover skill: {}", skillId, e);
            }
            return null;
        });
    }

    @Override
    public DiscoveryMethod getMethod() {
        return DiscoveryMethod.GITHUB;
    }

    @Override
    public boolean isAvailable() {
        if (!enabled) {
            logger.debug("[GitHubDiscoverer] Disabled by configuration");
            return false;
        }
        String githubToken = getConfigValue(config.getToken(), token);
        boolean available = githubToken != null && !githubToken.isEmpty();
        logger.debug("[GitHubDiscoverer] Available: {}", available);
        return available;
    }

    @Override
    public String getName() {
        return "GitHub 技能发现器";
    }

    private String getConfigValue(String configValue, String defaultValue) {
        if (configValue != null && !configValue.isEmpty()) {
            return configValue;
        }
        return defaultValue;
    }

    private DiscoveryService.SkillInfo convertToSkillInfo(SkillPackage pkg) {
        DiscoveryService.SkillInfo info = new DiscoveryService.SkillInfo();
        info.setSkillId(pkg.getSkillId());
        info.setName(pkg.getName());
        info.setVersion(pkg.getVersion());
        info.setDescription(pkg.getDescription());
        info.setCategory(pkg.getCategory());
        info.setTags(pkg.getTags());
        info.setSource("github");
        return info;
    }

    private CapabilityDTO convertToCapabilityDTO(SkillPackage pkg) {
        CapabilityDTO dto = new CapabilityDTO();
        dto.setId(pkg.getSkillId());
        dto.setSkillId(pkg.getSkillId());
        dto.setName(pkg.getName());
        dto.setVersion(pkg.getVersion());
        dto.setDescription(pkg.getDescription());
        dto.setCategory(pkg.getCategory());
        dto.setTags(pkg.getTags());
        dto.setSource("github");
        return dto;
    }
}
