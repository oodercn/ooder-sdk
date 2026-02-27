package net.ooder.scene.provider;

import net.ooder.scene.core.PageResult;
import net.ooder.scene.core.Result;

import java.util.List;
import java.util.Map;

/**
 * 系统Provider接口
 *
 * <p>提供系统信息、服务状态、资源使用等功能，由SEC Engine核心实现</p>
 */
public interface SystemProvider extends BaseProvider {

    /**
     * 获取系统信息
     */
    Result<SystemInfo> getSystemInfo();

    /**
     * 获取系统状态
     */
    Result<SystemStatus> getSystemStatus();

    /**
     * 获取系统负载
     */
    Result<SystemLoad> getSystemLoad();

    /**
     * 获取服务列表
     */
    Result<PageResult<ServiceInfo>> listServices(int page, int size);

    /**
     * 获取服务信息
     */
    Result<ServiceInfo> getService(String serviceName);

    /**
     * 启动服务
     */
    Result<Boolean> startService(String serviceName);

    /**
     * 停止服务
     */
    Result<Boolean> stopService(String serviceName);

    /**
     * 重启服务
     */
    Result<Boolean> restartService(String serviceName);

    /**
     * 获取资源使用情况
     */
    Result<List<ResourceUsage>> getResourceUsage();

    /**
     * 获取环境变量
     */
    Result<Map<String, String>> getEnvironmentVariables();

    /**
     * 获取系统属性
     */
    Result<Map<String, String>> getSystemProperties();

    /**
     * 执行系统命令
     */
    Result<SystemCommandResult> executeCommand(String command);
}
