package net.ooder.scene.llm.config.hotreload;

import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

public class ConfigChangedEvent {

    private final String skillId;
    private final Path configFile;
    private final WatchEvent.Kind<?> kind;
    private final long timestamp;

    public ConfigChangedEvent(String skillId, Path configFile, WatchEvent.Kind<?> kind, long timestamp) {
        this.skillId = skillId;
        this.configFile = configFile;
        this.kind = kind;
        this.timestamp = timestamp;
    }

    public String getSkillId() { return skillId; }
    public Path getConfigFile() { return configFile; }
    public WatchEvent.Kind<?> getKind() { return kind; }
    public long getTimestamp() { return timestamp; }

    public boolean isModified() {
        return kind == StandardWatchEventKinds.ENTRY_MODIFY;
    }

    public boolean isCreated() {
        return kind == StandardWatchEventKinds.ENTRY_CREATE;
    }

    public boolean isDeleted() {
        return kind == StandardWatchEventKinds.ENTRY_DELETE;
    }
}
