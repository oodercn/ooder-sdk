package net.ooder.scene.discovery.adapter;

import net.ooder.scene.discovery.CapabilityDTO;
import net.ooder.scene.discovery.api.DiscoveryRequest;
import net.ooder.scene.discovery.api.DiscoveryService;
import net.ooder.scene.discovery.api.DiscoveryResult;
import net.ooder.skills.api.SkillPackage;
import net.ooder.skills.core.discovery.LocalDiscoverer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 本地技能发现器适配器
 *
 * <p>将 skills-framework 的 LocalDiscoverer 适配到统一的 SkillDiscovererAdapter 接口。</p>
 *
 * <h3>功能特性：</h3>
 * <ul>
 *   <li>支持本地文件系统技能发现</li>
 *   <li>支持自定义搜索路径</li>
 *   <li>支持 Dev 模式目录</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
@Component
public class LocalSkillDiscovererAdapter implements SkillDiscovererAdapter {

    private static final Logger logger = LoggerFactory.getLogger(LocalSkillDiscovererAdapter.class);

    private final LocalDiscoverer localDiscoverer;

    @Value("${scene.engine.discovery.local.enabled:true}")
    private boolean enabled;

    @Value("${scene.engine.discovery.local.skills-path:./skills}")
    private String skillsPath;

    @Value("${scene.engine.discovery.local.search-paths:./skills,./.ooder/downloads,./.ooder/installed,./.ooder/dev}")
    private String searchPaths;

    @Autowired(required = false)
    public LocalSkillDiscovererAdapter() {
        this.localDiscoverer = new LocalDiscoverer();
    }

    public LocalSkillDiscovererAdapter(LocalDiscoverer localDiscoverer) {
        this.localDiscoverer = localDiscoverer != null ? localDiscoverer : new LocalDiscoverer();
    }

    @Override
    public CompletableFuture<DiscoveryResult> discover(DiscoveryRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            DiscoveryResult result = new DiscoveryResult();
            result.setSource(getMethod().getCode());
            result.setTimestamp(System.currentTimeMillis());

            try {
                logger.info("[LocalDiscoverer] Starting local discovery from: {}", skillsPath);

                if (localDiscoverer == null) {
                    logger.warn("[LocalDiscoverer] LocalDiscoverer not available");
                    result.setErrorMessage("LocalDiscoverer not available");
                    return result;
                }

                List<SkillPackage> packages = localDiscoverer.discover().get();

                List<DiscoveryService.SkillInfo> skills = new ArrayList<>();
                for (SkillPackage pkg : packages) {
                    skills.add(convertToSkillInfo(pkg));
                }

                result.setSkills(skills);
                result.setTotalCount(skills.size());

                logger.info("[LocalDiscoverer] Discovery completed: found {} skills", skills.size());

            } catch (Exception e) {
                logger.error("[LocalDiscoverer] Discovery failed: {}", e.getMessage(), e);
                result.setErrorMessage(e.getMessage());
            }

            return result;
        });
    }

    @Override
    public CompletableFuture<CapabilityDTO> discoverOne(String skillId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (localDiscoverer == null) {
                    return null;
                }

                List<SkillPackage> packages = localDiscoverer.discover().get();
                for (SkillPackage pkg : packages) {
                    if (skillId.equals(pkg.getSkillId())) {
                        return convertToCapabilityDTO(pkg);
                    }
                }
            } catch (Exception e) {
                logger.error("[LocalDiscoverer] Failed to discover skill: {}", skillId, e);
            }
            return null;
        });
    }

    @Override
    public DiscoveryMethod getMethod() {
        return DiscoveryMethod.LOCAL;
    }

    @Override
    public boolean isAvailable() {
        return enabled;
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public String getName() {
        return "本地技能发现器";
    }

    private DiscoveryService.SkillInfo convertToSkillInfo(SkillPackage pkg) {
        DiscoveryService.SkillInfo info = new DiscoveryService.SkillInfo();
        info.setSkillId(pkg.getSkillId());
        info.setName(pkg.getName());
        info.setVersion(pkg.getVersion());
        info.setDescription(pkg.getDescription());
        info.setCategory(pkg.getCategory());
        info.setTags(pkg.getTags());
        info.setSource("local");
        info.setInstalled(true);
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
        dto.setSource("local");
        dto.setInstalled(true);
        return dto;
    }
}
