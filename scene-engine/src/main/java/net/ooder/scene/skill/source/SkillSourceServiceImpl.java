package net.ooder.scene.skill.source;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import net.ooder.scene.core.InstalledSkillInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 技能来源服务实现
 *
 * @author Ooder Team
 * @version 3.0
 * @since 3.0.1
 */
@Service
public class SkillSourceServiceImpl implements SkillSourceService {

    private static final Logger logger = LoggerFactory.getLogger(SkillSourceServiceImpl.class);

    private static final String DEFAULT_DATA_DIR = "./.ooder/data/skill_sources";
    private static final String SOURCE_FILE = "skill_install_sources.json";

    private final String dataDir;
    private final Map<String, InstalledSkillInfo> sourceCache = new ConcurrentHashMap<>();

    public SkillSourceServiceImpl() {
        this(DEFAULT_DATA_DIR);
    }

    public SkillSourceServiceImpl(String dataDir) {
        this.dataDir = dataDir;
        initDataDir();
        loadFromFile();
    }

    private void initDataDir() {
        try {
            Files.createDirectories(Paths.get(dataDir));
            logger.info("Skill source data directory initialized: {}", dataDir);
        } catch (IOException e) {
            logger.error("Failed to create data directory: " + dataDir, e);
        }
    }

    @Override
    public List<InstalledSkillInfo> getSkillsBySource(String source) {
        return sourceCache.values().stream()
                .filter(info -> source.equals(info.getInstallSource()))
                .collect(Collectors.toList());
    }

    @Override
    public List<InstalledSkillInfo> getSkillsByInstaller(String userId) {
        return sourceCache.values().stream()
                .filter(info -> userId.equals(info.getInstalledBy()))
                .collect(Collectors.toList());
    }

    @Override
    public List<InstalledSkillInfo> getSkillsBySharer(String userId) {
        return sourceCache.values().stream()
                .filter(info -> userId.equals(info.getSharedBy()))
                .collect(Collectors.toList());
    }

    @Override
    public List<InstalledSkillInfo> getSkillsByDelegator(String userId) {
        return sourceCache.values().stream()
                .filter(info -> userId.equals(info.getDelegatedBy()))
                .collect(Collectors.toList());
    }

    @Override
    public void recordInstallSource(String skillId, String source, Map<String, Object> metadata) {
        InstalledSkillInfo info = sourceCache.computeIfAbsent(skillId, k -> new InstalledSkillInfo());
        info.setSkillId(skillId);
        info.setInstallSource(source);
        info.setInstalledAt(System.currentTimeMillis());
        if (metadata != null) {
            info.setSourceMetadata(metadata);
            if (metadata.containsKey("installedBy")) {
                info.setInstalledBy(String.valueOf(metadata.get("installedBy")));
            }
            if (metadata.containsKey("sharedBy")) {
                info.setSharedBy(String.valueOf(metadata.get("sharedBy")));
            }
            if (metadata.containsKey("delegatedBy")) {
                info.setDelegatedBy(String.valueOf(metadata.get("delegatedBy")));
            }
            if (metadata.containsKey("pushTime")) {
                Object pushTime = metadata.get("pushTime");
                if (pushTime instanceof Long) {
                    info.setPushTime((Long) pushTime);
                } else if (pushTime instanceof Number) {
                    info.setPushTime(((Number) pushTime).longValue());
                }
            }
            if (metadata.containsKey("pushMessage")) {
                info.setPushMessage(String.valueOf(metadata.get("pushMessage")));
            }
        }
        saveToFile();
        logger.info("Recorded install source for skill {}: {}", skillId, source);
    }

    @Override
    public void updateSource(String skillId, String source, String fromUserId) {
        InstalledSkillInfo info = sourceCache.get(skillId);
        if (info == null) {
            info = new InstalledSkillInfo();
            info.setSkillId(skillId);
            info.setInstalledAt(System.currentTimeMillis());
            sourceCache.put(skillId, info);
        }
        info.setInstallSource(source);
        InstallSource installSource = InstallSource.fromCode(source);
        switch (installSource) {
            case SHARE:
                info.setSharedBy(fromUserId);
                break;
            case DELEGATE:
                info.setDelegatedBy(fromUserId);
                break;
            default:
                info.setInstalledBy(fromUserId);
        }
        saveToFile();
        logger.info("Updated source for skill {}: {} from {}", skillId, source, fromUserId);
    }

