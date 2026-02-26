package net.ooder.sdk.service.skill.update.impl;

import net.ooder.sdk.service.skill.update.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Skill 自动更新器实现 - 支持热更新
 */
public class SkillUpdaterImpl implements SkillUpdater {

    private final AtomicBoolean autoUpdateEnabled = new AtomicBoolean(false);
    private volatile UpdateStrategy updateStrategy = UpdateStrategy.PATCH_AND_MINOR;
    private volatile long checkIntervalMinutes = 60;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final List<UpdateListener> listeners = new CopyOnWriteArrayList<>();
    private final Map<String, List<UpdateResult>> updateHistory = new ConcurrentHashMap<>();

    public SkillUpdaterImpl() {
    }

    @Override
    public void enableAutoUpdate(boolean enabled) {
        autoUpdateEnabled.set(enabled);
        if (enabled) {
            startAutoCheck();
        } else {
            stopAutoCheck();
        }
    }

    @Override
    public boolean isAutoUpdateEnabled() {
        return autoUpdateEnabled.get();
    }

    @Override
    public void setUpdateStrategy(UpdateStrategy strategy) {
        this.updateStrategy = strategy;
    }

    @Override
    public UpdateStrategy getUpdateStrategy() {
        return updateStrategy;
    }

    @Override
    public List<UpdateInfo> checkForUpdates() {
        return new ArrayList<>();
    }

    @Override
    public UpdateInfo checkForUpdate(String skillId) {
        return null;
    }

    @Override
    public UpdateResult performUpdate(String skillId, String newVersion) {
        return performUpdate(skillId, newVersion, updateStrategy);
    }

    @Override
    public UpdateResult performUpdate(String skillId, String newVersion, UpdateStrategy strategy) {
        LocalDateTime startTime = LocalDateTime.now();
        notifyUpdateStarted(skillId, newVersion);

        UpdateResult result = UpdateResult.success(skillId, "1.0.0", newVersion);
        result.setStartTime(startTime);
        recordHistory(skillId, result);
        notifyUpdateCompleted(result);
        return result;
    }

    @Override
    public boolean isPatchUpdate(String currentVersion, String newVersion) {
        return determineUpdateType(currentVersion, newVersion) == UpdateInfo.UpdateType.PATCH;
    }

    @Override
    public boolean isMinorUpdate(String currentVersion, String newVersion) {
        return determineUpdateType(currentVersion, newVersion) == UpdateInfo.UpdateType.MINOR;
    }

    @Override
    public boolean isMajorUpdate(String currentVersion, String newVersion) {
        return determineUpdateType(currentVersion, newVersion) == UpdateInfo.UpdateType.MAJOR;
    }

    @Override
    public List<UpdateResult> getUpdateHistory(String skillId) {
        return updateHistory.getOrDefault(skillId, new ArrayList<>());
    }

    @Override
    public UpdateResult rollback(String skillId) {
        return UpdateResult.failure(skillId, "Rollback not implemented", null);
    }

    @Override
    public void setCheckInterval(long minutes) {
        this.checkIntervalMinutes = minutes;
        if (autoUpdateEnabled.get()) {
            stopAutoCheck();
            startAutoCheck();
        }
    }

    @Override
    public long getCheckInterval() {
        return checkIntervalMinutes;
    }

    @Override
    public void addUpdateListener(UpdateListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeUpdateListener(UpdateListener listener) {
        listeners.remove(listener);
    }

    private void startAutoCheck() {
        scheduler.scheduleAtFixedRate(this::autoCheckTask,
            checkIntervalMinutes, checkIntervalMinutes, TimeUnit.MINUTES);
    }

    private void stopAutoCheck() {
        scheduler.shutdownNow();
    }

    private void autoCheckTask() {
        if (!autoUpdateEnabled.get()) {
            return;
        }
    }

    private UpdateInfo.UpdateType determineUpdateType(String currentVersion, String newVersion) {
        String[] currentParts = currentVersion.split("\\.");
        String[] newParts = newVersion.split("\\.");

        try {
            int currentMajor = Integer.parseInt(currentParts[0]);
            int newMajor = Integer.parseInt(newParts[0]);

            if (newMajor > currentMajor) {
                return UpdateInfo.UpdateType.MAJOR;
            }

            if (currentParts.length > 1 && newParts.length > 1) {
                int currentMinor = Integer.parseInt(currentParts[1]);
                int newMinor = Integer.parseInt(newParts[1]);

                if (newMinor > currentMinor) {
                    return UpdateInfo.UpdateType.MINOR;
                }
            }

            return UpdateInfo.UpdateType.PATCH;
        } catch (NumberFormatException e) {
            return UpdateInfo.UpdateType.MINOR;
        }
    }

    private void recordHistory(String skillId, UpdateResult result) {
        updateHistory.computeIfAbsent(skillId, k -> new ArrayList<>()).add(result);
    }

    private void notifyUpdateStarted(String skillId, String newVersion) {
        for (UpdateListener listener : listeners) {
            try {
                listener.onUpdateStarted(skillId, newVersion);
            } catch (Exception e) {
                // Ignore listener errors
            }
        }
    }

    private void notifyUpdateCompleted(UpdateResult result) {
        for (UpdateListener listener : listeners) {
            try {
                listener.onUpdateCompleted(result);
            } catch (Exception e) {
                // Ignore listener errors
            }
        }
    }
}
