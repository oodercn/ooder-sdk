package net.ooder.scene.service.reminder.impl;

import net.ooder.scene.service.reminder.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 提醒服务实现
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class ReminderServiceImpl implements ReminderService {

    private final Map<String, ReminderTask> taskStore = new ConcurrentHashMap<>();
    private final Map<String, List<ReminderHistory>> historyStore = new ConcurrentHashMap<>();

    @Override
    public ReminderTask createReminder(ReminderConfig config) {
        String reminderId = "rmd-" + UUID.randomUUID().toString().substring(0, 8);
        
        ReminderTask task = new ReminderTask();
        task.setReminderId(reminderId);
        task.setSceneId(config.getSceneId());
        task.setUserId(config.getUserId());
        task.setCronExpression(config.getCronExpression());
        task.setReminderType(config.getReminderType());
        task.setTitle(config.getTitle());
        task.setMessageTemplate(config.getMessageTemplate());
        task.setStatus(ReminderTask.ReminderStatus.ACTIVE);
        task.setCreateTime(System.currentTimeMillis());
        task.setNextTriggerTime(calculateNextTrigger(config.getCronExpression()));
        
        taskStore.put(reminderId, task);
        return task;
    }

    @Override
    public ReminderTask createDefaultReminder(String sceneId, String userId) {
        ReminderConfig config = ReminderConfig.weeklyFriday(sceneId, userId);
        return createReminder(config);
    }

    @Override
    public boolean cancelReminder(String reminderId) {
        ReminderTask task = taskStore.get(reminderId);
        if (task == null) return false;
        
        task.setStatus(ReminderTask.ReminderStatus.CANCELLED);
        return true;
    }

    @Override
    public boolean pauseReminder(String reminderId) {
        ReminderTask task = taskStore.get(reminderId);
        if (task == null) return false;
        
        task.setStatus(ReminderTask.ReminderStatus.PAUSED);
        return true;
    }

    @Override
    public boolean resumeReminder(String reminderId) {
        ReminderTask task = taskStore.get(reminderId);
        if (task == null) return false;
        
        task.setStatus(ReminderTask.ReminderStatus.ACTIVE);
        task.setNextTriggerTime(calculateNextTrigger(task.getCronExpression()));
        return true;
    }

    @Override
    public boolean updateReminder(String reminderId, ReminderConfig config) {
        ReminderTask task = taskStore.get(reminderId);
        if (task == null) return false;
        
        task.setCronExpression(config.getCronExpression());
        task.setReminderType(config.getReminderType());
        task.setTitle(config.getTitle());
        task.setMessageTemplate(config.getMessageTemplate());
        task.setNextTriggerTime(calculateNextTrigger(config.getCronExpression()));
        
        return true;
    }

    @Override
    public List<ReminderTask> getUserReminders(String userId) {
        return taskStore.values().stream()
            .filter(t -> t.getUserId().equals(userId))
            .filter(t -> t.getStatus() == ReminderTask.ReminderStatus.ACTIVE 
                      || t.getStatus() == ReminderTask.ReminderStatus.PAUSED)
            .sorted(Comparator.comparing(ReminderTask::getNextTriggerTime))
            .collect(Collectors.toList());
    }

    @Override
    public List<ReminderTask> getSceneReminders(String sceneId) {
        return taskStore.values().stream()
            .filter(t -> t.getSceneId().equals(sceneId))
            .filter(t -> t.getStatus() == ReminderTask.ReminderStatus.ACTIVE 
                      || t.getStatus() == ReminderTask.ReminderStatus.PAUSED)
            .sorted(Comparator.comparing(ReminderTask::getNextTriggerTime))
            .collect(Collectors.toList());
    }

    @Override
    public ReminderTask getReminder(String reminderId) {
        return taskStore.get(reminderId);
    }

    @Override
    public void triggerReminder(String reminderId) {
        ReminderTask task = taskStore.get(reminderId);
        if (task == null || !task.isActive()) return;
        
        ReminderHistory history = new ReminderHistory();
        history.setHistoryId("h-" + UUID.randomUUID().toString().substring(0, 8));
        history.setReminderId(reminderId);
        history.setSceneId(task.getSceneId());
        history.setUserId(task.getUserId());
        history.setTriggerTime(System.currentTimeMillis());
        history.setMessage(task.getMessageTemplate());
        history.setResult(ReminderHistory.ReminderResult.SUCCESS);
        
        historyStore.computeIfAbsent(reminderId, k -> new ArrayList<>()).add(history);
        
        task.setLastTriggerTime(System.currentTimeMillis());
        task.setTriggerCount(task.getTriggerCount() + 1);
        task.setNextTriggerTime(calculateNextTrigger(task.getCronExpression()));
        
        if (task.isExpired()) {
            task.setStatus(ReminderTask.ReminderStatus.COMPLETED);
        }
    }

    @Override
    public List<ReminderHistory> getReminderHistory(String reminderId, int limit) {
        List<ReminderHistory> history = historyStore.getOrDefault(reminderId, new ArrayList<>());
        
        if (history.size() <= limit) {
            return new ArrayList<>(history);
        }
        
        return history.subList(history.size() - limit, history.size());
    }

    private long calculateNextTrigger(String cronExpression) {
        return System.currentTimeMillis() + 24 * 60 * 60 * 1000L;
    }
}
