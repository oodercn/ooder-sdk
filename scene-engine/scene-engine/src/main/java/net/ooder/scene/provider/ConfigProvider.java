package net.ooder.scene.provider;

import net.ooder.scene.core.PageResult;
import net.ooder.scene.core.Result;
import net.ooder.scene.provider.model.config.AdvancedConfig;
import net.ooder.scene.provider.model.config.BasicConfig;
import net.ooder.scene.provider.model.config.NetworkConfig;
import net.ooder.scene.provider.model.config.SecurityConfig;
import net.ooder.scene.provider.model.config.ServiceConfig;
import net.ooder.scene.provider.model.config.SystemConfig;
import net.ooder.scene.provider.model.config.TerminalConfig;

import java.util.List;
import java.util.Map;

public interface ConfigProvider extends BaseProvider {

    Result<String> getConfig(String key);

    Result<String> getConfig(String key, String defaultValue);

    <T> Result<T> getConfig(String key, Class<T> type);

    <T> Result<T> getConfig(String key, Class<T> type, T defaultValue);

    Result<Boolean> setConfig(String key, String value);

    Result<Boolean> setConfig(String key, Object value);

    Result<Boolean> setConfigs(Map<String, String> configs);

    Result<Boolean> deleteConfig(String key);

    Result<Boolean> hasConfig(String key);

    Result<Map<String, String>> getAllConfigs();

    Result<Map<String, String>> getConfigsByPrefix(String prefix);

    Result<PageResult<ConfigHistory>> getConfigHistory(String key, int page, int size);

    Result<Boolean> rollbackConfig(String key, String version);

    Result<ConfigExportResult> exportConfig(String format);

    Result<Integer> importConfig(String content, String format);

    Result<Boolean> refreshConfig();

    Result<List<ConfigGroup>> getConfigGroups();

    Result<ConfigGroup> getConfigGroup(String groupName);

    Result<BasicConfig> getBasicConfig();

    Result<AdvancedConfig> getAdvancedConfig();

    Result<SecurityConfig> getSecurityConfig();

    Result<TerminalConfig> getTerminalConfig();

    Result<ServiceConfig> getServiceConfig();

    Result<SystemConfig> getSystemConfig();

    Result<NetworkConfig> getNetworkConfig();

    Result<Boolean> saveBasicConfig(BasicConfig config);

    Result<Boolean> saveAdvancedConfig(AdvancedConfig config);

    Result<Boolean> saveSecurityConfig(SecurityConfig config);

    Result<Boolean> saveTerminalConfig(TerminalConfig config);

    Result<Boolean> saveServiceConfig(ServiceConfig config);

    Result<Boolean> saveSystemConfig(SystemConfig config);

    Result<Boolean> saveNetworkConfig(NetworkConfig config);
}
