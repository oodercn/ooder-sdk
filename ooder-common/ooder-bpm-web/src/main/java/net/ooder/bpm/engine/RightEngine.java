/**
 * $RCSfile: RightEngine.java,v $
 * $Revision: 1.4 $
 * $Date: 2016/01/23 16:29:52 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine;

import net.ooder.bpm.client.ActivityDefRight;
import net.ooder.bpm.client.ActivityInst;
import net.ooder.bpm.enums.activityinst.ActivityInstRightAtt;
import net.ooder.bpm.enums.activityinsthistory.ActivityInstHistoryAtt;
import net.ooder.bpm.enums.right.RightConditionEnums;
import net.ooder.bpm.enums.right.RightCtx;
import net.ooder.bpm.enums.right.RightGroupEnums;
import net.ooder.common.Filter;
import net.ooder.common.ReturnType;
import net.ooder.org.Person;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Generation - Code and
 *         Comments
 */
public interface RightEngine {

	
	/** 环境上下文 */
	//public static final String CONSTANST_BPM_CONTEXT = "BPM_CONTEXT";


	/**
	 * 取得流程定义的权限过滤器
	 * 
	 * @param ctx
	 * @return
	 * @throws BPMException
	 */
	public abstract Filter getProcessDefListFilter(Map<RightCtx,Object> ctx) throws BPMException;

	/**
	 * 取的流程实例的过滤器
	 * 
	 * @param ctx
	 *            接受CTX_CONDITION_WAITEDWORK 和 CTX_CONDITION_ALLWORK参数
	 * @return
	 * @throws BPMException
	 */
	 public Filter getProcessInstListFilter(RightConditionEnums condition, Map<RightCtx,Object> ctx)
			throws BPMException;

	/**
	 * 取得活动实例的过滤器
	 * 
	 * @param ctx
	 *            接受CTX_CONDITION_JOINWORK" 和 CTX_CONDITION_PERFORMERWORK参数
	 * @return
	 * @throws BPMException
	 */
	  public Filter getActivityInstListFilter(RightConditionEnums condition, Map<RightCtx,Object> ctx)
			throws BPMException;
	
	/**
	 * 取得活动历史的过滤器
	 * 
	 * @param ctx
	 *            接受CTX_CONDITION_WAITEDWORK 和 CTX_CONDITION_ALLWORK参数
	 * @return
	 * @throws BPMException
	 */
	public abstract Filter getActivityInstHistoryListFilter(RightConditionEnums condition, Map<RightCtx,Object> ctx)
			throws BPMException;
	
	
	

	/**
	 * 创建流程实例
	 * 
	 * @param processInstId
	 *            在工作流引擎中创建的流程实例id
	 * @param ctx
	 *            上下文环境，包含权限引擎需要的信息
	 * @return 0 - 成功 ，-1 失败
	 * @throws BPMException
	 */
	public abstract ReturnType createProcessInst(String processInstId, Map<RightCtx,Object> ctx)
			throws BPMException;

	/**
	 * 创建一个流程实例，在权限部分判断当前办理人是否有启动此流程得权限，如果没有则返回Failed表示不能进行此操作！
	 */
	public ReturnType createProcessInst(String processInstId, String initType,
			Map<RightCtx,Object> ctx) throws BPMException;

	/**
	 * 创建活动实例
	 * 
	 * @param processInstId
	 *            在工作流引擎中创建的流程实例ID
     * @param activityInstId
	 *            在工作流引擎中创建的活动实例ID
	 * @param ctx
	 *            上下文环境，包含权限引擎需要的信息
	 * @return 0 - 成功 ，-1 失败
	 * @throws BPMException
	 */
	public abstract ReturnType startProcessInst(String processInstId,
			String activityInstId, Map<RightCtx,Object> ctx) throws BPMException;

	/**
	 * 取得活动办理人的候选人， 解析活动定义的公式来确定候选人！
	 * 
	 * @param activityDefId
	 *            指定的活动ID
	 * @param ctx
	 *            上下文环境，包括
	 * @return
	 * @throws BPMException
	 */
	public List<Person> getPerformerCandidate(String activityDefId, Map<RightCtx,Object> ctx)
			throws BPMException;

