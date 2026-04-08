package net.ooder.scene.discovery.adapter;

import net.ooder.scene.discovery.CapabilityDTO;
import net.ooder.scene.discovery.GiteeDiscoveryConfig;
import net.ooder.scene.discovery.UnifiedDiscoveryService;
import net.ooder.scene.discovery.api.DiscoveryRequest;
import net.ooder.scene.discovery.api.DiscoveryResult;
import net.ooder.scene.discovery.api.DiscoveryService;
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
 * Gitee 技能发现器适配器
 *
 * <p>将 UnifiedDiscoveryService 适配到统一的 SkillDiscovererAdapter 接口。</p>
 *
 * <h3>功能特性：</h3>
 * <ul>
 *   <li>支持 Gitee 仓库技能发现</li>
 *   <li>支持 Token 认证</li>
 *   <li>支持自定义仓库和分支</li>
 *   <li>支持缓存机制</li>
 * </ul>
 *
 * <h3>配置示例：</h3>
 * <pre>
 * scene:
 *   engine:
 *     discovery:
 *       gitee:
 *         enabled: true
 *         token: ${GITEE_TOKEN:}
 *         default-owner: ooderCN
 *         default-repo: skills
 *         default-branch: master
 * </pre>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
@Component
public class GiteeSkillDiscovererAdapter implements SkillDiscovererAdapter {

    private static final Logger logger = LoggerFactory.getLogger(GiteeSkillDiscovererAdapter.class);

    private final UnifiedDiscoveryService unifiedDiscoveryService;
    private final GiteeDiscoveryConfig config;

    @Value("${scene.engine.discovery.gitee.token:}")
    private String token;

    @Value("${scene.engine.discovery.gitee.default-owner:ooderCN}")
    private String defaultOwner;

    @Value("${scene.engine.discovery.gitee.default-repo:skills}")
    private String defaultRepo;

    @Value("${scene.engine.discovery.gitee.default-branch:master}")
    private String defaultBranch;

    @Value("${scene.engine.discovery.gitee.enabled:true}")
    private boolean enabled;

    @Autowired(required = false)
    public GiteeSkillDiscovererAdapter(UnifiedDiscoveryService unifiedDiscoveryService) {
        this.unifiedDiscoveryService = unifiedDiscoveryService;
        this.config = new GiteeDiscoveryConfig();
    }

    public GiteeSkillDiscovererAdapter(UnifiedDiscoveryService unifiedDiscoveryService, 
                                        GiteeDiscoveryConfig config) {
        this.unifiedDiscoveryService = unifiedDiscoveryService;
        this.config = config != null ? config : new GiteeDiscoveryConfig();
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
                String branch = getConfigValue(config.getBranch(), defaultBranch);
                String giteeToken = getConfigValue(config.getToken(), token);

                String repositoryUrl = String.format("https://gitee.com/%s/%s", owner, repo);
                
                logger.info("[GiteeDiscoverer] Starting discovery: owner={}, repo={}, branch={}", 
                    owner, repo, branch);

                if (unifiedDiscoveryService == null) {
                    logger.warn("[GiteeDiscoverer] UnifiedDiscoveryService not available");
                    result.setErrorMessage("UnifiedDiscoveryService not available");
                    return result;
                }

                CompletableFuture<List<SkillPackage>> future = 
                    unifiedDiscoveryService.discoverSkills(repositoryUrl, config.getSkillsPath());
                
                List<SkillPackage> packages = future.get();
                
                List<DiscoveryService.SkillInfo> skills = new ArrayList<>();
                for (SkillPackage pkg : packages) {
                    skills.add(convertToSkillInfo(pkg));
                }

                result.setSkills(skills);
                result.setTotalCount(skills.size());
                
                logger.info("[GiteeDiscoverer] Discovery completed: found {} skills", skills.size());
                
            } catch (Exception e) {
                logger.error("[GiteeDiscoverer] Discovery failed: {}", e.getMessage(), e);
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
                String repositoryUrl = String.format("https://gitee.com/%s/%s", owner, repo);

                if (unifiedDiscoveryService == null) {
                    return null;
                }

                SkillPackage pkg = unifiedDiscoveryService.discoverSkill(repositoryUrl, skillId).get();
                if (pkg != null) {
                    return convertToCapabilityDTO(pkg);
                }
            } catch (Exception e) {
                logger.error("[GiteeDiscoverer] Failed to discover skill: {}", skillId, e);
            }
            return null;
        });
    }

    @Override
    public DiscoveryMethod getMethod() {
        return DiscoveryMethod.GITEE;
    }

    @Override
    public boolean isAvailable() {
        if (!enabled) {
            logger.debug("[GiteeDiscoverer] Disabled by configuration");
            return false;
        }
        String giteeToken = getConfigValue(config.getToken(), token);
        boolean available = giteeToken != null && !giteeToken.isEmpty();
        logger.debug("[GiteeDiscoverer] Available: {}", available);
        return available;
    }

    @Override
    public int getPriority() {
        return 50;
    }

    @Override
    public String getName() {
        return "Gitee Discoverer";
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
        info.setSource("gitee");
        return info;
    }

    private CapabilityDTO convertToCapabilityDTO(SkillPackage pkg) {
        CapabilityDTO dto = new CapabilityDTO();
        dto.setId(pkg.getSkillId());
        dto.setName(pkg.getName());
        dto.setVersion(pkg.getVersion());
        dto.setDescription(pkg.getDescription());
        dto.setCategory(pkg.getCategory());
        dto.setTags(pkg.getTags());
        dto.setSource("gitee");
        return dto;
    }
}
