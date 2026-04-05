/**
 * $RCSfile: DataEngine.java,v $
 * $Revision: 1.3 $
 * $Date: 2016/01/23 16:29:52 $
 * <p>
 * Copyright (C) 2008 itjds, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 * <p>
 * 业务数据处理引擎
 */
package net.ooder.bpm.engine;

import net.ooder.bpm.client.ActivityInst;
import net.ooder.bpm.client.ProcessDefForm;
import net.ooder.bpm.client.data.DataMap;
import net.ooder.bpm.enums.right.RightCtx;
import net.ooder.common.Filter;
import net.ooder.common.ReturnType;
import net.ooder.annotation.MethodChinaName;

import java.util.List;
import java.util.Map;


/**
 * @author wenzhangli To change the template for this generated type comment
 * go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public interface DataEngine {

    /**
     * 根据活流程定义取得主MainMapDAO的定义对象
     *
     * @param processDefVersionID
     * @param ctx
     * @return
     * @throws BPMException
     */
    public ProcessDefForm getProcessDefForm(String processDefVersionID, Map<RightCtx, Object> ctx)
            throws BPMException;


    /**
     * 取得流程定义的权限过滤器
     *
     * @param ctx
     * @return
     * @throws BPMException
     */
    public abstract Filter getProcessDefListFilter(Map<RightCtx, Object> ctx) throws BPMException;

    /**
     * 取的流程实例的过滤器
     *
     * @param ctx 接受CTX_CONDITION_WAITEDWORK 和 CTX_CONDITION_ALLWORK参数
     * @return
     * @throws BPMException
     */
    public abstract Filter getProcessInstListFilter(Map<RightCtx, Object> ctx)
            throws BPMException;

    /**
     * 取得活动实例的过滤器
     *
     * @param ctx 接受ctx_condition_waitedwork 和 CTX_CONDITION_ALLWORK参数
     * @return
     * @throws BPMException
     */
    public abstract Filter getActivityInstListFilter(Map<RightCtx, Object> ctx)
            throws BPMException;

    /**
     * 创建流程实例
     *
     * @param processInstId 在工作流引擎中创建的流程实例ID
     * @param ctx           上下文环境，包含权限引擎需要的信息
     * @return 0 - 成功 ，-1 失败
     * @throws BPMException
     */
    public abstract ReturnType createProcessInst(String processInstId, Map<RightCtx, Object> ctx)
            throws BPMException;

    /**
     * 创建一个流程实例，在权限部分判断当前办理人是否有启动此流程得权限，如果没有则返回Failed表示不能进行此操作！
     */
    public ReturnType createProcessInst(String processInstId, String initType,
                                        Map<RightCtx, Object> ctx) throws BPMException;


    /**
     * @param processInstId  在工作流引擎中创建的流程实例id
     * @param activityInstId 在工作流引擎中创建的活动实例id
     * @param ctx
     * @return 0 - 成功 ，-1 失败
     * @throws BPMException
     */
    public abstract ReturnType startProcessInst(String processInstId,
                                                String activityInstId, Map<RightCtx, Object> ctx) throws BPMException;


    /**
     * 启动一个活动实例
     *
     * @param activityInstId -
     *                       要启动的活动实例
     * @param ctx            上下文环境，包含权限引擎需要的信息
     * @return 0 - 成功 ，-1 失败
     * @throws BPMException
     */
    public abstract ReturnType startActivityInst(String activityInstId, Map<RightCtx, Object> ctx)
            throws BPMException;


    /**
     * 将一个活动实例保存为历史活动
     *
     * @param activityInstHistoryId 保存的活动
     * @param ctx                   上下文环境，包含权限引擎需要的信息
     * @return 0 - 成功 ，-1 失败
     * @throws BPMException
     */
    public abstract ReturnType saveActivityHistoryInst(String activityInstId,
                                                       String activityInstHistoryId, Map<RightCtx, Object> ctx) throws BPMException;

    /**
     * 将一个活动实例拷贝成多个活动实例
     *
     * @param activityInstId     原活动实例
     * @param subActivityInstIds 拷贝成的活动实例
     * @param ctx                上下文环境，包含权限引擎需要的信息
     * @return 0 - 成功 ，-1 失败
     * @throws BPMException
     */
    public abstract ReturnType splitActivityInst(String activityInstId,
                                                 String[] subActivityInstIds, Map<RightCtx, Object> ctx) throws BPMException;


    /**
     * 将一个活动实例路有到指定活动ID
     *
     * @param activityInstId
     * @param activityDefId
     * @param ctx            上下文环境，包含权限引擎需要的信息
     * @return
     * @throws BPMException
     */
    public abstract ReturnType routeTo(String activityInstId,
                                       String activityDefId, Map<RightCtx, Object> ctx) throws BPMException;

    /**
     * 产生抄送COPY
     *
     * @return
     * @throws BPMException
     */
    public abstract ReturnType copyTo(List<ActivityInst> activityInstList, List<String> readers) throws BPMException;


    /**
     * 活动实例退回操作。
     *
     * @param activityInstId        活动实例的标识。
     * @param activityInstHistoryId 退回到某活动实例历史的标识
     * @param ctx                   上下文环境，包含权限引擎需要的信息
     * @return
     * @throws BPMException
     */
    public abstract ReturnType routeBack(String activityInstId,
                                         String activityInstHistoryId, Map<RightCtx, Object> ctx) throws BPMException;


    /**
     * 活动阅毕操作
     *
     * @param activityInstID 活动实例ID
     * @param ctx            权限相关上下文参数
     * @return 结果标识
     * @throws BPMException
     */
    public ReturnType endRead(String activityInstID, String activityInstHistoryID, Map<RightCtx, Object> ctx)
            throws BPMException;


    /**
     * 活动完成操作
     *
     * @param activityInstID        活动实例ID
     * @param activityInstHistoryID 活动历史实例ID
     * @param ctx                   权限相关上下文参数
     * @return 结果标识
     * @throws BPMException
     */
    public ReturnType endTask(String activityInstID, String activityInstHistoryID, Map<RightCtx, Object> ctx)
            throws BPMException;


    /**
     * 活动操作失败
     *
     * @param activityInstID        活动实例ID
     * @param activityInstHistoryID 活动历史实例ID
     * @param ctx                   权限相关上下文参数
     * @return 结果标识
     * @throws BPMException
     */
    public ReturnType abortedTask(String activityInstID, String activityInstHistoryID, Map<RightCtx, Object> ctx)
            throws BPMException;


    /**
     * 删除历史操作
     *
     * @param activityInstHistoryID 活动实例ID
     * @param ctx                   权限相关上下文参数
     * @return 结果标识
     * @throws BPMException
     */
    public ReturnType deleteHistory(String activityInstHistoryID, Map<RightCtx, Object> ctx)
            throws BPMException;


    /**
     * 彻底删除历史操作
     *
     * @param activityInstHistoryID 活动实例ID
     * @param ctx                   权限相关上下文参数
     * @return 结果标识
     * @throws BPMException
     */
    public ReturnType clearHistory(String activityInstHistoryID, Map<RightCtx, Object> ctx)
            throws BPMException;


    /**
     * 收回活动实例，恢复上一节点的状态。<br>
     *
     * @param activityInstId
     * @param ctx            上下文环境，包含权限引擎需要的信息
     * @return
     */
    public abstract ReturnType tackBack(String activityInstId, Map<RightCtx, Object> ctx)
            throws BPMException;


    /**
     * 签收活动实例，启动该活动实例。
     *
     * @param activityInstID 活动实例的标识
     * @param ctx            上下文环境，包含权限引擎需要的信息
     * @return
     * @throws BPMException
     */
    public abstract ReturnType signReceive(String activityInstID, Map<RightCtx, Object> ctx)
            throws BPMException;

    /**
     * 合并分裂的活动实例
     *
     * @param activityInstIds 活动实例标识的数组
     * @param ctx             上下文环境，包含权限引擎需要的信息
     * @return
     * @throws BPMException
     */


    public abstract ReturnType combineActivityInsts(String[] activityInstIds,
                                                    Map<RightCtx, Object> ctx) throws BPMException;

    /**
     * 活动挂起
     */
    public abstract ReturnType suspendActivityInst(String activityInstID,
                                                   Map<RightCtx, Object> ctx) throws BPMException;

    /**
     * 继续活动实例
     */
    public abstract ReturnType resumeActivityInst(String activityInstID, Map<RightCtx, Object> ctx)
            throws BPMException;

    /**
     * 流程实例挂起
     */
    public abstract ReturnType suspendProcessInst(String processInstID, Map<RightCtx, Object> ctx)
            throws BPMException;

    /**
     * 继续流程实例
     */
    public abstract ReturnType resumeProcessInst(String processInstID, Map<RightCtx, Object> ctx)
            throws BPMException;

    /**
     * 中止流程实例
     */
    public abstract ReturnType abortProcessInst(String processInstID, Map<RightCtx, Object> ctx)
            throws BPMException;

    /**
     * 流程实例完成
     */
    public abstract ReturnType completeProcessInst(String processInstID, Map<RightCtx, Object> ctx)
            throws BPMException;

    /**
     * 删除流程实例
     */
    public abstract ReturnType deleteProcessInst(String processInstID, Map<RightCtx, Object> ctx)
            throws BPMException;


    public void setSystemCode(String systemCode);

    public String getSystemCode();


    /**
     * 在指定历史节点重新发起实例
     *
     * @param activityInstId
     * @param activityHistoryInstId
     * @param ctx
     * @return
     * @throws BPMException
     */

    public void copyActivityInstByHistory(String activityInstId, String activityHistoryInstId, Map<RightCtx, Object> ctx) throws BPMException;


    /**
     * 获取表单数据
     *
     * @return
     */
    @MethodChinaName(cname = "获取表单数据")
    public DataMap getActivityInstFormValues(String activityInstID, String userId) throws BPMException;


    /**
     * 更新表单数据
     *
     * @return
     */
    @MethodChinaName(cname = "更新表单数据")
    public void updateActivityHistoryFormValues(String activityHistoryID, String userId, DataMap dataMap) throws BPMException;


    /**
     * 获取表单数据
     *
     * @return
     */
    @MethodChinaName(cname = "获取表单数据")
    public DataMap getActivityHistoryFormValues(String activityHistoryID, String userId) throws BPMException;


    /**
     * 更新表单数据
     *
     * @return
     */
    @MethodChinaName(cname = "更新表单数据")
    public void updateActivityInstFormValues(String activityInstID, String userId, DataMap dataMap) throws BPMException;


    /**
     * 获取表单数据
     *
     * @return
     */
    @MethodChinaName(cname = "获取表单数据")
    public DataMap getProcessInstFormValues(String processInstId, String userId) throws BPMException;


    /**
     * 更新表单数据
     *
     * @return
     */
    @MethodChinaName(cname = "更新表单数据")
    public void updateProcessInstFormValues(String processInstId, String userId, DataMap dataMap) throws BPMException;


    public void setWorkflowClient(WorkflowClientService service);


}
