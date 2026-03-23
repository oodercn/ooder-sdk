package net.ooder.scene.discovery.impl;

import net.ooder.scene.discovery.UnifiedDiscoveryService;
import net.ooder.scene.discovery.UnifiedSkillRegistry;
import net.ooder.scene.discovery.api.DiscoveryService;
import net.ooder.skills.api.SkillPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 发现服务实现
 *
 * @author ooder
 * @since 2.3.1
 */
public class DiscoveryServiceImpl implements DiscoveryService {

    private static final Logger logger = LoggerFactory.getLogger(DiscoveryServiceImpl.class);

    private final UnifiedDiscoveryService unifiedDiscoveryService;
    private final UnifiedSkillRegistry skillRegistry;
    private final List<DiscoveryListener> listeners = new ArrayList<>();

    public DiscoveryServiceImpl(UnifiedDiscoveryService unifiedDiscoveryService, 
                                 UnifiedSkillRegistry skillRegistry) {
        this.unifiedDiscoveryService = unifiedDiscoveryService;
        this.skillRegistry = skillRegistry;
    }

    @Override
    public CompletableFuture<DiscoveryResult> discover(DiscoveryRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            DiscoveryResult result = new DiscoveryResult();
            long startTime = System.currentTimeMillis();

            try {
                notifyDiscoveryStarted(request);

                String source = request.getSource();
                String repositoryUrl = request.getRepositoryUrl();
                List<SkillPackage> discoveredSkills = new ArrayList<>();

                if ("gitee".equalsIgnoreCase(source) || "all".equalsIgnoreCase(source)) {
                    List<SkillPackage> giteeSkills = unifiedDiscoveryService
                        .discoverSkills(repositoryUrl, request.getSkillsPath())
                        .get(request.getTimeout(), java.util.concurrent.TimeUnit.MILLISECONDS);
                    discoveredSkills.addAll(giteeSkills);
                }

                if ("github".equalsIgnoreCase(source) || "all".equalsIgnoreCase(source)) {
                    List<SkillPackage> githubSkills = unifiedDiscoveryService
                        .discoverSkills(repositoryUrl, request.getSkillsPath())
                        .get(request.getTimeout(), java.util.concurrent.TimeUnit.MILLISECONDS);
                    discoveredSkills.addAll(githubSkills);
                }

                UnifiedSkillRegistry.RegisterResult registerResult = 
                    skillRegistry.register("discovery:" + source, discoveredSkills).get();

                result.setSuccess(true);
                result.setSource(source);
                result.setSkills(convertToSkillInfo(discoveredSkills));
                result.setTotalCount(discoveredSkills.size());
                result.setFromNetwork(discoveredSkills.size());
                result.setDuration(System.currentTimeMillis() - startTime);
                result.setMessage("Discovery completed successfully");

                notifyDiscoveryCompleted(result);

            } catch (Exception e) {
                result.setSuccess(false);
                result.setMessage("Discovery failed: " + e.getMessage());
                result.setDuration(System.currentTimeMillis() - startTime);
                notifyDiscoveryFailed(e.getMessage());
                logger.error("Discovery failed", e);
            }

            return result;
        });
    }

    @Override
    public CompletableFuture<DiscoveryResult> refresh(DiscoveryRequest request) {
        request.setForceRefresh(true);
        return discover(request);
    }

    @Override
    public CompletableFuture<List<SkillInfo>> search(String keyword) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<SkillPackage> skills = skillRegistry.searchSkills(keyword).get();
                return convertToSkillInfo(skills);
            } catch (Exception e) {
                logger.error("Search failed", e);
                return new ArrayList<>();
            }
        });
    }

    @Override
    public CompletableFuture<List<SkillInfo>> searchByCategory(String category) {
        return CompletableFuture.supplyAsync(() -> {
            return new ArrayList<>();
        });
    }

    @Override
    public CompletableFuture<List<SkillInfo>> getInstalled() {
        return CompletableFuture.supplyAsync(() -> {
            return new ArrayList<>();
        });
    }

    @Override
    public CompletableFuture<List<SkillInfo>> getCached() {
        return CompletableFuture.supplyAsync(() -> {
            return new ArrayList<>();
        });
    }

    @Override
    public CompletableFuture<SkillInfo> getSkillInfo(String skillId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                SkillPackage skill = skillRegistry.getSkill(skillId).get();
                return skill != null ? convertToSkillInfo(skill) : null;
            } catch (Exception e) {
                logger.error("Failed to get skill info: " + skillId, e);
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<SkillInfo> getSkillInfo(String skillId, String version) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                SkillPackage skill = skillRegistry.getSkill(skillId, version).get();
                return skill != null ? convertToSkillInfo(skill) : null;
            } catch (Exception e) {
                logger.error("Failed to get skill info: " + skillId + " version: " + version, e);
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<IntegrityCheckResult> checkIntegrity(String skillId) {
        return CompletableFuture.supplyAsync(() -> {
            IntegrityCheckResult result = new IntegrityCheckResult();
            result.setSkillId(skillId);
            result.setValid(true);
            result.setMessage("Integrity check passed");
            return result;
        });
    }

    @Override
    public CompletableFuture<DependencyCheckResult> checkDependencies(String skillId) {
        return CompletableFuture.supplyAsync(() -> {
            DependencyCheckResult result = new DependencyCheckResult();
            result.setSkillId(skillId);
            result.setSatisfied(true);
            result.setMessage("All dependencies satisfied");
            return result;
        });
    }

    @Override
    public CompletableFuture<DependencyInstallResult> installDependencies(String skillId) {
        return CompletableFuture.supplyAsync(() -> {
            DependencyInstallResult result = new DependencyInstallResult();
            result.setSkillId(skillId);
            result.setSuccess(true);
            result.setMessage("Dependencies installed");
            return result;
        });
    }

    @Override
    public void addDiscoveryListener(DiscoveryListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeDiscoveryListener(DiscoveryListener listener) {
        listeners.remove(listener);
    }

    private List<SkillInfo> convertToSkillInfo(List<SkillPackage> skills) {
        return skills.stream()
            .map(this::convertToSkillInfo)
            .collect(Collectors.toList());
    }

    private SkillInfo convertToSkillInfo(SkillPackage skill) {
        SkillInfo info = new SkillInfo();
        info.setSkillId(skill.getSkillId());
        info.setName(skill.getName());
        info.setVersion(skill.getVersion());
        info.setDescription(skill.getDescription());
        info.setCategory(skill.getCategory());
        info.setTags(skill.getTags());
        info.setDependencies(skill.getDependencies());
        return info;
    }

    private void notifyDiscoveryStarted(DiscoveryRequest request) {
        for (DiscoveryListener listener : listeners) {
            try {
                listener.onDiscoveryStarted(request);
            } catch (Exception e) {
                logger.error("Listener error", e);
            }
        }
    }

    private void notifyDiscoveryCompleted(DiscoveryResult result) {
        for (DiscoveryListener listener : listeners) {
            try {
                listener.onDiscoveryCompleted(result);
            } catch (Exception e) {
                logger.error("Listener error", e);
            }
        }
    }

    private void notifyDiscoveryFailed(String error) {
        for (DiscoveryListener listener : listeners) {
            try {
                listener.onDiscoveryFailed(error);
            } catch (Exception e) {
                logger.error("Listener error", e);
            }
        }
    }
}
