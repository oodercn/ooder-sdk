package net.ooder.scene.provider;

import net.ooder.scene.core.PageResult;
import java.util.List;
import java.util.Map;

/**
 * 托管扩展Provider接口
 *
 * <p>定义托管服务扩展相关的操作接口，包括自动伸缩、服务发现、存储卷管理等功能</p>
 */
public interface HostingExtensionProvider extends BaseProvider {

    /**
     * 检查实例兼容性
     */
    boolean checkCompatibility(String instanceId, Map<String, Object> requirements);

    /**
     * 获取自动伸缩策略
     */
    AutoScalePolicy getAutoScalePolicy(String instanceId);

    /**
     * 创建自动伸缩策略
     */
    AutoScalePolicy createAutoScalePolicy(AutoScalePolicy policy);

    /**
     * 更新自动伸缩策略
     */
    boolean updateAutoScalePolicy(String policyId, AutoScalePolicy policy);

    /**
     * 删除自动伸缩策略
     */
    boolean deleteAutoScalePolicy(String policyId);

    /**
     * 启用自动伸缩策略
     */
    boolean enableAutoScalePolicy(String policyId);

    /**
     * 禁用自动伸缩策略
     */
    boolean disableAutoScalePolicy(String policyId);

    /**
     * 注册服务
     */
    ServiceEndpoint registerService(ServiceEndpoint service);

    /**
     * 注销服务
     */
    boolean unregisterService(String serviceId);

    /**
     * 获取服务
     */
    ServiceEndpoint getService(String serviceId);

    /**
     * 获取实例的服务列表
     */
    List<ServiceEndpoint> getServicesByInstance(String instanceId);

    /**
     * 发现服务
     */
    List<ServiceEndpoint> discoverService(String serviceName);

    /**
     * 创建存储卷
     */
    Volume createVolume(Volume volume);

    /**
     * 获取存储卷
     */
    Volume getVolume(String volumeId);

    /**
     * 删除存储卷
     */
    boolean deleteVolume(String volumeId);

    /**
     * 挂载存储卷
     */
    boolean mountVolume(String volumeId, String instanceId, String mountPath);

    /**
     * 卸载存储卷
     */
    boolean unmountVolume(String volumeId, String instanceId);

    /**
     * 列出存储卷
     */
    PageResult<Volume> listVolumes(int page, int size);

    /**
     * 获取实例的存储卷列表
     */
    List<Volume> getVolumesByInstance(String instanceId);
}
