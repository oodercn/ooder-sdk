package net.ooder.scene.snapshot;

/**
 * 能力快照
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class CapabilitySnapshot {

    private String capId;
    private String capName;
    private String status;
    private String version;

    public CapabilitySnapshot() {}

    public String getCapId() {
        return capId;
    }

    public void setCapId(String capId) {
        this.capId = capId;
    }

    public String getCapName() {
        return capName;
    }

    public void setCapName(String capName) {
        this.capName = capName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
