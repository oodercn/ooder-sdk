package net.ooder.scene.core;

public enum EngineStatus {
    CREATED("Created"),
    INITIALIZING("Initializing"),
    RUNNING("Running"),
    STOPPING("Stopping"),
    STOPPED("Stopped"),
    ERROR("Error");

    private final String description;

    EngineStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
