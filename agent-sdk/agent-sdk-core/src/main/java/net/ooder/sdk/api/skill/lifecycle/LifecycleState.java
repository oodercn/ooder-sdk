package net.ooder.sdk.api.skill.lifecycle;

public enum LifecycleState {
    DISCOVERED("discovered", "Skill has been discovered"),
    DOWNLOADED("downloaded", "Skill package has been downloaded"),
    VERIFIED("verified", "Skill package has been verified"),
    INSTALLED("installed", "Skill has been installed"),
    REGISTERED("registered", "Skill has been registered"),
    STARTED("started", "Skill has been started"),
    HEALTHY("healthy", "Skill is running healthy"),
    UPDATING("updating", "Skill is being updated"),
    STOPPED("stopped", "Skill has been stopped"),
    UNINSTALLED("uninstalled", "Skill has been uninstalled"),
    DESTROYED("destroyed", "Skill has been destroyed"),
    ERROR("error", "Skill encountered an error");

    private final String code;
    private final String description;

    LifecycleState(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == STARTED || this == HEALTHY || this == UPDATING;
    }

    public boolean isTerminal() {
        return this == UNINSTALLED || this == DESTROYED || this == ERROR;
    }

    public static LifecycleState fromCode(String code) {
        for (LifecycleState state : values()) {
            if (state.code.equalsIgnoreCase(code)) {
                return state;
            }
        }
        return ERROR;
    }
}
