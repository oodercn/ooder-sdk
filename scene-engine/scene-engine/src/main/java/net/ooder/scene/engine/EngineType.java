package net.ooder.scene.engine;

public enum EngineType {
    ORG("Org Engine", "User, department, role management"),
    MSG("Message Engine", "Message push, P2P, Topic subscription"),
    VFS("File Engine", "File upload, download, storage management"),
    AGENT("Agent Engine", "Agent registration, heartbeat, subnet management"),
    SKILL("Skill Engine", "Skill search, install, runtime monitoring"),
    SESSION("Session Engine", "Session, Token, connection management"),
    SECURITY("Security Engine", "Authentication, authorization, encryption management"),
    AUDIT("Audit Engine", "Log audit, operation records"),
    WORKFLOW("Workflow Engine", "Process orchestration, task scheduling"),
    STATE("State Engine", "State management, event driven"),
    CAPABILITY("Capability Engine", "Capability registration, discovery, invocation");

    private final String name;
    private final String description;

    EngineType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
}