	/**
	 * 启动一个活动实例
	 * 
	 * @param activityInstId -
	 *            要启动的活动实例
	 * @param ctx
	 *            上下文环境，包含权限引擎需要的信息
	 * @return 0 - 成功 ，-1 失败
	 * @throws BPMException
	 */
	public abstract ReturnType startActivityInst(String activityInstId, Map<RightCtx,Object> ctx)
			throws BPMException;

	/**
	 * 判断某人是否有启动流程实例的权限
	 * 
	 * @param processInstId
	 *            流程实例的ID
	 * @param ctx
	 *            上下文环境，包含权限引擎需要的信息
	 * @return
	 * @throws BPMException
	 */
	public ReturnType hasRightToStartProcess(String processInstId, Map<RightCtx,Object> ctx)
			throws BPMException;

	/**
	 * 将一个活动实例保存为历史活动（内部方法）
	 * 
	 * @param activityInstId
	 *            保存的活动实例ID
	 * @param activityInstHistoryId
	 *            保存的历史活动ID
	 * @param ctx
	 *            上下文环境，包含权限引擎需要的信息
	 * @return 0 - 成功 ，-1 失败
	 * @throws BPMException
	 */
	public abstract ReturnType saveActivityHistoryInst(String activityInstId,
			String activityInstHistoryId, Map<RightCtx,Object> ctx) throws BPMException;

	/**
	 * 将一个活动实例拷贝成多个活动实例
	 * 
	 * @param activityInstId
	 *            原活动实例
	 * @param subActivityInstIds
	 *            拷贝成的活动实例
	 * @param ctx
	 *            上下文环境，包含权限引擎需要的信息
	 * @return 0 - 成功 ，-1 失败
	 * @throws BPMException
	 */
	public abstract ReturnType splitActivityInst(String activityInstId,
			String[] subActivityInstIds,Map<RightCtx,Object> ctx) throws BPMException;

	/**
	 * 更换某个活动实例的当前办理人。
	 * 
	 * @param activityInstId
	 *            活动实例的标识
	 * @param ctx
	 *            上下文环境，包含办理人的信息。
	 * @return
	 * @throws BPMException
	 */
	public ReturnType changePerformer(String activityInstId,Map<RightCtx,Object> ctx)
			throws BPMException;

	/**
	 * 将一个活动实例路有到指定活动节点
	 * 
	 * @param activityInstId
	 * @param activityDefId
	 * @param ctx
	 *            上下文环境，包含权限引擎需要的信息
	 * @return
	 * @throws BPMException
	 */
	public abstract ReturnType routeTo(String activityInstId,
			String activityDefId, Map<RightCtx,Object> ctx) throws BPMException;

	/**
	 * 产生抄送COPY
	 * 
	 * @param activityInstList
	 * @param readers

	 * @return
	 * @throws BPMException
	 */
	public abstract ReturnType copyTo(List<ActivityInst> activityInstList, List<String> readers) throws BPMException;

	
	/**
	 * 是否可以退回。
	 * 
	 * @param activityInstId
	 * @param ctx
	 *            上下文环境，包含权限引擎需要的信息
	 * @return
	 * @throws BPMException
	 */
	public abstract boolean canRouteBack(String activityInstId, Map<RightCtx,Object> ctx)
			throws BPMException;

	/**
	 * 活动实例退回操作。
	 * 
	 * @param activityInstId
	 *            活动实例的标识。
	 * @param activityInstHistoryId
	 *            退回到某活动实例历史的标识
	 * @param ctx
	 *            上下文环境，包含权限引擎需要的信息
	 * @return
	 * @throws BPMException
	 */
	public abstract ReturnType routeBack(String activityInstId,
			String activityInstHistoryId, Map<RightCtx,Object> ctx) throws BPMException;

	
	/**
	 * 活动阅毕操作
	 * 
	 * @param activityInstID
	 *            活动实例ID
	 * @param ctx
	 *            权限相关上下文参数
	 * @return 结果标识
	 * @throws BPMException
	 */
	public ReturnType endRead(String activityInstID,String activityInstHistoryID, Map<RightCtx,Object> ctx)
			throws BPMException;
	
