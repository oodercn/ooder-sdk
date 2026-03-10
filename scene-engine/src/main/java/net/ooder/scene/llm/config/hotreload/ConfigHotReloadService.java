package net.ooder.scene.llm.config.hotreload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 配置热加载服务
 *
 * <p>支持：</p>
 * <ul>
 *   <li>文件变化监听</li>
 *   <li>配置差异计算</li>
 *   <li>增量更新</li>
 *   <li>变更通知</li>
 * </ul>
 *
 * @author ooder
 * @since 2.4
 */
public class ConfigHotReloadService {

    private static final Logger log = LoggerFactory.getLogger(ConfigHotReloadService.class);

    private final WatchService watchService;
    private final Map<Path, ConfigWatcher> watchers = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final List<ConfigChangeListener> listeners = new CopyOnWriteArrayList<>();

    public ConfigHotReloadService() throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
    }

    /**
     * 注册配置目录监听
     */
    public void registerWatch(Path configDir, String skillId) {
        Path watchDir = configDir.resolve(skillId);

        if (!Files.exists(watchDir)) {
            return;
        }

        try {
            WatchKey key = watchDir.register(watchService,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE);

            ConfigWatcher watcher = new ConfigWatcher(skillId, watchDir, key);
            watchers.put(watchDir, watcher);

            log.info("Registered config watch for skill: {}", skillId);
        } catch (IOException e) {
            log.error("Failed to register watch for: {}", watchDir, e);
        }
    }

    /**
     * 启动监听
     */
    public void start() {
        executor.submit(this::watchLoop);
        log.info("Config hot reload service started");
    }

    /**
     * 停止监听
     */
    public void stop() {
        executor.shutdown();
        try {
            watchService.close();
        } catch (IOException e) {
            // ignore
        }
        log.info("Config hot reload service stopped");
    }

    /**
     * 添加配置变更监听器
     */
    public void addListener(ConfigChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * 移除配置变更监听器
     */
    public void removeListener(ConfigChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * 监听循环
     */
    private void watchLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                WatchKey key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    handleWatchEvent(key, event);
                }

                key.reset();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("Error in watch loop", e);
            }
        }
    }

    /**
     * 处理文件变化事件
     */
    private void handleWatchEvent(WatchKey key, WatchEvent<?> event) {
        Path watchDir = (Path) key.watchable();
        Path changedFile = watchDir.resolve((Path) event.context());

        if (!changedFile.toString().endsWith(".yaml") &&
            !changedFile.toString().endsWith(".yml")) {
            return;
        }

        ConfigWatcher watcher = watchers.get(watchDir);
        if (watcher == null) return;

        log.info("Config file changed: {} ({})", changedFile, event.kind());

        watcher.debounce(changedFile, () -> {
            handleConfigChange(watcher.getSkillId(), changedFile, event.kind());
        });
    }

    /**
     * 处理配置变化
     */
    private void handleConfigChange(String skillId, Path configFile, WatchEvent.Kind<?> kind) {
        try {
            if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                notifyConfigDeleted(skillId, configFile);
                return;
            }

            ConfigChangedEvent event = new ConfigChangedEvent(
                skillId, configFile, kind, System.currentTimeMillis()
            );

            for (ConfigChangeListener listener : listeners) {
                try {
                    listener.onConfigChanged(event);
                } catch (Exception e) {
                    log.error("Listener error", e);
                }
            }
        } catch (Exception e) {
            log.error("Failed to handle config change: {}", configFile, e);
        }
    }

    private void notifyConfigDeleted(String skillId, Path configFile) {
        for (ConfigChangeListener listener : listeners) {
            listener.onConfigDeleted(skillId, configFile);
        }
    }

    /**
     * 配置监听器
     */
    private static class ConfigWatcher {
        private final String skillId;
        private final Path watchDir;
        private final WatchKey watchKey;
        private final Map<Path, ScheduledFuture<?>> debounceTasks = new ConcurrentHashMap<>();
        private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        public ConfigWatcher(String skillId, Path watchDir, WatchKey watchKey) {
            this.skillId = skillId;
            this.watchDir = watchDir;
            this.watchKey = watchKey;
        }

        public void debounce(Path file, Runnable action) {
            ScheduledFuture<?> existing = debounceTasks.get(file);
            if (existing != null) {
                existing.cancel(false);
            }

            ScheduledFuture<?> future = scheduler.schedule(() -> {
                debounceTasks.remove(file);
                action.run();
            }, 500, TimeUnit.MILLISECONDS);

            debounceTasks.put(file, future);
        }

        public String getSkillId() { return skillId; }
        public Path getWatchDir() { return watchDir; }
        public WatchKey getWatchKey() { return watchKey; }
    }
}
