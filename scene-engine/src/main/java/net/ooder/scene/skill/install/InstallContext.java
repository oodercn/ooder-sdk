package net.ooder.scene.skill.install;

import java.util.Map;

/**
 * 安装上下文
 *
 * @author ooder
 * @since 2.3.2
 */
public class InstallContext {

    private String installId;
    private String operatorId;
    private String targetPath;
    private Map<String, Object> options;
    private long startTime;

    public InstallContext() {
        this.startTime = System.currentTimeMillis();
    }

    public String getInstallId() {
        return installId;
    }

    public void setInstallId(String installId) {
        this.installId = installId;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
