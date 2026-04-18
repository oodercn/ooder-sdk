package net.ooder.sdk.cli.api;

import java.util.*;

public class CommandContext {

    private final Map<String, Object> attributes = new HashMap<>();
    private final Map<String, String> parameters = new HashMap<>();
    private final Map<String, String> options = new HashMap<>();
    private final List<String> positionalArgs = new ArrayList<>();
    private String currentUser;
    private String currentScene;
    private String outputFormat = "text";
    private boolean verbose = false;
    private boolean quiet = false;
    private boolean interactive = false;
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

    public String getParameter(String name) {
        return parameters.get(name);
    }

    public String getParameter(String name, String defaultValue) {
        return parameters.getOrDefault(name, defaultValue);
    }

    public void setParameter(String name, String value) {
        parameters.put(name, value);
    }

    public Map<String, String> getParameters() {
        return new HashMap<>(parameters);
    }

    public String getOption(String name) {
        return options.get(name);
    }

    public String getOption(String name, String defaultValue) {
        return options.getOrDefault(name, defaultValue);
    }

    public void setOption(String name, String value) {
        options.put(name, value);
    }

    public Map<String, String> getOptions() {
        return new HashMap<>(options);
    }

    public List<String> getPositionalArgs() {
        return new ArrayList<>(positionalArgs);
    }

    public void addPositionalArg(String arg) {
        positionalArgs.add(arg);
    }

    public String getPositionalArg(int index) {
        return index < positionalArgs.size() ? positionalArgs.get(index) : null;
    }

    public String getPositionalArg(int index, String defaultValue) {
        return index < positionalArgs.size() ? positionalArgs.get(index) : defaultValue;
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

    public boolean isInteractive() {
        return interactive;
    }

    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args != null ? args : new String[0];
    }

    public String getString(String key, String defaultValue) {
        Object value = attributes.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    public Object get(String key) {
        return attributes.get(key);
    }
}