    @Override
    public CompletableFuture<Boolean> installWithSource(String skillId, String source, String installedBy) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Object> metadata = new HashMap<>();
                metadata.put("installedBy", installedBy);
                recordInstallSource(skillId, source, metadata);
                return true;
            } catch (Exception e) {
                logger.error("Failed to install skill {} with source {}", skillId, source, e);
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<ShareResult> shareSkill(String skillId, String fromUserId,
            List<String> toUserIds, String message) {
        return CompletableFuture.supplyAsync(() -> {
            ShareResult result = new ShareResult();
            result.setSkillId(skillId);
            result.setFromUserId(fromUserId);
            result.setToUserIds(toUserIds);
            result.setShareTime(System.currentTimeMillis());
            try {
                for (String toUserId : toUserIds) {
                    updateSource(skillId, InstallSource.SHARE.getCode(), fromUserId);
                }
                result.setSuccess(true);
                result.setMessage("技能分享成功");
                logger.info("Skill {} shared from {} to {}", skillId, fromUserId, toUserIds);
            } catch (Exception e) {
                result.setSuccess(false);
                result.setMessage("分享失败: " + e.getMessage());
                logger.error("Failed to share skill {}", skillId, e);
            }
            return result;
        });
    }

    @Override
    public CompletableFuture<DelegateResult> delegateSkill(String skillId, String fromUserId,
            List<String> toUserIds, Long deadline, String message) {
        return CompletableFuture.supplyAsync(() -> {
            DelegateResult result = new DelegateResult();
            result.setSkillId(skillId);
            result.setFromUserId(fromUserId);
            result.setToUserIds(toUserIds);
            result.setDeadline(deadline);
            result.setDelegateTime(System.currentTimeMillis());
            try {
                for (String toUserId : toUserIds) {
                    updateSource(skillId, InstallSource.DELEGATE.getCode(), fromUserId);
                }
                result.setSuccess(true);
                result.setMessage("技能委派成功");
                logger.info("Skill {} delegated from {} to {} with deadline {}",
                        skillId, fromUserId, toUserIds, deadline);
            } catch (Exception e) {
                result.setSuccess(false);
                result.setMessage("委派失败: " + e.getMessage());
                logger.error("Failed to delegate skill {}", skillId, e);
            }
            return result;
        });
    }

    @Override
    public CompletableFuture<PushResult> pushSkill(String skillId, List<String> toUserIds, String message) {
        return CompletableFuture.supplyAsync(() -> {
            PushResult result = new PushResult();
            result.setSkillId(skillId);
            result.setToUserIds(toUserIds);
            result.setPushTime(System.currentTimeMillis());
            try {
                InstalledSkillInfo info = sourceCache.computeIfAbsent(skillId, k -> new InstalledSkillInfo());
                info.setSkillId(skillId);
                info.setInstallSource(InstallSource.PUSH.getCode());
                info.setPushTime(result.getPushTime());
                info.setPushMessage(message);
                saveToFile();
                result.setSuccess(true);
                result.setMessage("技能推送成功");
                logger.info("Skill {} pushed to {}", skillId, toUserIds);
            } catch (Exception e) {
                result.setSuccess(false);
                result.setMessage("推送失败: " + e.getMessage());
                logger.error("Failed to push skill {}", skillId, e);
            }
            return result;
        });
    }

    @Override
    public InstalledSkillInfo getSourceInfo(String skillId) {
        return sourceCache.get(skillId);
    }

    private void saveToFile() {
        try {
            Path filePath = Paths.get(dataDir, SOURCE_FILE);
            List<InstalledSkillInfo> list = new ArrayList<>(sourceCache.values());
            String json = JSON.toJSONString(list, JSONWriter.Feature.PrettyFormat);
            Files.write(filePath, json.getBytes("UTF-8"));
            logger.debug("Saved {} skill sources to {}", list.size(), filePath);
        } catch (Exception e) {
            logger.error("Failed to save skill sources to file", e);
        }
    }

    private void loadFromFile() {
        try {
            Path filePath = Paths.get(dataDir, SOURCE_FILE);
            if (!Files.exists(filePath)) {
                logger.info("No existing skill source file found at {}", filePath);
                return;
            }
            String json = new String(Files.readAllBytes(filePath), "UTF-8");
            List<InstalledSkillInfo> list = JSON.parseArray(json, InstalledSkillInfo.class);
            if (list != null) {
                for (InstalledSkillInfo info : list) {
                    if (info.getSkillId() != null) {
                        sourceCache.put(info.getSkillId(), info);
                    }
                }
                logger.info("Loaded {} skill sources from {}", list.size(), filePath);
            }
        } catch (Exception e) {
            logger.error("Failed to load skill sources from file", e);
        }
    }
}