	/**
	 * 活动完成操作
	 * 
	 * @param activityInstID
	 *            活动实例ID
	 * @param activityInstHistoryID
	 *            活动历史实例ID           
	 * @param ctx
	 *            权限相关上下文参数
	 * @return 结果标识
	 * @throws BPMException
	 */
	public ReturnType endTask(String activityInstID,String activityInstHistoryID, Map<RightCtx,Object> ctx)
			throws BPMException;
	

	
	/**
	 * 活动操作失败
	 * 
	 * @param activityInstID
	 *            活动实例ID
	 * @param activityInstHistoryID
	 *            活动历史实例ID            
	 * @param ctx
	 *            权限相关上下文参数
	 * @return 结果标识
	 * @throws BPMException
	 */
	public ReturnType abortedTask(String activityInstID,String activityInstHistoryID, Map<RightCtx,Object> ctx)
	        throws BPMException;

	/**
	 * 删除历史操作
	 * 
	 * @param activityInstHistoryID
	 *            活动实例ID
	 * @param ctx
	 *            权限相关上下文参数
	 * @return 结果标识
	 * @throws BPMException
	 */
	public ReturnType deleteHistory(String activityInstHistoryID, Map<RightCtx,Object> ctx)
			throws BPMException;
	
	/**
	 * 还原历史操作
	 * 
	 * @param activityInstHistoryID
	 *            活动实例ID
	 * @param ctx
	 *            权限相关上下文参数
	 * @return 结果标识
	 * @throws BPMException
	 */
	public ReturnType restoreHistory(String activityInstHistoryID, Map<RightCtx,Object> ctx)
			throws BPMException;
	
	

	/**
	 * 彻底删除历史操作
	 * 
	 * @param activityInstHistoryID
	 *            活动实例ID
	 * @param ctx
	 *            权限相关上下文参数
	 * @return 结果标识
	 * @throws BPMException
	 */
	public ReturnType clearHistory(String activityInstHistoryID,Map<RightCtx,Object> ctx)
			throws BPMException;
	
	
	/**
	 * 是否可以收回。<br>
	 * 
	 * @param activityInstId
	 * @param ctx
	 *            上下文环境，包含权限引擎需要的信息
	 * @return
	 * @throws BPMException
	 */
	public boolean canTakeBack(String activityInstId, Map<RightCtx,Object> ctx)
			throws BPMException;

	/**
	 * 收回活动实例，恢复上一节点的状态。<br>
	 * 
	 * @param activityInstId
	 * @param ctx
	 *            上下文环境，包含权限引擎需要的信息
	 * @return
	 */
	public abstract ReturnType tackBack(String activityInstId, Map<RightCtx,Object> ctx)
			throws BPMException;

	/**
	 * 查询是否能签收
	 * 
	 * @param activityInstID
	 * @param ctx
	 *            上下文环境，包含权限引擎需要的信息
	 * @return
	 */
	public abstract boolean canSignReceive(String activityInstID, Map<RightCtx,Object> ctx)
			throws BPMException;

	/**
	 * 签收活动实例，启动该活动实例。
	 * 
	 * @param activityInstID
	 *            活动实例的标识
	 * @param ctx
	 *            上下文环境，包含权限引擎需要的信息
	 * @return
	 * @throws BPMException
	 */
	public abstract ReturnType signReceive(String activityInstID, Map<RightCtx,Object> ctx)
			throws BPMException;

	/**
	 * 合并分裂的活动实例
	 * 
	 * @param activityInstIds
	 *            活动实例标识的数组
	 * @param ctx
	 *            上下文环境，包含权限引擎需要的信息
	 * @return
	 * @throws BPMException
	 */
	
	
	
	public abstract ReturnType combineActivityInsts(String[] activityInstIds,
			Map<RightCtx,Object> ctx) throws BPMException;

