package net.ooder.sdk.deployment.impl;

import net.ooder.sdk.api.skill.SkillRegistry;
import net.ooder.sdk.api.skill.InstalledSkill;
import net.ooder.sdk.core.skill.installer.SkillInstaller;
import net.ooder.sdk.deployment.BlueGreenDeploymentManager;
import net.ooder.sdk.deployment.DeploymentConfig;
import net.ooder.sdk.deployment.DeploymentResult;
import net.ooder.sdk.deployment.Environment;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 蓝绿部署管理器实现
 */
public class BlueGreenDeploymentManagerImpl implements BlueGreenDeploymentManager {

    private final SkillRegistry skillRegistry;
    private final SkillInstaller skillInstaller;

    private final Map<String, DeploymentState> deploymentStates = new ConcurrentHashMap<>();
    private final Map<String, Environment> activeEnvironments = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public BlueGreenDeploymentManagerImpl(SkillRegistry skillRegistry, SkillInstaller skillInstaller) {
        this.skillRegistry = skillRegistry;
        this.skillInstaller = skillInstaller;
    }

    @Override
    public DeploymentResult deploy(String skillId, Path newVersionJar, DeploymentConfig config) {
        LocalDateTime startTime = LocalDateTime.now();
        DeploymentState state = new DeploymentState();
        state.status = DeploymentStatus.DEPLOYING;
        deploymentStates.put(skillId, state);

        try {
            Environment currentEnv = getActiveEnvironment(skillId);
            Environment targetEnv = (currentEnv == Environment.BLUE) ? Environment.GREEN : Environment.BLUE;

            state.targetEnvironment = targetEnv;
            state.previousEnvironment = currentEnv;

            Path deployDir = getEnvironmentDir(skillId, targetEnv);
            Files.createDirectories(deployDir);

            Path targetJar = deployDir.resolve(newVersionJar.getFileName());
            Files.copy(newVersionJar, targetJar, StandardCopyOption.REPLACE_EXISTING);

            state.status = BlueGreenDeploymentManager.DeploymentStatus.HEALTH_CHECKING;

            if (!performHealthCheck(skillId, targetEnv, config)) {
                if (config.isAutoRollback()) {
                    rollback(skillId);
                }
                return DeploymentResult.failure(skillId, "Health check failed", null);
            }

            state.status = DeploymentStatus.READY;

            if (config.isZeroDowntime()) {
                switchTraffic(skillId, targetEnv);
            }

            state.status = DeploymentStatus.ACTIVE;

            DeploymentResult result = DeploymentResult.success(skillId, extractVersion(newVersionJar), targetEnv);
            result.setStartTime(startTime);
            result.setPreviousEnvironment(currentEnv);

            if (!config.isKeepOldVersion() && currentEnv != Environment.NONE) {
                cleanupEnvironment(skillId, currentEnv);
            }

            activeEnvironments.put(skillId, targetEnv);
            return result;

        } catch (Exception e) {
            state.status = BlueGreenDeploymentManager.DeploymentStatus.FAILED;
            if (config.isAutoRollback()) {
                rollback(skillId);
            }
            return DeploymentResult.failure(skillId, "Deployment failed: " + e.getMessage(), e);
        }
    }

    @Override
    public DeploymentResult rollback(String skillId) {
        DeploymentState state = deploymentStates.get(skillId);
        if (state == null || state.previousEnvironment == Environment.NONE) {
            return DeploymentResult.failure(skillId, "No previous deployment to rollback", null);
        }

        try {
            switchTraffic(skillId, state.previousEnvironment);

            if (state.targetEnvironment != Environment.NONE) {
                cleanupEnvironment(skillId, state.targetEnvironment);
            }

            activeEnvironments.put(skillId, state.previousEnvironment);

            DeploymentResult result = new DeploymentResult();
            result.setSuccess(true);
            result.setSkillId(skillId);
            result.setStatus(DeploymentResult.DeploymentStatus.ROLLED_BACK);
            result.setMessage("Rolled back to previous version");
            result.setEndTime(LocalDateTime.now());
            return result;

        } catch (Exception e) {
            return DeploymentResult.failure(skillId, "Rollback failed: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean switchTraffic(String skillId, Environment targetEnvironment) {
        activeEnvironments.put(skillId, targetEnvironment);
        return true;
    }

    @Override
    public Environment getActiveEnvironment(String skillId) {
        return activeEnvironments.getOrDefault(skillId, Environment.NONE);
    }

    @Override
    public DeploymentStatus getDeploymentStatus(String skillId) {
        DeploymentState state = deploymentStates.get(skillId);
        return state != null ? state.status : DeploymentStatus.IDLE;
    }

    @Override
    public boolean healthCheck(String skillId, Environment environment) {
        return performHealthCheck(skillId, environment, DeploymentConfig.defaultConfig());
    }

    @Override
    public boolean cleanup(String skillId) {
        try {
            Environment inactiveEnv = getInactiveEnvironment(skillId);
            if (inactiveEnv != Environment.NONE) {
                cleanupEnvironment(skillId, inactiveEnv);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean performHealthCheck(String skillId, Environment environment, DeploymentConfig config) {
        int retries = config.getHealthCheckRetries();
        long timeoutMs = config.getHealthCheckTimeout().toMillis();

        for (int i = 0; i < retries; i++) {
            try {
                if (checkSkillHealth(skillId)) {
                    return true;
                }
                Thread.sleep(timeoutMs / retries);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    private boolean checkSkillHealth(String skillId) {
        try {
            InstalledSkill skill = skillRegistry.getInstalledSkill(skillId);
            return skill != null && skill.isActive();
        } catch (Exception e) {
            return false;
        }
    }

    private Environment getInactiveEnvironment(String skillId) {
        Environment active = getActiveEnvironment(skillId);
        if (active == Environment.BLUE) return Environment.GREEN;
        if (active == Environment.GREEN) return Environment.BLUE;
        return Environment.NONE;
    }

    private Path getEnvironmentDir(String skillId, Environment environment) {
        return Paths.get("deployments", skillId, environment.name().toLowerCase());
    }

    private void cleanupEnvironment(String skillId, Environment environment) throws IOException {
        Path dir = getEnvironmentDir(skillId, environment);
        if (Files.exists(dir)) {
            Files.walk(dir)
                .sorted((a, b) -> -a.compareTo(b))
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        // Ignore
                    }
                });
        }
    }

    private String extractVersion(Path jarPath) {
        String fileName = jarPath.getFileName().toString();
        int lastDash = fileName.lastIndexOf('-');
        int dotJar = fileName.lastIndexOf('.');
        if (lastDash > 0 && dotJar > lastDash) {
            return fileName.substring(lastDash + 1, dotJar);
        }
        return "unknown";
    }

    private static class DeploymentState {
        volatile BlueGreenDeploymentManager.DeploymentStatus status = BlueGreenDeploymentManager.DeploymentStatus.IDLE;
        Environment targetEnvironment = Environment.NONE;
        Environment previousEnvironment = Environment.NONE;
    }
}
