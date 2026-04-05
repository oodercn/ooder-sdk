/**
 * 
 */
package net.ooder.bpm.engine;

import java.util.List;
import java.util.Map;

import net.ooder.annotation.AttributeName;
import net.ooder.bpm.enums.activitydef.service.ActivityDefServiceAtt;
import net.ooder.bpm.enums.activityinst.ActivityInstRightAtt;
import net.ooder.bpm.enums.right.RightCtx;
import net.ooder.common.ReturnType;

public interface ServiceEngine {

    /**
     * 创建活动实例
     * 
     * @param processInstId
     *            在工作流引擎中创建的流程实例ID
     * @param activityInstId
     *            在工作流引擎中创建的活动实例ID
     * @param ctx
     *            上下文环境，包含设备引擎需要的信息
     * @return 0 - 成功 ，-1 失败
     * @throws BPMException
     */
    public abstract ReturnType startProcessInst(String processInstId, String activityInstId, Map<RightCtx,Object> ctx) throws BPMException;

    /**
     * 取得活动当前执行设备的待执行设备， 解析活动定义的公式来确定待执行设备！
     * 
     * @param activityDefId
     *            指定的活动ID
     * @param ctx
     *            上下文环境，包括
     * @return
     * @throws BPMException
     */
    public List getPerformerCandidate(String activityDefId, Map<RightCtx,Object> ctx) throws BPMException;

    /**
     * 启动一个活动实例
     * 
     * @param activityInstId
     *            - 要启动的活动实例
     * @param ctx
     *            上下文环境，包含设备引擎需要的信息
     * @return 0 - 成功 ，-1 失败
     * @throws BPMException
     */
    public abstract ReturnType startActivityInst(String activityInstId, Map<RightCtx,Object> ctx) throws BPMException;

    /**
     * 判断某设备是否有启动流程实例的设备
     * 
     * @param processInstId
     *            流程实例的ID
     * @param ctx
     *            上下文环境，包含设备引擎需要的信息
     * @return
     * @throws BPMException
     */
    public ReturnType hasServiceToStartProcess(String processInstId, Map<RightCtx,Object> ctx) throws BPMException;

    /**
     * 将一个活动实例保存为历史活动（内部方法）
     * 
     * @param activityInstId
     *            保存的活动实例ID
     * @param activityInstHistoryId
     *            保存的历史活动ID
     * @param ctx
     *            上下文环境，包含设备引擎需要的信息
     * @return 0 - 成功 ，-1 失败
     * @throws BPMException
     */
    public abstract ReturnType saveActivityHistoryInst(String activityInstId, String activityInstHistoryId, Map<RightCtx,Object> ctx) throws BPMException;

    /**
     * 将一个活动实例拷贝成多个活动实例
     * 
     * @param srcActivityInst
     *            原活动实例
     * @param subActivityInsts
     *            拷贝成的活动实例
     * @param ctx
     *            上下文环境，包含设备引擎需要的信息
     * @return 0 - 成功 ，-1 失败
     * @throws BPMException
     */
    public abstract ReturnType splitActivityInst(String activityInstId, String[] subActivityInstIds, Map<RightCtx,Object> ctx) throws BPMException;

    /**
     * 更换某个活动实例的当前当前执行设备。
     * 
     * @param activityInstId
     *            活动实例的标识
     * @param ctx
     *            上下文环境，包含当前执行设备的信息。
     * @return
     * @throws BPMException
     */
    public ReturnType changePerformer(String activityInstId, Map<RightCtx,Object> ctx) throws BPMException;

    /**
     * 将一个活动实例路有到指定活动节点
     * 
     * @param activityInstId
     * @param activityDefId
     * @param ctx
     *            上下文环境，包含设备引擎需要的信息
     * @return
     * @throws BPMException
     */
    public abstract ReturnType routeTo(String activityInstId, String activityDefId, Map<RightCtx,Object> ctx) throws BPMException;

    /**
     * 活动完成操作
     * 
     * @param activityInstID
     *            活动实例ID
     * @param activityInstHistoryID
     *            活动历史实例ID
     * @param ctx
     *            设备相关上下文参数
     * @return 结果标识
     * @throws BPMException
     */
    public ReturnType endTask(String activityInstID, String activityInstHistoryID, Map<RightCtx,Object> ctx) throws BPMException;

    /**
     * 活动操作失败
     * 
     * @param activityInstID
     *            活动实例ID
     * @param activityInstHistoryID
     *            活动历史实例ID
     * @param ctx
     *            设备相关上下文参数
     * @return 结果标识
     * @throws BPMException
     */
    public ReturnType abortedTask(String activityInstID, String activityInstHistoryID, Map<RightCtx,Object> ctx) throws BPMException;

    /**
     * 查询是否能签收
     * 
     * @param activityInstID
     * @param ctx
     *            上下文环境，包含设备引擎需要的信息
     * @return
     */
    public abstract boolean canSignReceive(String activityInstID, Map<RightCtx,Object> ctx) throws BPMException;

