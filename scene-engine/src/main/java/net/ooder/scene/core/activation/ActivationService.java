package net.ooder.scene.core.activation;

import net.ooder.scene.core.activation.model.ActivationProcess;
import net.ooder.scene.core.activation.model.ActivationRequest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 激活服务接口
 *
 * <p>提供场景激活的完整生命周期管理，包括：</p>
 * <ul>
 *   <li>启动激活流程</li>
 *   <li>执行和跳过激活步骤</li>
 *   <li>配置私有能力</li>
 *   <li>执行网络动作</li>
 *   <li>确认和取消激活</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public interface ActivationService {
    
    /**
     * 启动激活流程
     *
     * @param request 激活请求
     * @return 激活流程实例
     */
    ActivationProcess start(ActivationRequest request);
    
    /**
     * 获取激活流程
     *
     * @param processId 流程ID
     * @return 激活流程实例
     */
    ActivationProcess getProcess(String processId);
    
    /**
     * 执行指定步骤
     *
     * @param processId 流程ID
     * @param stepId 步骤ID
     * @param data 步骤数据
     * @return 更新后的流程实例
     */
    ActivationProcess executeStep(String processId, String stepId, Map<String, Object> data);
    
    /**
     * 跳过指定步骤
     *
     * @param processId 流程ID
     * @param stepId 步骤ID
     * @return 更新后的流程实例
     */
    ActivationProcess skipStep(String processId, String stepId);
    
    /**
     * 自动执行所有步骤并完成激活
     *
     * @param processId 流程ID
     * @return 更新后的流程实例
     */
    ActivationProcess autoActivate(String processId);
    
    /**
     * 确认激活
     *
     * @param processId 流程ID
     * @return 更新后的流程实例
     */
    ActivationProcess confirm(String processId);
    
    /**
     * 取消激活
     *
     * @param processId 流程ID
     * @return 更新后的流程实例
     */
    ActivationProcess cancel(String processId);
    
    /**
     * 配置私有能力
     *
     * @param processId 流程ID
     * @param capabilityIds 启用的能力ID列表
     * @return 更新后的流程实例
     */
    ActivationProcess configurePrivateCapabilities(String processId, List<String> capabilityIds);
    
    /**
     * 执行网络动作
     *
     * @param processId 流程ID
     * @return 异步结果
     */
    CompletableFuture<ActivationProcess> executeNetworkActions(String processId);
    
    /**
     * 获取激活流程列表
     *
     * @param userId 用户ID
     * @return 激活流程列表
     */
    List<ActivationProcess> getProcessesByUser(String userId);
    
    /**
     * 获取激活流程列表
     *
     * @param sceneGroupId 场景组ID
     * @return 激活流程列表
     */
    List<ActivationProcess> getProcessesBySceneGroup(String sceneGroupId);
}
