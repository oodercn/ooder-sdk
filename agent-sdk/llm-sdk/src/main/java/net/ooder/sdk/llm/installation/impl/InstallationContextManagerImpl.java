package net.ooder.sdk.llm.installation.impl;

import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.llm.installation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 安装上下文管理器实现
 */
@Slf4j
public class InstallationContextManagerImpl implements InstallationContextManager {

    private final Map<String, InstallationContext> contextRegistry = new ConcurrentHashMap<>();

    @Override
    public InstallationContext createInstallationContext(String installId, String sceneId) {
        return createInstallationContext(installId, sceneId, null);
    }

    @Override
    public InstallationContext createInstallationContext(String installId, String sceneId, String userId) {
        if (installId == null || sceneId == null) {
            throw new IllegalArgumentException("InstallId and sceneId cannot be null");
        }

        InstallationContext context = InstallationContext.builder()
                .installId(installId)
                .sceneId(sceneId)
                .userId(userId)
                .status(InstallationStatus.IN_PROGRESS)
                .build();

        contextRegistry.put(installId, context);
        log.info("Installation context created: {} for scene: {}", installId, sceneId);

        return context;
    }

    @Override
    public InstallationContext getInstallationContext(String installId) {
        return contextRegistry.get(installId);
    }

    @Override
    public void updateInstallationContext(InstallationContext context) {
        if (context == null || context.getInstallId() == null) {
            throw new IllegalArgumentException("Context and installId cannot be null");
        }

        context.setUpdatedAt(System.currentTimeMillis());
        contextRegistry.put(context.getInstallId(), context);
    }

    @Override
    public void saveCheckpoint(String installId, String stepId, Map<String, Object> state) {
        saveCheckpoint(installId, stepId, null, state);
    }

    @Override
    public void saveCheckpoint(String installId, String stepId, String checkpointName, Map<String, Object> state) {
        InstallationContext context = getInstallationContext(installId);
        if (context == null) {
            throw new IllegalArgumentException("Installation context not found: " + installId);
        }

        String name = checkpointName != null ? checkpointName : "Checkpoint for " + stepId;
        Checkpoint checkpoint = Checkpoint.create(stepId, name, new HashMap<>(state));
        context.addCheckpoint(checkpoint);
        context.setStatus(InstallationStatus.CHECKPOINT_SAVED);

        updateInstallationContext(context);
        log.info("Checkpoint saved for installation: {}, step: {}", installId, stepId);
    }

    @Override
    public Map<String, Object> restoreCheckpoint(String installId, String stepId) {
        InstallationContext context = getInstallationContext(installId);
        if (context == null) {
            throw new IllegalArgumentException("Installation context not found: " + installId);
        }

        Checkpoint checkpoint = context.getCheckpoint(stepId);
        if (checkpoint == null) {
            log.warn("Checkpoint not found for installation: {}, step: {}", installId, stepId);
            return null;
        }

        context.setStatus(InstallationStatus.RECOVERING);
        updateInstallationContext(context);

        log.info("Checkpoint restored for installation: {}, step: {}", installId, stepId);
        return checkpoint.getState();
    }

    @Override
    public Map<String, Object> restoreCheckpointById(String installId, String checkpointId) {
        InstallationContext context = getInstallationContext(installId);
        if (context == null) {
            throw new IllegalArgumentException("Installation context not found: " + installId);
        }

        for (Checkpoint checkpoint : context.getCheckpoints()) {
            if (checkpoint.getCheckpointId().equals(checkpointId)) {
                context.setStatus(InstallationStatus.RECOVERING);
                updateInstallationContext(context);

                log.info("Checkpoint restored for installation: {}, checkpointId: {}", installId, checkpointId);
                return checkpoint.getState();
            }
        }

        log.warn("Checkpoint not found for installation: {}, checkpointId: {}", installId, checkpointId);
        return null;
    }

    @Override
    public List<Checkpoint> getCheckpoints(String installId) {
        InstallationContext context = getInstallationContext(installId);
        if (context == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(context.getCheckpoints());
    }

    @Override
    public void completeInstallation(String installId) {
        completeInstallation(installId, null);
    }

    @Override
    public void completeInstallation(String installId, Object result) {
        InstallationContext context = getInstallationContext(installId);
        if (context == null) {
            throw new IllegalArgumentException("Installation context not found: " + installId);
        }

        context.markCompleted();
        if (result != null) {
            context.setVariable("result", result);
        }

        updateInstallationContext(context);
        log.info("Installation completed: {}", installId);
    }

    @Override
    public void cancelInstallation(String installId) {
        cancelInstallation(installId, null);
    }

    @Override
    public void cancelInstallation(String installId, String reason) {
        InstallationContext context = getInstallationContext(installId);
        if (context == null) {
            throw new IllegalArgumentException("Installation context not found: " + installId);
        }

        context.markCancelled();
        if (reason != null) {
            context.setVariable("cancelReason", reason);
        }

        updateInstallationContext(context);
        log.info("Installation cancelled: {}, reason: {}", installId, reason);
    }

    @Override
    public void failInstallation(String installId, String errorMessage) {
        InstallationContext context = getInstallationContext(installId);
        if (context == null) {
            throw new IllegalArgumentException("Installation context not found: " + installId);
        }

        context.markFailed();
        context.setVariable("errorMessage", errorMessage);

        updateInstallationContext(context);
        log.info("Installation failed: {}, error: {}", installId, errorMessage);
    }

    @Override
    public void addInstallationStep(String installId, InstallationStep step) {
        InstallationContext context = getInstallationContext(installId);
        if (context == null) {
            throw new IllegalArgumentException("Installation context not found: " + installId);
        }

        context.addStep(step);
        updateInstallationContext(context);
        log.debug("Installation step added: {} to {}", step.getStepId(), installId);
    }

    @Override
    public void updateInstallationStep(String installId, InstallationStep step) {
        InstallationContext context = getInstallationContext(installId);
        if (context == null) {
            throw new IllegalArgumentException("Installation context not found: " + installId);
        }

        InstallationStep existingStep = context.getStep(step.getStepId());
        if (existingStep != null) {
            int index = context.getSteps().indexOf(existingStep);
            context.getSteps().set(index, step);
            updateInstallationContext(context);
            log.debug("Installation step updated: {} in {}", step.getStepId(), installId);
        }
    }

    @Override
    public int getInstallationProgress(String installId) {
        InstallationContext context = getInstallationContext(installId);
        if (context == null) {
            return 0;
        }
        return context.getProgressPercentage();
    }

    @Override
    public List<InstallationContext> listAllContexts() {
        return new ArrayList<>(contextRegistry.values());
    }

    @Override
    public List<InstallationContext> listContextsByStatus(InstallationStatus status) {
        return contextRegistry.values().stream()
                .filter(ctx -> ctx.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteInstallationContext(String installId) {
        contextRegistry.remove(installId);
        log.info("Installation context deleted: {}", installId);
    }

    @Override
    public boolean hasInstallation(String installId) {
        return contextRegistry.containsKey(installId);
    }
}