	/** 活动挂起 */
	public abstract ReturnType suspendActivityInst(String activityInstID,
			Map<RightCtx,Object> ctx) throws BPMException;

	/** 继续活动实例 */
	public abstract ReturnType resumeActivityInst(String activityInstID, Map<RightCtx,Object> ctx)
			throws BPMException;

	/** 流程实例挂起 */
	public abstract ReturnType suspendProcessInst(String processInstID, Map<RightCtx,Object> ctx)
			throws BPMException;

	/** 继续流程实例 */
	public abstract ReturnType resumeProcessInst(String processInstID, Map<RightCtx,Object> ctx)
			throws BPMException;

	/** 中止流程实例 */
	public abstract ReturnType abortProcessInst(String processInstID, Map<RightCtx,Object> ctx)
			throws BPMException;

	/** 流程实例完成 */
	public abstract ReturnType completeProcessInst(String processInstID, Map<RightCtx,Object> ctx)
			throws BPMException;

	/** 删除流程实例 */
	public abstract ReturnType deleteProcessInst(String processInstID, Map<RightCtx,Object> ctx)
			throws BPMException;

	// ---------------------------------------------- 权限相关方法 add by lxl
	// 2004-01-15


	/**
	 * 活动定义相关的权限属性
	 * 
	 * @param activityDefId
	 *            活动定义的ID
	 * @return
	 * @throws BPMException
	 */
	public ActivityDefRight getActivityDefRightAttribute(String activityDefId) throws BPMException;

	/**
	 * 活动实例的权限属性
	 * 
	 * @param activityInstId
	 *            活动实例的ID
	 * @param attName
	 *            属性名称
	 * @param ctx
	 * @return
	 * @throws BPMException
	 */
	public List<Person> getActivityInstRightAttribute(String activityInstId,ActivityInstRightAtt attName, Map<RightCtx,Object> ctx) throws BPMException;

	/**
	 * 活动实例历史的权限属性
	 * 
	 * @param activityInstHistoryId
	 *            活动实例历史的ID
	 * @param attName
	 *            属性名称
	 * @param ctx
	 * @return
	 * @throws BPMException
	 */
	public List<Person> getActivityInstHistoryRightAttribute(
			String activityInstHistoryId, ActivityInstHistoryAtt attName, Map<RightCtx,Object> ctx)
			throws BPMException;

	/**
	 * 判断当前人对活动实例的权限
	 * 
	 * @param activityInstId
	 * @param ctx
	 * @return
	 * @throws BPMException
	 */
	public boolean queryPermissionToActivityInst(String activityInstId, Map<RightCtx,Object> ctx)
			throws BPMException;

	/**
	 * 得到当前人对活动实例的所有权限列表
	 * 
	 * @param activityInstId
	 * @param ctx
	 * @return
	 * @throws BPMException
	 */
	public  List<RightGroupEnums> queryAllPermissionToActivityInst(String activityInstId, Map<RightCtx,Object> ctx)
			throws BPMException;
	/**
	 * 在指定历史节点重新发起实例
	 * 
	 * @param activityInstId
	 * @param activityHistoryInstId
	 * @param ctx
	 * @return
	 * @throws BPMException 
	 */
	public  void copyActivityInstByHistory(String activityInstId, String activityHistoryInstId,Map<RightCtx,Object> ctx) throws BPMException;
	
	
	
	/**
	 * 为办理人添加历史节点标记
	 * @param activityInstHistoryID
	 * @param tagName
	 * @param ctx
	 * @return
	 * @throws BPMException
	 */
	public ReturnType addPersonTagToHistory(String activityInstHistoryID, ActivityInstHistoryAtt tagName, Map<RightCtx,Object> ctx) throws BPMException ;
	
	/**
	 * 删除办理人标记
	 * @param activityInstHistoryID
	 * @param tagName
	 * @param ctx
	 * @return
	 * @throws BPMException
	 */
	public ReturnType deletePersonTagToHistory(String activityInstHistoryID,ActivityInstHistoryAtt tagName, Map<RightCtx,Object> ctx) throws BPMException;

}
