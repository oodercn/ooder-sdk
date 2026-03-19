package net.ooder.scene.bridge;

import net.ooder.scene.group.SceneGroup;
import net.ooder.scene.group.SceneGroupManager;
import net.ooder.scene.participant.Participant;
import net.ooder.sdk.api.scene.SceneGroupKey;
import net.ooder.sdk.api.scene.SceneMember;
import net.ooder.sdk.api.scene.SceneGroupManager.SceneGroupConfig;
import net.ooder.sdk.api.scene.SceneGroupManager.KeyShare;
import net.ooder.sdk.api.scene.SceneGroupManager.FailoverStatus;
import net.ooder.sdk.api.scene.SceneGroupManager.VfsPermission;
import net.ooder.sdk.common.enums.MemberRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * SDK SceneGroupManager 接口适配器
 *
 * <p>将 SE 原生的 SceneGroupManager 适配为 SDK 的 SceneGroupManager 接口。</p>
 *
 * <p>架构层级：桥接层 - SDK适配</p>
 *
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
@Component
public class SdkSceneGroupManagerAdapter implements net.ooder.sdk.api.scene.SceneGroupManager {

    private static final Logger logger = LoggerFactory.getLogger(SdkSceneGroupManagerAdapter.class);

    private final SceneGroupManager seManager;

    public SdkSceneGroupManagerAdapter(SceneGroupManager seManager) {
        this.seManager = seManager;
        logger.info("SdkSceneGroupManagerAdapter initialized");
    }

    @Override
    public CompletableFuture<net.ooder.sdk.api.scene.SceneGroup> create(
            String sceneId, SceneGroupConfig config) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                SceneGroup seGroup = seManager.createSceneGroup(
                    sceneId,
                    config.getSceneId(),
                    "system",
                    SceneGroup.CreatorType.SYSTEM
                );

                seGroup.setName("Scene Group: " + sceneId);
                seGroup.setDescription("Created via SDK adapter");

