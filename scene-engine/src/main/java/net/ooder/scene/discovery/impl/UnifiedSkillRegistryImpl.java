package net.ooder.scene.discovery.impl;

import net.ooder.scene.discovery.UnifiedSkillRegistry;
import net.ooder.skills.api.SkillPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 统一Skill注册中心实现
 *
 * @author ooder
 * @since 2.3.1
 */
public class UnifiedSkillRegistryImpl implements UnifiedSkillRegistry {

    private static final Logger logger = LoggerFactory.getLogger(UnifiedSkillRegistryImpl.class);

    private final Map<String, SkillPackage> skills = new ConcurrentHashMap<>();
    private final Map<String, ChannelConfig> channels = new ConcurrentHashMap<>();
    private final Map<String, List<String>> skillChannels = new ConcurrentHashMap<>();

    @Override
    public CompletableFuture<RegisterResult> register(String channelId, List<SkillPackage> packages) {
        return CompletableFuture.supplyAsync(() -> {
            RegisterResult result = new RegisterResult();
            result.setTimestamp(System.currentTimeMillis());

            try {
                int newCount = 0;
                int updatedCount = 0;
                int duplicateCount = 0;
                List<String> errors = new ArrayList<>();

                for (SkillPackage pkg : packages) {
                    try {
                        String skillId = pkg.getSkillId();
                        SkillPackage existing = skills.get(skillId);

                        if (existing == null) {
                            skills.put(skillId, pkg);
                            newCount++;
                        } else {
                            if (isNewerVersion(pkg, existing)) {
                                skills.put(skillId, pkg);
                                updatedCount++;
                            } else {
                                duplicateCount++;
                            }
                        }

                        skillChannels.computeIfAbsent(skillId, k -> new ArrayList<>());
                        if (!skillChannels.get(skillId).contains(channelId)) {
                            skillChannels.get(skillId).add(channelId);
                        }
                    } catch (Exception e) {
                        errors.add("Failed to register skill: " + pkg.getSkillId() + " - " + e.getMessage());
                    }
                }

                result.setSuccess(true);
                result.setTotalCount(packages.size());
                result.setNewCount(newCount);
                result.setUpdatedCount(updatedCount);
                result.setDuplicateCount(duplicateCount);
                result.setErrors(errors);

                logger.info("Registered {} skills from channel {}: new={}, updated={}, duplicate={}",
                    packages.size(), channelId, newCount, updatedCount, duplicateCount);

            } catch (Exception e) {
                result.setSuccess(false);
                result.setErrors(Arrays.asList("Registration failed: " + e.getMessage()));
                logger.error("Failed to register skills from channel: " + channelId, e);
            }

            return result;
        });
    }

    @Override
    public CompletableFuture<RegisterResult> register(String channelId, SkillPackage pkg) {
        return register(channelId, Arrays.asList(pkg));
    }

    @Override
    public CompletableFuture<List<SkillPackage>> getAllSkills() {
        return CompletableFuture.supplyAsync(() -> {
            return new ArrayList<>(skills.values());
        });
    }

    @Override
    public CompletableFuture<List<SkillPackage>> getSkillsByChannel(String channelId) {
        return CompletableFuture.supplyAsync(() -> {
            return skills.values().stream()
                .filter(skill -> {
                    List<String> channels = skillChannels.get(skill.getSkillId());
                    return channels != null && channels.contains(channelId);
                })
                .collect(Collectors.toList());
        });
    }

    @Override
    public CompletableFuture<SkillPackage> getSkill(String skillId) {
        return CompletableFuture.supplyAsync(() -> {
            return skills.get(skillId);
        });
    }

    @Override
    public CompletableFuture<SkillPackage> getSkill(String skillId, String version) {
        return CompletableFuture.supplyAsync(() -> {
            SkillPackage skill = skills.get(skillId);
            if (skill != null && version.equals(skill.getVersion())) {
                return skill;
            }
            return null;
        });
    }

    @Override
    public CompletableFuture<List<String>> getSkillVersions(String skillId) {
        return CompletableFuture.supplyAsync(() -> {
            SkillPackage skill = skills.get(skillId);
            if (skill != null) {
                return Arrays.asList(skill.getVersion());
            }
            return new ArrayList<>();
        });
    }

