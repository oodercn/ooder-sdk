package net.ooder.sdk.cli.api;

import java.util.HashMap;
import java.util.Map;

/**
 * 命令上下文
 *
 * <p>保存命令执行时的上下文信息</p>
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class CommandContext {

    private final Map<String, Object> attributes = new HashMap<>();
    private String currentUser;
    private String currentScene;
    private String outputFormat = "text";
    private boolean verbose = false;
    private boolean quiet = false;
    private String[] args = new String[0];

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }

    public Map<String, Object> getAttributes() {
        return new HashMap<>(attributes);
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public String getCurrentScene() {
        return currentScene;
    }

    public void setCurrentScene(String currentScene) {
        this.currentScene = currentScene;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isQuiet() {
        return quiet;
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    /**
     * 获取命令参数
     */
    public String[] getArgs() {
        return args;
    }

    /**
     * 设置命令参数
     */
    public void setArgs(String[] args) {
        this.args = args != null ? args : new String[0];
    }

    /**
     * 获取属性值，如果不存在返回默认值
     */
    public String getString(String key, String defaultValue) {
        Object value = attributes.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    /**
     * 获取属性值
     */
    public Object get(String key) {
        return attributes.get(key);
    }
}