                return convertToSdkSceneGroup(seGroup);
            } catch (Exception e) {
                logger.error("Failed to create scene group: {}", sceneId, e);
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> destroy(String sceneGroupId) {
        return CompletableFuture.runAsync(() -> {
            boolean success = seManager.destroySceneGroup(sceneGroupId);
            if (!success) {
                throw new RuntimeException("Failed to destroy scene group: " + sceneGroupId);
            }
        });
    }

    @Override
    public CompletableFuture<net.ooder.sdk.api.scene.SceneGroup> get(String sceneGroupId) {
        return CompletableFuture.supplyAsync(() -> {
            SceneGroup seGroup = seManager.getSceneGroup(sceneGroupId);
            return seGroup != null ? convertToSdkSceneGroup(seGroup) : null;
        });
    }

    @Override
    public CompletableFuture<List<net.ooder.sdk.api.scene.SceneGroup>> listAll() {
        return CompletableFuture.supplyAsync(() -> {
            return seManager.getAllSceneGroups().stream()
                    .map(this::convertToSdkSceneGroup)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public CompletableFuture<List<net.ooder.sdk.api.scene.SceneGroup>> listByScene(String sceneId) {
        return CompletableFuture.supplyAsync(() -> {
            return seManager.getSceneGroupsByTemplate(sceneId).stream()
                    .map(this::convertToSdkSceneGroup)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public CompletableFuture<Void> join(String sceneGroupId, String agentId, MemberRole role) {
        return CompletableFuture.runAsync(() -> {
            SceneGroup seGroup = seManager.getSceneGroup(sceneGroupId);
            if (seGroup == null) {
                throw new RuntimeException("SceneGroup not found: " + sceneGroupId);
            }

            Participant participant = new Participant(
                agentId,
                sceneGroupId,
                agentId,
                Participant.Type.AGENT
            );
            participant.setRole(convertRole(role));

            seManager.addParticipant(sceneGroupId, participant);
            participant.join();
            participant.activate();
        });
    }

    @Override
    public CompletableFuture<Void> leave(String sceneGroupId, String agentId) {
        return CompletableFuture.runAsync(() -> {
            seManager.removeParticipant(sceneGroupId, agentId);
        });
    }

    @Override
    public CompletableFuture<Void> changeRole(String sceneGroupId, String agentId, MemberRole newRole) {
        return CompletableFuture.runAsync(() -> {
            Participant participant = seManager.getParticipant(sceneGroupId, agentId);
            if (participant != null) {
                participant.setRole(convertRole(newRole));
            }
        });
    }

    @Override
    public CompletableFuture<MemberRole> getRole(String sceneGroupId, String agentId) {
        return CompletableFuture.supplyAsync(() -> {
            Participant participant = seManager.getParticipant(sceneGroupId, agentId);
            if (participant == null) {
                return null;
            }
            return convertToSdkRole(participant.getRole());
        });
    }

    @Override
    public CompletableFuture<List<SceneMember>> listMembers(String sceneGroupId) {
        return CompletableFuture.supplyAsync(() -> {
            SceneGroup seGroup = seManager.getSceneGroup(sceneGroupId);
            if (seGroup == null) {
                return Collections.emptyList();
            }
            return seGroup.getAllParticipants().stream()
                    .map(this::convertToSdkMember)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public CompletableFuture<SceneMember> getPrimary(String sceneGroupId) {
        return CompletableFuture.supplyAsync(() -> {
            SceneGroup seGroup = seManager.getSceneGroup(sceneGroupId);
            if (seGroup == null) {
                return null;
            }
            return seGroup.getAllParticipants().stream()
                    .filter(p -> p.getRole() == Participant.Role.OWNER)
                    .findFirst()
                    .map(this::convertToSdkMember)
                    .orElse(null);
        });
    }

    @Override
    public CompletableFuture<List<SceneMember>> getBackups(String sceneGroupId) {
        return CompletableFuture.supplyAsync(() -> {
            SceneGroup seGroup = seManager.getSceneGroup(sceneGroupId);
            if (seGroup == null) {
                return Collections.emptyList();
            }
            return seGroup.getAllParticipants().stream()
                    .filter(p -> p.getRole() == Participant.Role.MANAGER)
                    .map(this::convertToSdkMember)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public CompletableFuture<Void> handleFailover(String sceneGroupId, String failedMemberId) {
        logger.warn("Failover requested for {} in {}, but SE SceneGroup does not support HA failover",
            failedMemberId, sceneGroupId);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<FailoverStatus> getFailoverStatus(String sceneGroupId) {
        return CompletableFuture.supplyAsync(() -> {
            FailoverStatus status = new FailoverStatus();
            status.setSceneGroupId(sceneGroupId);
            status.setInProgress(false);
            return status;
        });
    }

    @Override
    public CompletableFuture<Void> startHeartbeat(String sceneGroupId) {
        return CompletableFuture.runAsync(() -> {
            SceneGroup seGroup = seManager.getSceneGroup(sceneGroupId);
            if (seGroup != null) {
                for (Participant p : seGroup.getAllParticipants()) {
                    p.heartbeat();
                }
            }
        });
    }

    @Override
    public CompletableFuture<Void> stopHeartbeat(String sceneGroupId) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<SceneGroupKey> generateKey(String sceneGroupId) {
        logger.warn("Key generation requested for {}, but SE SceneGroup does not support distributed keys",
            sceneGroupId);
        return CompletableFuture.supplyAsync(SceneGroupKey::new);
    }

    @Override
    public CompletableFuture<SceneGroupKey> reconstructKey(String sceneGroupId, List<KeyShare> shares) {
        return CompletableFuture.supplyAsync(SceneGroupKey::new);
    }

    @Override
    public CompletableFuture<Void> distributeKeyShares(String sceneGroupId, SceneGroupKey key) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<VfsPermission> getVfsPermission(String sceneGroupId, String agentId) {
        return CompletableFuture.supplyAsync(() -> {
            VfsPermission permission = new VfsPermission();
            permission.setAgentId(agentId);
            permission.setSceneGroupId(sceneGroupId);
            permission.setFullAccess(true);
            return permission;
        });
    }

    private net.ooder.sdk.api.scene.SceneGroup convertToSdkSceneGroup(SceneGroup seGroup) {
        net.ooder.sdk.api.scene.SceneGroup sdkGroup = new net.ooder.sdk.api.scene.SceneGroup();
        sdkGroup.setSceneGroupId(seGroup.getSceneGroupId());
        sdkGroup.setSceneId(seGroup.getTemplateId());
        sdkGroup.setStatus(seGroup.getStatus().name());
        sdkGroup.setCreateTime(seGroup.getCreateTime() != null ? 
            seGroup.getCreateTime().toEpochMilli() : System.currentTimeMillis());
        return sdkGroup;
    }

    private SceneMember convertToSdkMember(Participant participant) {
        SceneMember member = new SceneMember();
        member.setAgentId(participant.getParticipantId());
        member.setRole(convertToSdkRole(participant.getRole()));
        member.setStatus(participant.getStatus().name());
        return member;
    }

    private MemberRole convertToSdkRole(Participant.Role role) {
        if (role == null) {
            return MemberRole.MEMBER;
        }
        switch (role) {
            case OWNER:
                return MemberRole.PRIMARY;
            case MANAGER:
                return MemberRole.BACKUP;
            default:
                return MemberRole.MEMBER;
        }
    }

    private Participant.Role convertRole(MemberRole role) {
        if (role == null) {
            return Participant.Role.EMPLOYEE;
        }
        switch (role) {
            case PRIMARY:
                return Participant.Role.OWNER;
            case BACKUP:
                return Participant.Role.MANAGER;
            default:
                return Participant.Role.EMPLOYEE;
        }
    }
}