    @Override
    public CompletableFuture<List<String>> getSkillChannels(String skillId) {
        return CompletableFuture.supplyAsync(() -> {
            return skillChannels.getOrDefault(skillId, new ArrayList<>());
        });
    }

    @Override
    public CompletableFuture<List<SkillPackage>> searchSkills(String keyword) {
        return CompletableFuture.supplyAsync(() -> {
            String lowerKeyword = keyword.toLowerCase();
            return skills.values().stream()
                .filter(skill -> {
                    String name = skill.getName() != null ? skill.getName().toLowerCase() : "";
                    String desc = skill.getDescription() != null ? skill.getDescription().toLowerCase() : "";
                    return name.contains(lowerKeyword) || desc.contains(lowerKeyword);
                })
                .collect(Collectors.toList());
        });
    }

    @Override
    public CompletableFuture<List<SkillPackage>> searchByTag(String tag) {
        return CompletableFuture.supplyAsync(() -> {
            return skills.values().stream()
                .filter(skill -> {
                    List<String> tags = skill.getTags();
                    return tags != null && tags.contains(tag);
                })
                .collect(Collectors.toList());
        });
    }

    @Override
    public CompletableFuture<List<DiscoveryHistory>> getDiscoveryHistory(String channelId, int limit) {
        return CompletableFuture.supplyAsync(() -> {
            return new ArrayList<>();
        });
    }

    @Override
    public CompletableFuture<List<SkillHistory>> getSkillHistory(String skillId) {
        return CompletableFuture.supplyAsync(() -> {
            return new ArrayList<>();
        });
    }

    @Override
    public CompletableFuture<String> addChannel(ChannelConfig channelConfig) {
        return CompletableFuture.supplyAsync(() -> {
            String channelId = channelConfig.getChannelId();
            channels.put(channelId, channelConfig);
            logger.info("Added channel: {}", channelId);
            return channelId;
        });
    }

    @Override
    public CompletableFuture<Boolean> removeChannel(String channelId) {
        return CompletableFuture.supplyAsync(() -> {
            ChannelConfig removed = channels.remove(channelId);
            if (removed != null) {
                logger.info("Removed channel: {}", channelId);
                return true;
            }
            return false;
        });
    }

    @Override
    public CompletableFuture<List<ChannelConfig>> getAllChannels() {
        return CompletableFuture.supplyAsync(() -> {
            return new ArrayList<>(channels.values());
        });
    }

    @Override
    public CompletableFuture<RefreshResult> refreshChannel(String channelId) {
        return CompletableFuture.supplyAsync(() -> {
            RefreshResult result = new RefreshResult();
            result.setChannelId(channelId);
            result.setSuccess(true);
            result.setMessage("Channel refreshed");
            return result;
        });
    }

    @Override
    public CompletableFuture<List<RefreshResult>> refreshAllChannels() {
        return CompletableFuture.supplyAsync(() -> {
            List<RefreshResult> results = new ArrayList<>();
            for (String channelId : channels.keySet()) {
                results.add(new RefreshResult());
            }
            return results;
        });
    }

    @Override
    public CompletableFuture<RegistryStats> getStats() {
        return CompletableFuture.supplyAsync(() -> {
            RegistryStats stats = new RegistryStats();
            stats.setTotalChannels(channels.size());
            stats.setActiveChannels(channels.size());
            stats.setTotalSkills(skills.size());
            stats.setUniqueSkills(skills.size());
            stats.setLastUpdateTime(System.currentTimeMillis());
            return stats;
        });
    }

    @Override
    public CompletableFuture<String> exportRegistry(String format) {
        return CompletableFuture.supplyAsync(() -> {
            return "";
        });
    }

    @Override
    public CompletableFuture<ImportResult> importRegistry(String content, String format) {
        return CompletableFuture.supplyAsync(() -> {
            ImportResult result = new ImportResult();
            result.setSuccess(false);
            result.setErrors(Arrays.asList("Import not implemented"));
            return result;
        });
    }

    private boolean isNewerVersion(SkillPackage newSkill, SkillPackage existingSkill) {
        String newVersion = newSkill.getVersion();
        String existingVersion = existingSkill.getVersion();
        return newVersion != null && !newVersion.equals(existingVersion);
    }
}
