package net.ooder.scene.engine;

public enum EngineStatus {
    CREATED("Created"),
    INITIALIZING("Initializing"),
    INITIALIZED("Initialized"),
    STARTING("Starting"),
    RUNNING("Running"),
    STOPPING("Stopping"),
    STOPPED("Stopped"),
    ERROR("Error"),
    DESTROYED("Destroyed");

    private final String description;

    EngineStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
