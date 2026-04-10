package net.ooder.skill.common.model;

public class SystemConfig {
    private String syscode;
    private String configName;
    private boolean sdkEnabled;
    private boolean mockEnabled;
    private String version;
    private String environment;

    public String getSyscode() { return syscode; }
    public void setSyscode(String syscode) { this.syscode = syscode; }
    public String getConfigName() { return configName; }
    public void setConfigName(String configName) { this.configName = configName; }
    public boolean isSdkEnabled() { return sdkEnabled; }
    public void setSdkEnabled(boolean sdkEnabled) { this.sdkEnabled = sdkEnabled; }
    public boolean isMockEnabled() { return mockEnabled; }
    public void setMockEnabled(boolean mockEnabled) { this.mockEnabled = mockEnabled; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }
}