    /**
     * 签收活动实例，启动该活动实例。
     * 
     * @param activityInstID
     *            活动实例的标识
     * @param ctx
     *            上下文环境，包含设备引擎需要的信息
     * @return
     * @throws BPMException
     */
    public abstract ReturnType signReceive(String activityInstID, Map<RightCtx,Object> ctx) throws BPMException;

    /**
     * 合并分裂的活动实例
     * 
     * @param activityInstIds
     *            活动实例标识的数组
     * @param ctx
     *            上下文环境，包含设备引擎需要的信息
     * @return
     * @throws BPMException
     */

    public abstract ReturnType combineActivityInsts(String[] activityInstIds, Map<RightCtx,Object> ctx) throws BPMException;

    /** 活动挂起 */
    public abstract ReturnType suspendActivityInst(String activityInstID, Map<RightCtx,Object> ctx) throws BPMException;

    /** 继续活动实例 */
    public abstract ReturnType resumeActivityInst(String activityInstID, Map<RightCtx,Object> ctx) throws BPMException;

    /** 流程实例挂起 */
    public abstract ReturnType suspendProcessInst(String processInstID, Map<RightCtx,Object> ctx) throws BPMException;

    /** 继续流程实例 */
    public abstract ReturnType resumeProcessInst(String processInstID, Map<RightCtx,Object> ctx) throws BPMException;

    /** 中止流程实例 */
    public abstract ReturnType abortProcessInst(String processInstID, Map<RightCtx,Object> ctx) throws BPMException;

    /** 流程实例完成 */
    public abstract ReturnType completeProcessInst(String processInstID, Map<RightCtx,Object> ctx) throws BPMException;

    /** 删除流程实例 */
    public abstract ReturnType deleteProcessInst(String processInstID, Map<RightCtx,Object> ctx) throws BPMException;

    /**
     * 流程定义相关的设备属性
     * 
     * @param processDefVersionId
     *            流程定义的ID
     * @param attName
     *            属性名称
     * @param ctx
     * @return
     * @throws BPMException
     */
    public Object getProcessDefVersionServiceAttribute(String processDefVersionId, AttributeName attName, Map<RightCtx,Object> ctx) throws BPMException;

    /**
     * 活动定义相关的设备属性
     * 
     * @param activityDefId
     *            活动定义的ID
     * @param attName
     *            属性名称
     * @param ctx
     * @return
     * @throws BPMException
     */
    public Object getActivityDefServiceAttribute(String activityDefId, ActivityDefServiceAtt attName, Map<RightCtx,Object> ctx) throws BPMException;

    /**
     * 路由定义相关的设备属性
     * 
     * @param routeDefId
     *            路由定义的ID
     * @param attName
     *            属性名称
     * @param ctx
     * @return
     * @throws BPMException
     */
    public Object getRouteDefServiceAttribute(String routeDefId, AttributeName attName, Map<RightCtx,Object> ctx) throws BPMException;

    /**
     * 活动实例的设备属性
     * 
     * @param activityInstId
     *            活动实例的ID
     * @param attName
     *            属性名称
     * @param ctx
     * @return
     * @throws BPMException
     */
    public Object getActivityInstServiceAttribute(String activityInstId, ActivityInstRightAtt attName, Map<RightCtx,Object> ctx) throws BPMException;

    /**
     * 活动实例历史的设备属性
     * 
     * @param activityInstHistoryId
     *            活动实例历史的ID
     * @param attName
     *            属性名称
     * @param ctx
     * @return
     * @throws BPMException
     */
    public Object getActivityInstHistoryServiceAttribute(String activityInstHistoryId, AttributeName  attName, Map<RightCtx,Object> ctx) throws BPMException;

    /**
     * 判断当前设备对活动实例的设备
     * 
     * @param activityInstId
     * @param ctx
     * @return
     * @throws BPMException
     */
    public boolean queryPermissionToActivityInst(String activityInstId, Map<RightCtx,Object> ctx) throws BPMException;

    /**
     * 得到当前设备对活动实例的所有设备列表
     * 
     * @param activityInstId
     * @param ctx
     * @return
     * @throws BPMException
     */
    public List queryAllPermissionToActivityInst(String activityInstId, Map<RightCtx,Object> ctx) throws BPMException;

    public  ReturnType createProcessInst(String processInstId, Map<RightCtx,Object> rightCtx)throws BPMException;

    public  ReturnType createProcessInst(String processInstId, String initType, Map<RightCtx,Object> rightCtx)throws BPMException;

    public  ReturnType tackBack(String activityInstID, Map<RightCtx,Object> rightCtx)throws BPMException;

    public abstract boolean canTakeBack(String activityInstID, Map<RightCtx,Object> rightCtx);

    public abstract boolean canRouteBack(String activityInstID, Map<RightCtx,Object> rightCtx);

    public abstract ReturnType routeBack(String fromActivityInstID, String toActivityInstHistoryID, Map<RightCtx,Object> rightCtx);

    public abstract ReturnType endRead(String activityInstID, String activityHistoryId, Map<RightCtx,Object> ctxRight);


 
}
