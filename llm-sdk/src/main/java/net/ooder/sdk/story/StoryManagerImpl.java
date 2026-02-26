package net.ooder.sdk.story;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 故事管理器实现类
 */
public class StoryManagerImpl implements StoryManager {

    private static final Logger log = LoggerFactory.getLogger(StoryManagerImpl.class);

    private final Map<String, UserStory> stories = new ConcurrentHashMap<>();
    private final Map<String, StoryContext> storyContexts = new ConcurrentHashMap<>();
    private final List<StoryListener> listeners = new CopyOnWriteArrayList<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final AtomicInteger storyCounter = new AtomicInteger(0);

    /**
     * 故事执行上下文
     */
    private static class StoryContext {
        private final String storyId;
        private volatile boolean paused = false;
        private volatile boolean cancelled = false;
        private final List<String> completedSteps = new CopyOnWriteArrayList<>();
        private final List<String> failedSteps = new CopyOnWriteArrayList<>();
        private final long startTime;

        StoryContext(String storyId) {
            this.storyId = storyId;
            this.startTime = System.currentTimeMillis();
        }
    }

    @Override
    public UserStory createStory(String title, String description, String actor, String goal, String benefit) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Story title cannot be empty");
        }

        String storyId = "story-" + storyCounter.incrementAndGet() + "-" + UUID.randomUUID().toString().substring(0, 8);

        UserStory story = UserStory.builder()
                .storyId(storyId)
                .title(title)
                .description(description)
                .actor(actor)
                .goal(goal)
                .benefit(benefit)
                .status(UserStory.StoryStatus.DRAFT)
                .createdAt(System.currentTimeMillis())
                .build();

        stories.put(storyId, story);

        log.info("Story created: {} - {}", storyId, title);
        notifyStoryCreated(story);

        return story;
    }

    @Override
    public Optional<UserStory> getStory(String storyId) {
        return Optional.ofNullable(stories.get(storyId));
    }

    @Override
    public boolean updateStory(String storyId, UserStory story) {
        if (storyId == null || story == null) {
            return false;
        }

        UserStory existing = stories.get(storyId);
        if (existing == null) {
            return false;
        }

        stories.put(storyId, story);
        log.info("Story updated: {}", storyId);
        notifyStoryUpdated(story);

        return true;
    }

    @Override
    public boolean deleteStory(String storyId) {
        UserStory removed = stories.remove(storyId);
        if (removed != null) {
            storyContexts.remove(storyId);
            log.info("Story deleted: {}", storyId);
            notifyStoryDeleted(storyId);
            return true;
        }
        return false;
    }

    @Override
    public List<UserStory> getAllStories() {
        return new ArrayList<>(stories.values());
    }

    @Override
    public List<UserStory> getStoriesByStatus(UserStory.StoryStatus status) {
        return stories.values().stream()
                .filter(s -> s.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public CompletableFuture<StoryExecutionResult> executeStory(String storyId) {
        UserStory story = stories.get(storyId);
        if (story == null) {
            return CompletableFuture.completedFuture(
                    StoryExecutionResult.failure(storyId, "Story not found"));
        }

        StoryContext context = new StoryContext(storyId);
        storyContexts.put(storyId, context);

        story.setStatus(UserStory.StoryStatus.IN_PROGRESS);
        notifyStoryStarted(story);

        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            List<String> completedSteps = new ArrayList<>();
            List<String> failedSteps = new ArrayList<>();

            try {
                List<StoryStep> steps = story.getSteps();
                if (steps == null || steps.isEmpty()) {
                    story.setStatus(UserStory.StoryStatus.COMPLETED);
                    return StoryExecutionResult.success(storyId, "No steps to execute");
                }

                for (StoryStep step : steps) {
                    // 检查是否被取消
                    if (context.cancelled) {
                        story.setStatus(UserStory.StoryStatus.CANCELLED);
                        notifyStoryCancelled(story);
                        return StoryExecutionResult.failure(storyId, "Story was cancelled");
                    }

                    // 检查是否被暂停
                    while (context.paused) {
                        Thread.sleep(100);
                        if (context.cancelled) {
                            story.setStatus(UserStory.StoryStatus.CANCELLED);
                            notifyStoryCancelled(story);
                            return StoryExecutionResult.failure(storyId, "Story was cancelled while paused");
                        }
                    }

                    // 执行步骤
                    boolean stepSuccess = executeStep(story, step);

                    if (stepSuccess) {
                        completedSteps.add(step.getStepId());
                        context.completedSteps.add(step.getStepId());
                        notifyStoryStepCompleted(story, step.getStepId());
                    } else {
                        failedSteps.add(step.getStepId());
                        context.failedSteps.add(step.getStepId());
                        notifyStoryStepFailed(story, step.getStepId(), new RuntimeException("Step execution failed"));
                    }
                }

                // 判断整体执行结果
                boolean allSuccess = failedSteps.isEmpty();
                story.setStatus(allSuccess ? UserStory.StoryStatus.COMPLETED : UserStory.StoryStatus.FAILED);

                StoryExecutionResult result = new StoryExecutionResult();
                result.setStoryId(storyId);
                result.setSuccess(allSuccess);
                result.setMessage(allSuccess ? "Story completed successfully" : "Some steps failed");
                result.setExecutionTime(System.currentTimeMillis() - startTime);
                result.setCompletedSteps(completedSteps);
                result.setFailedSteps(failedSteps);

                if (allSuccess) {
                    notifyStoryCompleted(story, result);
                } else {
                    notifyStoryFailed(story, new RuntimeException("Some steps failed"));
                }

                return result;

            } catch (Exception e) {
                log.error("Story execution failed: {}", storyId, e);
                story.setStatus(UserStory.StoryStatus.FAILED);
                notifyStoryFailed(story, e);
                return StoryExecutionResult.failure(storyId, "Execution error: " + e.getMessage());
            } finally {
                storyContexts.remove(storyId);
            }
        }, executor);
    }

    /**
     * 执行单个步骤
     */
    private boolean executeStep(UserStory story, StoryStep step) {
        try {
            log.debug("Executing step {} for story {}", step.getStepId(), story.getStoryId());

            // 模拟步骤执行
            Thread.sleep(100 + (long) (Math.random() * 200));

            // 模拟成功率
            return Math.random() > 0.1; // 90% 成功率

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public boolean pauseStory(String storyId) {
        StoryContext context = storyContexts.get(storyId);
        if (context != null) {
            context.paused = true;
            UserStory story = stories.get(storyId);
            if (story != null) {
                notifyStoryPaused(story);
            }
            log.info("Story paused: {}", storyId);
            return true;
        }
        return false;
    }

    @Override
    public boolean resumeStory(String storyId) {
        StoryContext context = storyContexts.get(storyId);
        if (context != null && context.paused) {
            context.paused = false;
            UserStory story = stories.get(storyId);
            if (story != null) {
                notifyStoryResumed(story);
            }
            log.info("Story resumed: {}", storyId);
            return true;
        }
        return false;
    }

    @Override
    public boolean cancelStory(String storyId) {
        StoryContext context = storyContexts.get(storyId);
        if (context != null) {
            context.cancelled = true;
            log.info("Story cancelled: {}", storyId);
            return true;
        }

        UserStory story = stories.get(storyId);
        if (story != null && story.getStatus() == UserStory.StoryStatus.DRAFT) {
            story.setStatus(UserStory.StoryStatus.CANCELLED);
            notifyStoryCancelled(story);
            return true;
        }

        return false;
    }

    @Override
    public void addStoryListener(StoryListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeStoryListener(StoryListener listener) {
        listeners.remove(listener);
    }

    @Override
    public int getActiveStoryCount() {
        return (int) stories.values().stream()
                .filter(s -> s.getStatus() == UserStory.StoryStatus.IN_PROGRESS)
                .count();
    }

    @Override
    public StoryStatistics getStatistics() {
        StoryStatistics stats = new StoryStatistics();
        stats.setTotalStories(stories.size());
        stats.setDraftCount(getStoriesByStatus(UserStory.StoryStatus.DRAFT).size());
        stats.setReadyCount(getStoriesByStatus(UserStory.StoryStatus.READY).size());
        stats.setInProgressCount(getStoriesByStatus(UserStory.StoryStatus.IN_PROGRESS).size());
        stats.setCompletedCount(getStoriesByStatus(UserStory.StoryStatus.COMPLETED).size());
        stats.setFailedCount(getStoriesByStatus(UserStory.StoryStatus.FAILED).size());
        stats.setCancelledCount(getStoriesByStatus(UserStory.StoryStatus.CANCELLED).size());
        return stats;
    }

    @Override
    public void clear() {
        stories.clear();
        storyContexts.clear();
        log.info("All stories cleared");
    }

    /**
     * 关闭管理器
     */
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // 通知方法

    private void notifyStoryCreated(UserStory story) {
        for (StoryListener listener : listeners) {
            try {
                listener.onStoryCreated(story);
            } catch (Exception e) {
                log.warn("Listener error onStoryCreated", e);
            }
        }
    }

    private void notifyStoryUpdated(UserStory story) {
        for (StoryListener listener : listeners) {
            try {
                listener.onStoryUpdated(story);
            } catch (Exception e) {
                log.warn("Listener error onStoryUpdated", e);
            }
        }
    }

    private void notifyStoryDeleted(String storyId) {
        for (StoryListener listener : listeners) {
            try {
                listener.onStoryDeleted(storyId);
            } catch (Exception e) {
                log.warn("Listener error onStoryDeleted", e);
            }
        }
    }

    private void notifyStoryStarted(UserStory story) {
        for (StoryListener listener : listeners) {
            try {
                listener.onStoryStarted(story);
            } catch (Exception e) {
                log.warn("Listener error onStoryStarted", e);
            }
        }
    }

    private void notifyStoryStepCompleted(UserStory story, String stepId) {
        for (StoryListener listener : listeners) {
            try {
                listener.onStoryStepCompleted(story, stepId);
            } catch (Exception e) {
                log.warn("Listener error onStoryStepCompleted", e);
            }
        }
    }

    private void notifyStoryStepFailed(UserStory story, String stepId, Throwable error) {
        for (StoryListener listener : listeners) {
            try {
                listener.onStoryStepFailed(story, stepId, error);
            } catch (Exception e) {
                log.warn("Listener error onStoryStepFailed", e);
            }
        }
    }

    private void notifyStoryCompleted(UserStory story, StoryExecutionResult result) {
        for (StoryListener listener : listeners) {
            try {
                listener.onStoryCompleted(story, result);
            } catch (Exception e) {
                log.warn("Listener error onStoryCompleted", e);
            }
        }
    }

    private void notifyStoryFailed(UserStory story, Throwable error) {
        for (StoryListener listener : listeners) {
            try {
                listener.onStoryFailed(story, error);
            } catch (Exception e) {
                log.warn("Listener error onStoryFailed", e);
            }
        }
    }

    private void notifyStoryCancelled(UserStory story) {
        for (StoryListener listener : listeners) {
            try {
                listener.onStoryCancelled(story);
            } catch (Exception e) {
                log.warn("Listener error onStoryCancelled", e);
            }
        }
    }

    private void notifyStoryPaused(UserStory story) {
        for (StoryListener listener : listeners) {
            try {
                listener.onStoryPaused(story);
            } catch (Exception e) {
                log.warn("Listener error onStoryPaused", e);
            }
        }
    }

    private void notifyStoryResumed(UserStory story) {
        for (StoryListener listener : listeners) {
            try {
                listener.onStoryResumed(story);
            } catch (Exception e) {
                log.warn("Listener error onStoryResumed", e);
            }
        }
    }
}
