package net.ooder.scene.core.activation.repository;

import net.ooder.scene.core.activation.model.ActivationProcess;

import java.util.List;
import java.util.Optional;

/**
 * 激活流程存储库接口
 * 
 * <p>提供激活流程的持久化操作</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface ActivationProcessRepository {

    /**
     * 保存激活流程
     *
     * @param process 激活流程
     * @return 保存后的流程
     */
    ActivationProcess save(ActivationProcess process);

    /**
     * 根据流程ID查找
     *
     * @param processId 流程ID
     * @return 激活流程
     */
    Optional<ActivationProcess> findById(String processId);

    /**
     * 根据激活ID查找
     *
     * @param activationId 激活ID
     * @return 激活流程
     */
    Optional<ActivationProcess> findByActivationId(String activationId);

    /**
     * 根据模板ID查找所有流程
     *
     * @param templateId 模板ID
     * @return 流程列表
     */
    List<ActivationProcess> findByTemplateId(String templateId);

    /**
     * 根据用户ID查找流程
     *
     * @param userId 用户ID
     * @return 流程列表
     */
    List<ActivationProcess> findByUserId(String userId);

    /**
     * 根据状态查找流程
     *
     * @param status 状态
     * @return 流程列表
     */
    List<ActivationProcess> findByStatus(ActivationProcess.ProcessStatus status);

    /**
     * 根据用户ID和状态查找流程
     *
     * @param userId 用户ID
     * @param status 状态
     * @return 流程列表
     */
    List<ActivationProcess> findByUserIdAndStatus(String userId, ActivationProcess.ProcessStatus status);

    /**
     * 查找所有流程
     *
     * @return 流程列表
     */
    List<ActivationProcess> findAll();

    /**
     * 删除流程
     *
     * @param processId 流程ID
     * @return 是否成功
     */
    boolean deleteById(String processId);

    /**
     * 更新流程状态
     *
     * @param processId 流程ID
     * @param status 新状态
     * @return 是否成功
     */
    boolean updateStatus(String processId, ActivationProcess.ProcessStatus status);

    /**
     * 添加步骤执行记录
     *
     * @param processId 流程ID
     * @param stepExecution 步骤执行记录
     * @return 是否成功
     */
    boolean addStepExecution(String processId, ActivationProcess.StepExecution stepExecution);

    /**
     * 统计流程数量
     *
     * @return 流程数量
     */
    long count();

    /**
     * 统计指定状态的流程数量
     *
     * @param status 状态
     * @return 流程数量
     */
    long countByStatus(ActivationProcess.ProcessStatus status);

    /**
     * 检查流程是否存在
     *
     * @param processId 流程ID
     * @return 是否存在
     */
    boolean existsById(String processId);

    /**
     * 清理过期的流程
     *
     * @param beforeTime 时间戳
     * @return 清理数量
     */
    int cleanupExpired(long beforeTime);
}
