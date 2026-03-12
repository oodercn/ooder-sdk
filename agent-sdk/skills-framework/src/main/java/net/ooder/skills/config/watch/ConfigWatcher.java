package net.ooder.skills.config.watch;

import net.ooder.skills.config.ConfigNode;
import net.ooder.skills.config.exception.ConfigException;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * 配置监听器
 *
 * <p>监听配置文件变化，支持热加载</p>
 *
 * @author Agent-SDK Team
 * @version 2.4.0
 * @since 2.4.0
 */
public class ConfigWatcher {

    private final WatchService watchService;
    private final ConcurrentMap<Path, WatchTarget> watchTargets;
    private final Thread watchThread;
    private volatile boolean running = false;

    public ConfigWatcher() throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        this.watchTargets = new ConcurrentHashMap<>();
        this.watchThread = new Thread(this::watchLoop, "ConfigWatcher");
        this.watchThread.setDaemon(true);
    }

    /**
     * 开始监听
     */
    public void start() {
        if (!running) {
            running = true;
            watchThread.start();
        }
    }

    /**
     * 停止监听
     */
    public void stop() {
        running = false;
        watchThread.interrupt();
        try {
            watchService.close();
        } catch (IOException e) {
            // ignore
        }
    }

    /**
     * 监听配置文件
     *
     * @param configFile 配置文件路径
     * @param callback   变化回调
     */
    public void watch(Path configFile, Consumer<ConfigChangeEvent> callback) {
        try {
            Path directory = configFile.getParent();
            Path fileName = configFile.getFileName();

            if (!watchTargets.containsKey(directory)) {
                WatchKey key = directory.register(watchService,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.ENTRY_DELETE);
                watchTargets.put(directory, new WatchTarget(key, directory));
            }

            WatchTarget target = watchTargets.get(directory);
            target.addWatcher(fileName.toString(), callback);

        } catch (IOException e) {
            throw new ConfigException("Failed to watch config file: " + configFile, e);
        }
    }

    /**
     * 取消监听
     *
     * @param configFile 配置文件路径
     */
    public void unwatch(Path configFile) {
        Path directory = configFile.getParent();
        Path fileName = configFile.getFileName();

        WatchTarget target = watchTargets.get(directory);
        if (target != null) {
            target.removeWatcher(fileName.toString());
        }
    }

    private void watchLoop() {
        while (running) {
            try {
                WatchKey key = watchService.take();
                Path directory = (Path) key.watchable();
                WatchTarget target = watchTargets.get(directory);

                if (target != null) {
                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();
                        Path fileName = (Path) event.context();
                        String fileNameStr = fileName.toString();

                        if (target.hasWatcher(fileNameStr)) {
                            ConfigChangeEvent changeEvent = new ConfigChangeEvent(
                                    directory.resolve(fileName),
                                    kind,
                                    target.getConfigType()
                            );
                            target.notifyWatchers(fileNameStr, changeEvent);
                        }
                    }
                }

                key.reset();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (ClosedWatchServiceException e) {
                break;
            }
        }
    }

    /**
     * 配置变化事件
     */
    public static class ConfigChangeEvent {
        private final Path file;
        private final WatchEvent.Kind<?> kind;
        private final String configType;

        public ConfigChangeEvent(Path file, WatchEvent.Kind<?> kind, String configType) {
            this.file = file;
            this.kind = kind;
            this.configType = configType;
        }

        public Path getFile() {
            return file;
        }

        public WatchEvent.Kind<?> getKind() {
            return kind;
        }

        public String getConfigType() {
            return configType;
        }

        public boolean isCreate() {
            return kind == StandardWatchEventKinds.ENTRY_CREATE;
        }

        public boolean isModify() {
            return kind == StandardWatchEventKinds.ENTRY_MODIFY;
        }

        public boolean isDelete() {
            return kind == StandardWatchEventKinds.ENTRY_DELETE;
        }
    }

    /**
     * 监听目标
     */
    private static class WatchTarget {
        private final WatchKey key;
        private final Path directory;
        private final ConcurrentMap<String, Consumer<ConfigChangeEvent>> watchers;
        private String configType = "unknown";

        WatchTarget(WatchKey key, Path directory) {
            this.key = key;
            this.directory = directory;
            this.watchers = new ConcurrentHashMap<>();
        }

        void addWatcher(String fileName, Consumer<ConfigChangeEvent> callback) {
            watchers.put(fileName, callback);
        }

        void removeWatcher(String fileName) {
            watchers.remove(fileName);
        }

        boolean hasWatcher(String fileName) {
            return watchers.containsKey(fileName);
        }

        void notifyWatchers(String fileName, ConfigChangeEvent event) {
            Consumer<ConfigChangeEvent> callback = watchers.get(fileName);
            if (callback != null) {
                try {
                    callback.accept(event);
                } catch (Exception e) {
                    // Log error but don't stop watching
                    System.err.println("Error notifying config watcher: " + e.getMessage());
                }
            }
        }

        String getConfigType() {
            return configType;
        }

        void setConfigType(String configType) {
            this.configType = configType;
        }
    }
}
