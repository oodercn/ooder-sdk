package net.ooder.scene.core.provider;

import net.ooder.scene.core.Result;
import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.provider.SkillShareProvider;
import net.ooder.scene.provider.model.share.ReceivedSkill;
import net.ooder.scene.provider.model.share.SharedSkill;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SkillShareProviderImpl implements SkillShareProvider {

    private static final String PROVIDER_NAME = "skill-share-provider";
    private static final String VERSION = "1.0.0";

    private boolean initialized = false;
    private boolean running = false;
    private SceneEngine engine;

    private final Map<String, SharedSkill> sharedSkills = new ConcurrentHashMap<>();
    private final Map<String, ReceivedSkill> receivedSkills = new ConcurrentHashMap<>();

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public void initialize(SceneEngine engine) {
        this.engine = engine;
        this.initialized = true;
    }

    @Override
    public void start() {
        if (!initialized) {
            throw new IllegalStateException("Provider not initialized");
        }
        this.running = true;
    }

    @Override
    public void stop() {
        this.running = false;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public Result<SharedSkill> shareSkill(Map<String, Object> skillData) {
        if (skillData == null) {
            return Result.badRequest("Skill data is required");
        }

        String skillId = (String) skillData.get("skillId");
        if (skillId == null || skillId.isEmpty()) {
            return Result.badRequest("Skill ID is required");
        }

        String name = (String) skillData.get("name");
        if (name == null || name.isEmpty()) {
            return Result.badRequest("Skill name is required");
        }

        SharedSkill sharedSkill = new SharedSkill();
        sharedSkill.setShareId(UUID.randomUUID().toString());
        sharedSkill.setSkillId(skillId);
        sharedSkill.setName(name);

        if (skillData.containsKey("description")) {
            sharedSkill.setDescription((String) skillData.get("description"));
        }
        if (skillData.containsKey("sharedWith")) {
            @SuppressWarnings("unchecked")
            List<String> sharedWith = (List<String>) skillData.get("sharedWith");
            sharedSkill.setSharedWith(sharedWith);
        } else {
            sharedSkill.setSharedWith(new ArrayList<>());
        }
        if (skillData.containsKey("shareType")) {
            sharedSkill.setShareType((String) skillData.get("shareType"));
        } else {
            sharedSkill.setShareType("user");
        }

        sharedSkill.setCreatedAt(System.currentTimeMillis());
        sharedSkill.setStatus("active");

        sharedSkills.put(sharedSkill.getShareId(), sharedSkill);

        if (sharedSkill.getSharedWith() != null) {
            for (String recipient : sharedSkill.getSharedWith()) {
                ReceivedSkill receivedSkill = new ReceivedSkill();
                receivedSkill.setShareId(sharedSkill.getShareId());
                receivedSkill.setSkillId(skillId);
                receivedSkill.setName(name);
                receivedSkill.setDescription(sharedSkill.getDescription());
                receivedSkill.setFrom("current-user");
                receivedSkill.setFromName("Current User");
                receivedSkill.setReceivedAt(System.currentTimeMillis());
                receivedSkill.setStatus("pending");

                receivedSkills.put(sharedSkill.getShareId() + "-" + recipient, receivedSkill);
            }
        }

        return Result.success(sharedSkill);
    }

    @Override
    public Result<List<SharedSkill>> getSharedSkills() {
        List<SharedSkill> skills = new ArrayList<>(sharedSkills.values());
        skills.sort((a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()));
        return Result.success(skills);
    }

    @Override
    public Result<List<ReceivedSkill>> getReceivedSkills() {
        List<ReceivedSkill> skills = new ArrayList<>(receivedSkills.values());
        skills.sort((a, b) -> Long.compare(b.getReceivedAt(), a.getReceivedAt()));
        return Result.success(skills);
    }

    @Override
    public Result<Boolean> cancelShare(String shareId) {
        if (shareId == null || shareId.isEmpty()) {
            return Result.badRequest("Share ID is required");
        }

        SharedSkill removed = sharedSkills.remove(shareId);
        if (removed == null) {
            return Result.notFound("Shared skill not found: " + shareId);
        }

        Iterator<Map.Entry<String, ReceivedSkill>> iterator = receivedSkills.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ReceivedSkill> entry = iterator.next();
            if (entry.getKey().startsWith(shareId + "-")) {
                iterator.remove();
            }
        }

        return Result.success(true);
    }

    @Override
    public Result<Boolean> acceptShare(String shareId) {
        if (shareId == null || shareId.isEmpty()) {
            return Result.badRequest("Share ID is required");
        }

        boolean found = false;
        for (ReceivedSkill skill : receivedSkills.values()) {
            if (shareId.equals(skill.getShareId())) {
                skill.setStatus("accepted");
                found = true;
            }
        }

        if (!found) {
            return Result.notFound("Received skill not found: " + shareId);
        }

        SharedSkill sharedSkill = sharedSkills.get(shareId);
        if (sharedSkill != null) {
            sharedSkill.setStatus("accepted");
        }

        return Result.success(true);
    }

    @Override
    public Result<Boolean> rejectShare(String shareId) {
        if (shareId == null || shareId.isEmpty()) {
            return Result.badRequest("Share ID is required");
        }

        boolean found = false;
        for (ReceivedSkill skill : receivedSkills.values()) {
            if (shareId.equals(skill.getShareId())) {
                skill.setStatus("rejected");
                found = true;
            }
        }

        if (!found) {
            return Result.notFound("Received skill not found: " + shareId);
        }

        SharedSkill sharedSkill = sharedSkills.get(shareId);
        if (sharedSkill != null) {
            sharedSkill.setStatus("rejected");
        }

        return Result.success(true);
    }
}
