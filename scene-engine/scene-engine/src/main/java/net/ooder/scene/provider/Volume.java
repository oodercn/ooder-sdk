package net.ooder.scene.provider;

import java.util.Map;

/**
 * 存储卷
 */
public class Volume {
    private String volumeId;
    private String volumeName;
    private String volumeType;
    private long sizeGB;
    private String status;
    private String instanceId;
    private String mountPath;
    private boolean mounted;
    private Map<String, String> labels;
    private Map<String, String> annotations;
    private long createdAt;
    private long updatedAt;

    public String getVolumeId() { return volumeId; }
    public void setVolumeId(String volumeId) { this.volumeId = volumeId; }
    public String getVolumeName() { return volumeName; }
    public void setVolumeName(String volumeName) { this.volumeName = volumeName; }
    public String getVolumeType() { return volumeType; }
    public void setVolumeType(String volumeType) { this.volumeType = volumeType; }
    public long getSizeGB() { return sizeGB; }
    public void setSizeGB(long sizeGB) { this.sizeGB = sizeGB; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getInstanceId() { return instanceId; }
    public void setInstanceId(String instanceId) { this.instanceId = instanceId; }
    public String getMountPath() { return mountPath; }
    public void setMountPath(String mountPath) { this.mountPath = mountPath; }
    public boolean isMounted() { return mounted; }
    public void setMounted(boolean mounted) { this.mounted = mounted; }
    public Map<String, String> getLabels() { return labels; }
    public void setLabels(Map<String, String> labels) { this.labels = labels; }
    public Map<String, String> getAnnotations() { return annotations; }
    public void setAnnotations(Map<String, String> annotations) { this.annotations = annotations; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
