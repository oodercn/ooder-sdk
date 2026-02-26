package net.ooder.sdk.infra.config.scene.impl;

import net.ooder.sdk.infra.config.scene.DynamicSceneConfigManager;
import net.ooder.sdk.infra.config.scene.SceneConfiguration;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class DynamicSceneConfigManagerImpl implements DynamicSceneConfigManager {

    private final Map<String, SceneConfiguration> configs = new ConcurrentHashMap<>();
    private final List<ConfigChangeListener> listeners = new CopyOnWriteArrayList<>();
    private WatchService watchService;
    private ExecutorService watchExecutor;
    private volatile boolean watching = false;
    private Path configDir;

    @Override
    public void enableFileWatching(Path configDir) {
        this.configDir = configDir;
        try {
            watchService = FileSystems.getDefault().newWatchService();
            configDir.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE);

            watching = true;
            watchExecutor = Executors.newSingleThreadExecutor();
            watchExecutor.submit(this::watchTask);

            loadAllConfigs();
        } catch (IOException e) {
            throw new RuntimeException("Failed to enable file watching", e);
        }
    }

    @Override
    public void disableFileWatching() {
        watching = false;
        if (watchExecutor != null) {
            watchExecutor.shutdownNow();
        }
        if (watchService != null) {
            try {
                watchService.close();
            } catch (IOException e) {
            }
        }
    }

    @Override
    public void loadSceneConfig(String sceneId) {
        if (configDir == null) {
            return;
        }

        Path configFile = configDir.resolve(sceneId + ".yaml");
        if (!Files.exists(configFile)) {
            configFile = configDir.resolve(sceneId + ".json");
        }

        if (Files.exists(configFile)) {
            SceneConfiguration config = parseConfigFile(configFile);
            if (config != null) {
                configs.put(sceneId, config);
            }
        }
    }

    @Override
    public void reloadAllConfigs() {
        configs.clear();
        loadAllConfigs();
    }

    @Override
    public void addSkillToScene(String sceneId, String skillId) {
        SceneConfiguration config = configs.computeIfAbsent(sceneId, k -> {
            SceneConfiguration c = new SceneConfiguration();
            c.setSceneId(sceneId);
            return c;
        });

        config.addSkill(skillId);
        saveConfig(sceneId, config);
        notifySkillAdded(sceneId, skillId);
    }

    @Override
    public void removeSkillFromScene(String sceneId, String skillId) {
        SceneConfiguration config = configs.get(sceneId);
        if (config != null) {
            config.removeSkill(skillId);
            saveConfig(sceneId, config);
            notifySkillRemoved(sceneId, skillId);
        }
    }

    @Override
    public List<String> getSceneSkills(String sceneId) {
        SceneConfiguration config = configs.get(sceneId);
        return config != null ? config.getSkills() : new ArrayList<>();
    }

    @Override
    public void updateSceneConfig(String sceneId, SceneConfiguration config) {
        config.setSceneId(sceneId);
        configs.put(sceneId, config);
        saveConfig(sceneId, config);
        notifyConfigChanged(sceneId, config);
    }

    @Override
    public SceneConfiguration getSceneConfig(String sceneId) {
        return configs.get(sceneId);
    }

    @Override
    public void addConfigListener(ConfigChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeConfigListener(ConfigChangeListener listener) {
        listeners.remove(listener);
    }

    private void watchTask() {
        while (watching) {
            try {
                WatchKey key = watchService.poll(1, TimeUnit.SECONDS);
                if (key == null) {
                    continue;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    Path fileName = (Path) event.context();
                    String fileNameStr = fileName.toString();

                    if (fileNameStr.endsWith(".yaml") || fileNameStr.endsWith(".json")) {
                        String sceneId = fileNameStr.substring(0, fileNameStr.lastIndexOf('.'));

                        if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                            configs.remove(sceneId);
                        } else {
                            loadSceneConfig(sceneId);
                            SceneConfiguration config = configs.get(sceneId);
                            if (config != null) {
                                notifyConfigChanged(sceneId, config);
                            }
                        }
                    }
                }

                key.reset();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void loadAllConfigs() {
        if (configDir == null || !Files.exists(configDir)) {
            return;
        }

        try {
            Files.list(configDir)
                .filter(p -> {
                    String name = p.getFileName().toString();
                    return name.endsWith(".yaml") || name.endsWith(".json");
                })
                .forEach(p -> {
                    String fileName = p.getFileName().toString();
                    String sceneId = fileName.substring(0, fileName.lastIndexOf('.'));
                    loadSceneConfig(sceneId);
                });
        } catch (IOException e) {
        }
    }

    private SceneConfiguration parseConfigFile(Path configFile) {
        try {
            String content = new String(Files.readAllBytes(configFile));
            String fileName = configFile.getFileName().toString();

            if (fileName.endsWith(".json")) {
                return parseJson(content);
            } else {
                return parseYaml(content);
            }
        } catch (IOException e) {
            return null;
        }
    }

    private SceneConfiguration parseJson(String content) {
        SceneConfiguration config = new SceneConfiguration();
        content = content.trim();
        if (content.startsWith("{")) {
            content = content.substring(1, content.length() - 1);
        }
        String[] pairs = content.split(",");
        for (String pair : pairs) {
            String[] kv = pair.split(":");
            if (kv.length == 2) {
                String key = kv[0].trim().replace("\"", "");
                String value = kv[1].trim().replace("\"", "");
                switch (key) {
                    case "sceneId": config.setSceneId(value); break;
                    case "name": config.setName(value); break;
                    case "description": config.setDescription(value); break;
                    case "enabled": config.setEnabled(Boolean.parseBoolean(value)); break;
                    case "priority": config.setPriority(Integer.parseInt(value)); break;
                }
            }
        }
        return config;
    }

    private SceneConfiguration parseYaml(String content) {
        SceneConfiguration config = new SceneConfiguration();
        String[] lines = content.split("\n");
        for (String line : lines) {
            if (line.trim().startsWith("#") || !line.contains(":")) {
                continue;
            }
            String[] kv = line.split(":", 2);
            if (kv.length == 2) {
                String key = kv[0].trim();
                String value = kv[1].trim();
                switch (key) {
                    case "sceneId": config.setSceneId(value); break;
                    case "name": config.setName(value); break;
                    case "description": config.setDescription(value); break;
                    case "enabled": config.setEnabled(Boolean.parseBoolean(value)); break;
                    case "priority": config.setPriority(Integer.parseInt(value)); break;
                }
            }
        }
        return config;
    }

    private void saveConfig(String sceneId, SceneConfiguration config) {
        if (configDir == null) {
            return;
        }
        try {
            Path configFile = configDir.resolve(sceneId + ".yaml");
            StringBuilder yaml = new StringBuilder();
            yaml.append("sceneId: ").append(config.getSceneId()).append("\n");
            yaml.append("name: ").append(config.getName()).append("\n");
            yaml.append("description: ").append(config.getDescription()).append("\n");
            yaml.append("enabled: ").append(config.isEnabled()).append("\n");
            yaml.append("priority: ").append(config.getPriority()).append("\n");
            yaml.append("skills:\n");
            for (String skill : config.getSkills()) {
                yaml.append("  - ").append(skill).append("\n");
            }
            Files.write(configFile, yaml.toString().getBytes());
        } catch (IOException e) {
        }
    }

    private void notifyConfigChanged(String sceneId, SceneConfiguration config) {
        for (ConfigChangeListener listener : listeners) {
            try {
                listener.onConfigChanged(sceneId, config);
            } catch (Exception e) {
            }
        }
    }

    private void notifySkillAdded(String sceneId, String skillId) {
        for (ConfigChangeListener listener : listeners) {
            try {
                listener.onSkillAdded(sceneId, skillId);
            } catch (Exception e) {
            }
        }
    }

    private void notifySkillRemoved(String sceneId, String skillId) {
        for (ConfigChangeListener listener : listeners) {
            try {
                listener.onSkillRemoved(sceneId, skillId);
            } catch (Exception e) {
            }
        }
    }
}
