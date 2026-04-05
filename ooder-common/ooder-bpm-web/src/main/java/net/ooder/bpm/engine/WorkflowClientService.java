/**
 * $RCSfile: WorkflowClientService.java,v $
 * $Revision: 1.3 $
 * $Date: 2016/01/23 16:29:52 $
 * <p>
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine;

import net.ooder.bpm.client.*;
import net.ooder.bpm.client.data.DataMap;
import net.ooder.bpm.engine.query.BPMCondition;
import net.ooder.bpm.enums.activityinst.ActivityInstRightAtt;
import net.ooder.bpm.enums.activityinsthistory.ActivityInstHistoryAtt;
import net.ooder.bpm.enums.right.RightConditionEnums;
import net.ooder.bpm.enums.right.RightCtx;
import net.ooder.bpm.enums.right.RightPermission;
import net.ooder.command.Command;
import net.ooder.common.Filter;
import net.ooder.common.JDSException;
import net.ooder.common.ReturnType;
import net.ooder.config.ListResultModel;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.annotation.MethodChinaName;
import net.ooder.agent.client.iot.DeviceEndPoint;
import net.ooder.org.OrgManager;
import net.ooder.org.Person;
import net.ooder.server.JDSClientService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 客户端服务接口。应用通过对该接口实现的调用实现 与工作流引擎的交互。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 *
 * @author wenzhang li
 * @version 1.0
 */
public interface WorkflowClientService {


    /**
     * 取得系统标识
     */
    @MethodChinaName(cname = "取得系统标识", display = false)
    public String getSystemCode();

    /**
     * 取得SessionHandle
     *
     * @return
     */
    @MethodChinaName(cname = "取得SessionHandle", display = false)
    public JDSSessionHandle getSessionHandle();

    /**
     * 登陆
     *
     * @param connInfo
     *            登陆连接信息
     * @throws BPMException
     */
    @MethodChinaName(cname = "登陆", returnStr = "connect($connInfo)", display = false)
    public void connect(ConnectInfo connInfo) throws JDSException;

    /**
     * 注销
     *
     * @return 结果标识
     * @throws BPMException
     */
    @MethodChinaName(cname = "注销", returnStr = "disconnect()", display = false)
    public ReturnType disconnect() throws JDSException;

    /**
     * 取得登录人信息
     *
     * @return
     */
    @MethodChinaName(cname = "取得登录人信息")
    public ConnectInfo getConnectInfo();


    /**
     * 取得资源管理器
     *
     * @return OrgManager
     */
    @MethodChinaName(cname = "取得资源管理器")
    public OrgManager getOrgManager();


    // --------------------------------------------- 定义相关方法

    /**
     * 取得符合条件的流程定义列表。
     *
     * @param condition
     *            查询条件，例如根据流程定义的名称进行查询。
     * @param filter
     *            扩展属性查询过滤器，可能包含对用户（当前登陆 人可以启动的流程）或系统(OA，CMS或一站式的流程定义）的过滤。
     * @return 所有符合条件的ProcessDef列表
     * @throws BPMException
     */
    @MethodChinaName(cname = "取得符合条件的流程定义列表", returnStr = "getProcessDefVersionList($condition, $filter,$CTX)")

    public ListResultModel<List<ProcessDefVersion>> getProcessDefVersionList(BPMCondition condition, Filter filter,
                                                                             Map<RightCtx, Object> ctx) throws BPMException;

    /**
     * 取得符合条件的流程定义列表。
     *
     * @param condition
     *            查询条件，例如根据流程定义的名称进行查询。
     * @param filter
     *            扩展属性查询过滤器，可能包含对用户（当前登陆 人可以启动的流程）或系统(OA，CMS或一站式的流程定义）的过滤。
     * @return 所有符合条件的ProcessDef列表
     * @throws BPMException
     */
    @MethodChinaName(cname = "取得符合条件的流程定义列表", returnStr = "getProcessDefList($condition,$filter,$CTX)")
    public ListResultModel<List<ProcessDef>> getProcessDefList(BPMCondition condition, Filter filter, Map<RightCtx, Object> ctx)
            throws BPMException;

    /**
     * 获得指定流程定义ID的流程定义
     *
     * @param processDefID
     *            流程定义ID
     * @return 流程定义
     * @throws BPMException
     */
    @MethodChinaName(cname = "获得指定流程定义ID的流程定义", returnStr = "getProcessDef($R('processDefId'))")
    public ProcessDef getProcessDef(String processDefID) throws BPMException;


    /**
     * 获得指定流程版本定义ID的流程版本定义
     *
     * @param processDefVersionID
     *            流程定义ID
     * @return 流程定义
     * @throws BPMException
     */
    @MethodChinaName(cname = "获得指定流程版本定义ID的流程版本定义", returnStr = "getProcessDefVersion($R('processDefVersionID'))")
    public ProcessDefVersion getProcessDefVersion(String processDefVersionID) throws BPMException;

    /**
     * 获得指定活动定义ID的活动定义
     *
     * @param activityDefID
     *            活动定义ID
     * @return 活动定义
     * @throws BPMException
     */
    @MethodChinaName(cname = "获得指定活动定义ID的活动定义", returnStr = "getActivityDef($R('activityDefId'))")
    public ActivityDef getActivityDef(String activityDefID) throws BPMException;

    /**
     * 获得指定路由定义ID的路由定义
     *
     * @param routeDefId
     *            路由定义ID
     * @return 路由定义
     * @throws BPMException
     */
    @MethodChinaName(cname = "获得指定路由定义ID的路由定义", returnStr = "getRouteDef($R('routeDefId'))")
    public RouteDef getRouteDef(String routeDefId) throws BPMException;

    // --------------------------------------------- 实例相关方法

    /**
     * 取得符合条件的流程实例列表
     *
     * @param condition
     *            查询条件，例如根据流程状态，流程类型，流程定义 ID或用户ID进行查询。
     * @param filter
     *            扩展属性过滤器。
     * @param ctx
     *            权限相关上下文参数，由应用（包含应用客户端和引擎中权 限部分插件间的可交互参数）决定其中参数的具体含义。可能包含用户
     *            （当前登陆人做过的流程）的条件参数。
     * @return 所有符合条件的ProcessInst列表
     * @throws BPMException
     */
    @MethodChinaName(cname = "取得符合条件的流程实例列表", returnStr = "getProcessInstList($condition,$filter,$CTX)")
    public ListResultModel<List<ProcessInst>> getProcessInstList(BPMCondition condition, RightConditionEnums conditionEnus, Filter filter, Map<RightCtx, Object> ctx)
            throws BPMException;

    /**
     * 取得符合条件的所有活动实例。
     *
     * @param condition
     *            查询条件，例如根据活动状态(待办或在办的 活动)或所属流程实例进行查询。
     * @param filter
     *            扩展属性过滤器。
     * @param ctx
     *            权限相关上下文参数，由应用（包含应用客户端和引擎中权 限部分插件间的可交互参数）决定其中参数的具体含义。可能包含用户
     *            （当前登陆人做过的活动）的条件参数。
     * @return 所有符合条件的ActivityInst列表
     * @throws BPMException
     */
    @MethodChinaName(cname = "取得符合条件的所有活动实例", returnStr = "getActivityInstList($condition,$filter,$CTX)")
    public  ListResultModel<List<ActivityInst>> getActivityInstList(BPMCondition condition, RightConditionEnums conditionEnus, Filter filter, Map<RightCtx, Object> ctx)
            throws BPMException;


    /**
     * 取得从指定历史分裂出的活动实例
     *
     * @param activityInstHistoryId
     *            活动实例历史ID
     * @param ctx
     *            权限相关上下文参数
     * @return List<ActivityInst>
     * @throws BPMException
     */
    public  List<ActivityInst> getActivityInstListByOutActvityInstHistory(String activityInstHistoryId, Map<RightCtx, Object> ctx) throws BPMException;

    /**
     * 取得活动的历史数据 根据活动实例，这能列出属于当前活动实例分支上的历史活动节点 按照历史的经过得时间顺序排列
     *
     * @param actvityInstId
     *            活动实例ID
     * @param ctx
     *            权限相关上下文参数
     * @return 活动实例历史（ActivityInstHistory）的列表
     * @throws BPMException
     */
    @MethodChinaName(cname = "取得活动的历史数据 根据活动实例", returnStr = "getActivityInstHistoryListByActvityInst($R('actvityInstId'),$CTX)")
    public  List<ActivityInstHistory> getActivityInstHistoryListByActvityInst(String actvityInstId,
                                                                             Map<RightCtx, Object> ctx) throws BPMException;


    /**
     * 取得符合条件的所有活动历史实例。
     *
     * @param condition
     *            查询条件，例如根据历史活动办理状态或所属流程实例进行查询。
     * @param filter
     *            扩展属性过滤器。
     * @param ctx
     *            权限相关上下文参数，由应用（包含应用客户端和引擎中权 限部分插件间的可交互参数）决定其中参数的具体含义。可能包含用户
     *            （当前登陆人做过的活动）的条件参数。
     * @return 所有符合条件的ActivityInstHistory列表
     * @throws BPMException
     */
    @MethodChinaName(cname = "取得符合条件的所有历史活动实例", returnStr = "getActivityInstHistoryList($condition,$filter,$CTX)")
    public  ListResultModel<List<ActivityInstHistory>> getActivityInstHistoryList(BPMCondition condition, RightConditionEnums conditionEnus, Filter filter, Map<RightCtx, Object> ctx)
            throws BPMException;


    /**
     * 取得上一步活动的历史数据 根据活动实例，这能列出属于当前活动实例分支上的历史活动节点 按照历史的经过得时间顺序排列
     *
     * @param actvityInstId
     *            活动实例ID
     * @param ctx
     *            权限相关上下文参数
     * @return 活动实例历史（ActivityInstHistory）的列表
     * @throws BPMException
     */
    @MethodChinaName(cname = "取得上一步活动的历史数据 根据活动实例", returnStr = "getActivityInstHistoryListByActvityInst($R('actvityInstId'),$CTX)")
    public  List<ActivityInstHistory> getLastActivityInstHistoryListByActvityInst(String actvityInstId,
                                                                                 Map<RightCtx, Object> ctx) throws BPMException;


    /**
     * 取得上一步活动的历史数据 根据活动实例，这能列出属于当前活动实例分支上的历史活动节点 按照历史的经过得时间顺序排列
     *
     * @param actvityInstId
     *            活动实例ID
     * @param ctx
     *            权限相关上下文参数
     * @param noSplit
     * @return 活动实例历史（ActivityInstHistory）的列表
     * @throws BPMException
     */
    @MethodChinaName(cname = "取得上一步活动的历史数据 根据活动实例", returnStr = "getActivityInstHistoryListByActvityInst($R('actvityInstId'),$CTX,true)")
    public  List<ActivityInstHistory> getLastActivityInstHistoryListByActvityInst(String actvityInstId,
                                                                                 Map<RightCtx, Object> ctx, boolean noSplit) throws BPMException;


    /**
     * 根据指定历史分裂节点获分裂出去的所有历史
     * @param historyHisroryId
     * @param noSplit//是否包含分裂的节点
     * @return
     * @throws BPMException
     */
    @MethodChinaName(cname = "根据指定历史分裂节点获分裂出去的所有历史", returnStr = "getAllOutActivityInstHistoryByActvityInstHistory($R('actvityInstHistoryId'),true)")
    public  List<ActivityInstHistory> getAllOutActivityInstHistoryByActvityInstHistory(String historyHisroryId, boolean noSplit) throws BPMException;


    /**
     * 取得活动的历史数据， 根据流程实例，列出所有属于当前流程实例的历史数据， 按照历史的前后顺序排序
     *
     * @param processInstId
     *            流程实例ID
     * @param ctx
     *            权限相关上下文参数
     * @return 活动实例历史（ActivityInstHistory）的列表
     * @throws BPMException
     */
    @MethodChinaName(cname = "取得活动的历史数据， 根据流程实例", returnStr = "getActivityInstHistoryListByProcessInst($R('processInstId'),$CTX)")
    public  ListResultModel<List<ActivityInstHistory>> getActivityInstHistoryListByProcessInst(String processInstId,
                                                                             Map<RightCtx, Object> ctx) throws BPMException;

    /**
     * 按照流程实例的ID取得流程实例对象
     *
     * @param processInstID
     *            流程实例ID
     * @return 指定流程实例ID的ProcessInst对象
     * @throws BPMException
     */
    @MethodChinaName(cname = "按照流程实例的ID取得流程实例对象", returnStr = "getProcessInst($R('processInstId'))")
    public ProcessInst getProcessInst(String processInstID) throws BPMException;

    /**
     * 更新流程实例名称（公文标题）
     *
     * @param name
     *            新名称，长度在100字节以内
     * @return 结果标识
     * @throws BPMException
     */
    @MethodChinaName(cname = "更新流程实例名称（公文标题）", returnStr = "updateProcessInstName($R('processInstId'),$R('newProcessInstName'))")
    public ReturnType updateProcessInstName(String processInstId, String name)
            throws BPMException;

    /**
     * 更新流程实例紧急程度
     *
     * @param urgency
     *            新的紧急程度
     * @return 结果标识
     * @throws BPMException
     */
    @MethodChinaName(cname = "更新流程实例紧急程度", returnStr = "updateProcessInstUrgency($R('processInstId'),$R('urgency'))")
    public ReturnType updateProcessInstUrgency(String processInstId,
                                               String urgency) throws BPMException;

    /**
     * 按照活动实例的ID取得活动实例对象
     *
     * @param activityInstID
     *            活动实例ID
     * @return 指定活动实例ID的ActivityInst对象
     * @throws BPMException
     */
    @MethodChinaName(cname = "按照活动实例的ID取得活动实例对象", returnStr = "getActivityInst($R('activityInstId'))")
    public ActivityInst getActivityInst(String activityInstID)
            throws BPMException;


    /**
     * 按照路由实例的ID取得路由实例对象
     *
     * @param routeInstId
     *            活动路由ID
     * @return 指定活动路由ID的RouteInst对象
     * @throws BPMException
     */
    @MethodChinaName(cname = "按照路由实例的ID取得活动实例对象", returnStr = "getRouteInst($R('routeInstId'))")
    public RouteInst getRouteInst(String routeInstId)
            throws BPMException;

    /**
     * 按照活动实例历史的ID取得活动实例历史对象
     *
     * @param activityInstHistoryID
     *            活动实例历史ID
     * @return 指定活动实例历史ID的ActivityInst对象
     * @throws BPMException
     */
    @MethodChinaName(cname = "按照活动实例历史的ID取得活动实例历史对象", returnStr = "getActivityInstHistory($R('activityInstHistoryId'))")
    public ActivityInstHistory getActivityInstHistory(
            String activityInstHistoryID) throws BPMException;

    // --------------------------------------------- 流程启动相关方法

    /**
     * 开始一个流程
     *
     * @param processDefId
     *            要创建的流程定义ID
     * @param processInstName
     *            流程实例名称
     * @param processUrgency
     *            流程紧急程度
     * @param ctx
     *            权限相关上下文参数，由应用（包含应用客户端和引擎中权 限部分插件间的可交互参数）决定其中参数的具体含义。可能包含用户
     *            （当前登陆人的ID）的条件参数。
     * @return 创建的流程实例
     * @throws BPMException
     */
    @MethodChinaName(cname = "开始一个流程", returnStr = "newProcess($R('processDefId'),$R('processInstName'),$R('processUrgency'),$CTX)", display = false)
    public ProcessInst newProcess(String processDefId, String processInstName,
                                  String processUrgency, Map<RightCtx, Object> ctx) throws BPMException;

    /**
     * 开始一个流程
     *
     * @param processDefId
     *            要创建的流程定义ID
     * @param processInstName
     *            流程实例名称
     * @param processUrgency
     *            流程紧急程度
     * @param initType
     *            启动的类型
     * @param ctx
     *            权限相关上下文参数，由应用（包含应用客户端和引擎中权 限部分插件间的可交互参数）决定其中参数的具体含义。可能包含用户
     *            （当前登陆人的ID）的条件参数。
     * @return 创建的流程实例
     * @throws BPMException
     */
    @MethodChinaName(cname = "开始一个流程", returnStr = "newProcess($R('processDefId'),$R('processInstName'),$R('urgency'),$R('initType'),$CTX)", display = false)
    public ProcessInst newProcess(String processDefId, String processInstName,
                                  String processUrgency, String initType, Map<RightCtx, Object> ctx)
            throws BPMException;

    // --------------------------------------------- 路由相关方法

    /**
     * 取得某个活动实例的所有可提交路由的列表
     *
     * @param startActivityInstID
     *            起始活动实例ID
     * @param condition
     *            查询条件。
     * @param routeFilter
     *            路由条件过滤器。
     * @param ctx
     *            权限相关上下文参数，由应用（包含应用客户端和引擎中权 限部分插件间的可交互参数）决定其中参数的具体含义。可能包含用户
     *            （当前登陆人ID）的条件参数。
     * @return 路由定义的列表
     * @throws BPMException
     */
    @MethodChinaName(cname = "取得某个活动实例的所有可提交路由的列表", returnStr = "getNextRoutes($R('activityInstId'),$condition,$filter,$CTX)")
    public List<RouteDef> getNextRoutes(String startActivityInstID, BPMCondition condition,
                                        Filter routeFilter, Map<RightCtx, Object> ctx) throws BPMException;

    /**
     * 将当前用户完成决策的对象转储形成新的实例，本函数完成所有提交后的处理。 ActivityInstWrapper:当前活动实例
     *
     * @param startActivityInstID
     *            起始活动实例ID
     * @param nextActivityDefIDs
     *            要路由到的活动定义ID列表
     * @param ctx
     *            权限相关上下文参数，由应用（包含应用客户端和引擎中权 限部分插件间的可交互参数）决定其中参数的具体含义。可能包含用户
     *            （selectedDealer或selectedReader）的条件参数。
     * @return 结果标识
     * @throws BPMException
     */
    @MethodChinaName(cname = "发送", returnStr = "getNextRoutes($R('activityInstId'),$RL('nextActivityDefIDs'),$CTX),display=false")
    public ReturnType routeTo(String startActivityInstID,
                              List<String> nextActivityDefIDs, List<Map<RightCtx, Object>> ctx) throws BPMException;


    /**
     * 从指定活动历时实例中新增加一个从指定定义节点出发的实例（主要用于补发等需求）
     * 1，返回的是最近分裂的主活动对应的实例节点
     * @param activityHistoryInstId
     * @return ActivityInst
     * @throws BPMException
     */
    @MethodChinaName(cname = "补发", returnStr = "getNextRoutes($R('activityInstId'),$RL('nextActivityDefIDs'),$CTX),display=false")
    public ActivityInst copyActivityInstByHistory(String activityHistoryInstId, Map<RightCtx, Object> ctx) throws BPMException;


    /**
     * 从指定活动历时实例中新增加一个从指定定义节点出发的实例（主要用于补发等需求）
     * 1，返回的是最近分裂的主活动对应的实例节点
     * 2，从主活动上拷贝实例
     * 3，重置活动状态
     * @param activityHistoryInstId
     * @param ctx
     * @param isnew
     * @return ActivityInst
     * @throws BPMException
     */
    @MethodChinaName(cname = "补发", returnStr = "getNextRoutes($R('activityInstId'),$RL('nextActivityDefIDs'),$CTX,$R('isnew')),display=false")
    public ActivityInst copyActivityInstByHistory(String activityHistoryInstId, Map<RightCtx, Object> ctx, boolean isnew) throws BPMException;


    /**
     *  从指定流程实例中新增加一个从指定定义节点出发的实例（主要用于补发等需求）
     * @param processInstId 指定流程实例
     * @param activityDefId 定义出发节点
     * @param ctx
     * @return
     * @throws BPMException
     */
    @MethodChinaName(cname = "从指定流程实例中新增加一个从指定定义节点出发的实例", returnStr = "newActivityInstByActivityDefId($R('processInstId'),$R('activityDefId'),$CTX)", display = false)
    public ActivityInst newActivityInstByActivityDefId(String processInstId, String activityDefId, Map<RightCtx, Object> ctx) throws BPMException;


    /**
     * 抄送处理将当前活动进行复制每人产生一个COPY
     *
     * @param activityHistoryInstId
     *            起始活动历史实例ID
     * @param readers
     *            抄送人员。
     * @return 结果标识
     * @throws BPMException
     */
    @MethodChinaName(cname = "抄送处理将当前活动进行复制每人产生一个COPY", returnStr = "copyTo($R('activityHistoryInstId'),$readers)", display = false)
    public ReturnType copyTo(String activityHistoryInstId,
                             List readers) throws BPMException;

    // --------------------------------------------- 收回相关方法

    /**
     * 判断当前活动能否收回
     *
     * @param activityInstID
     *            活动实例ID
     * @param ctx
     *            权限相关上下文参数
     * @return true-可以收回；false-不能收回。
     * @throws BPMException;
     */
    @MethodChinaName(cname = "判断当前活动能否收回", returnStr = "canTakeBack($R('activityInstId'),$CTX)")
    public boolean canTakeBack(String activityInstID, Map<RightCtx, Object> ctx)
            throws BPMException;


    /**
     * 活动收回操作
     *
     * @param activityInstID
     *            活动实例ID
     * @param ctx
     *            权限相关上下文参数
     * @return 结果标识
     * @throws BPMException
     */
    @MethodChinaName(cname = "活动收回操作", returnStr = "takeBack($R('activityInstId'),$CTX)", display = false)
    public ReturnType takeBack(String activityInstID, Map<RightCtx, Object> ctx)
            throws BPMException;


    //----------------------------------------阅闭操作

    /**
     * 判断当前活动能否阅闭
     *
     * @param activityInstID
     *            活动实例ID
     * @param ctx
     *            权限相关上下文参数
     * @return true-可以阅闭；false-不能阅闭。
     * @throws BPMException
     */
    @MethodChinaName(cname = "判断当前活动能否阅闭", returnStr = "canEndRead($R('activityInstId'),$CTX)")
    public boolean canEndRead(String activityInstID, Map<RightCtx, Object> ctx)
            throws BPMException;

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
    @MethodChinaName(cname = "活动阅毕操作", returnStr = "endRead($R('activityInstId'),$CTX)", display = false)
    public ReturnType endRead(String activityInstID, Map<RightCtx, Object> ctx)
            throws BPMException;


    /**
     * 活动结束操作（任务已成功完成）
     *
     * @param activityInstID
     *            活动实例ID
     * @param ctx
     *            权限相关上下文参数
     * @return 结果标识
     * @throws BPMException
     */
    @MethodChinaName(cname = "任务完成", returnStr = "endTask($R('activityInstId'),$CTX)", display = false)
    public ReturnType endTask(String activityInstID, Map<RightCtx, Object> ctx)
            throws BPMException;

    /**
     * 任务失败
     *
     * @param activityInstID
     *            活动实例ID
     * @param ctx
     *            权限相关上下文参数
     * @return 结果标识
     * @throws BPMException
     */
    @MethodChinaName(cname = "任务失败", returnStr = "abortedTask($R('activityInstId'),$CTX)", display = false)
    public ReturnType abortedTask(String activityInstID, Map<RightCtx, Object> ctx)
            throws BPMException;


    // --------------------------------------------- 退回相关方法

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
    @MethodChinaName(cname = "删除历史操作操作", returnStr = "deleteHistroy($R('activityInstHistoryID'),$CTX)", display = true)

    public ReturnType deleteHistory(String activityInstHistoryID, Map<RightCtx, Object> ctx)
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
    @MethodChinaName(cname = "还原操作操作", returnStr = "restoreHistory($R('activityInstHistoryID'),$CTX)", display = true)

    public ReturnType restoreHistory(String activityInstHistoryID, Map<RightCtx, Object> ctx)
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
    @MethodChinaName(cname = "彻底删除历史操作", returnStr = "clearHistroy($R('activityInstHistoryID'),$CTX)", display = false)

    public ReturnType clearHistory(String activityInstHistoryID, Map<RightCtx, Object> ctx)
            throws BPMException;


    /**
     * 判断某活动能否退回
     *
     * @param activityInstID
     *            活动实例ID
     * @param ctx
     *            权限相关上下文参数
     * @return true-可以退回；false-不能退回。
     * @throws BPMException
     */
    @MethodChinaName(cname = "判断某活动能否退回", returnStr = "canRouteBack($R('activityInstId'),$CTX)")
    public boolean canRouteBack(String activityInstID, Map<RightCtx, Object> ctx)
            throws BPMException;

    /**
     * 取得可以退回的所有活动历史的列表
     *
     * @param activityInstID
     *            活动实例ID
     * @param routeFilter
     *            路由过滤器
     * @param ctx
     *            权限相关上下文参数
     * @return 活动实例历史的列表
     * @throws BPMException
     */
    @MethodChinaName(cname = "取得可以退回的所有活动历史的列表", returnStr = "getRouteBackActivityHistoryInstList($R('activityInstId'),$routeFilter,$CTX)")
    public List<ActivityInstHistory> getRouteBackActivityHistoryInstList(String activityInstID,
                                                                         Filter routeFilter, Map<RightCtx, Object> ctx) throws BPMException;

    /**
     * 退回操作
     *
     * @param fromActivityInstID
     *            需要执行退回操作的活动实例
     * @param toActivityInstHistoryID
     *            要退回到达的活动实例历史
     * @param ctx
     *            权限相关上下文参数
     * @return 结果标识
     * @throws BPMException
     */
    @MethodChinaName(cname = "退回操作", returnStr = "getRouteBackActivityHistoryInstList($R('activityInstId'),$R('activityInstHistoryId'),$CTX)", display = false)
    public ReturnType routeBack(String fromActivityInstID,
                                String toActivityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException;

    // --------------------------------------------- 签收相关方法

    /**
     * 能否banli
     *
     * @param activityInstID
     * @param ctx
     *            权限相关上下文参数 return 结果标识
     * @throws BPMException
     */
    @MethodChinaName(cname = "能否Perform", returnStr = "canPerform($R('activityInstId'),$CTX)")
    public boolean canPerform(String activityInstID, Map<RightCtx, Object> ctx)
            throws BPMException;


    /**
     * 能否签收
     *
     * @param activityInstID
     * @param ctx
     *            权限相关上下文参数 return 结果标识
     * @throws BPMException
     */
    @MethodChinaName(cname = "能否签收", returnStr = "canSignReceive($R('activityInstId'),$CTX)")
    public boolean canSignReceive(String activityInstID, Map<RightCtx, Object> ctx)
            throws BPMException;


    /**
     * 签收操作
     *
     * @param activityInstID
     * @param ctx
     *            权限相关上下文参数
     * @return 结果标识
     * @throws BPMException
     */
    @MethodChinaName(cname = "签收操作", returnStr = "signReceive($R('activityInstId'),$CTX)", display = false)
    public ReturnType signReceive(String activityInstID, Map<RightCtx, Object> ctx)
            throws BPMException;

    // --------------------------------------------- 活动状态转换方法


    /**
     * 活动挂起
     *
     * @param activityInstID
     *            活动实例ID
     * @param ctx
     *            权限相关上下文参数
     * @return 结果标识
     * @throws BPMException
     */
    @MethodChinaName(cname = "活动挂起", returnStr = "suspendActivityInst($R('activityInstId'),$CTX)", display = false)
    public ReturnType suspendActivityInst(String activityInstID, Map<RightCtx, Object> ctx)
            throws BPMException;

    /**
     * 继续活动实例
     *
     * @param activityInstID
     *            活动实例ID
     * @param ctx
     *            权限相关上下文参数
     * @return 结果标识
     * @throws BPMException
     */
    @MethodChinaName(cname = "继续活动实例", returnStr = "resumeActivityInst($R('activityInstId'),$CTX)", display = false)
    public ReturnType resumeActivityInst(String activityInstID, Map<RightCtx, Object> ctx)
            throws BPMException;

    // --------------------------------------------- 流程状态转换方法

    /**
     * 流程实例挂起
     *
     * @param processInstID
     *            流程实例ID
     * @param ctx
     *            权限相关上下文参数
     * @return 结果标识
     * @throws BPMException
     */
    @MethodChinaName(cname = "流程实例挂起", returnStr = "suspendProcessInst($R('processInstId'),$CTX)", display = false)
    public ReturnType suspendProcessInst(String processInstID, Map<RightCtx, Object> ctx)
            throws BPMException;

    /**
     * 继续流程实例
     *
     * @param processInstID
     *            流程实例ID
     * @param ctx
     *            权限相关上下文参数
     * @return 结果标识
     * @throws BPMException
     */
    @MethodChinaName(cname = "继续流程实例", returnStr = "resumeProcessInst($R('processInstId'),$CTX)", display = false)
    public ReturnType resumeProcessInst(String processInstID, Map<RightCtx, Object> ctx)
            throws BPMException;

    /**
     * 中止流程实例
     *
     * @param processInstID
     *            流程实例ID
     * @param ctx
     * @return
     * @throws BPMException
     */
    @MethodChinaName(cname = "中止流程实例", returnStr = "abortProcessInst($R('processInstId'),$CTX)", display = false)
    public ReturnType abortProcessInst(String processInstID, Map<RightCtx, Object> ctx)
            throws BPMException;

    /**
     * 流程实例完成
     *
     * @param processInstID
     *            流程实例ID
     * @param ctx
     *            权限相关上下文参数
     * @return 结果标识
     * @throws BPMException
     */
    @MethodChinaName(cname = "流程实例完成", returnStr = "completeProcessInst($R('processInstId'),$CTX)", display = false)
    public ReturnType completeProcessInst(String processInstID, Map<RightCtx, Object> ctx)
            throws BPMException;

    /**
     * 删除流程实例
     *
     * @param processInstID
     *            流程实例ID
     * @param ctx
     *            权限相关上下文参数
     * @return 结果标识
     * @throws BPMException
     */
    @MethodChinaName(cname = "删除流程实例", returnStr = "deleteProcessInst($R('processInstId'),$CTX)", display = false)
    public ReturnType deleteProcessInst(String processInstID, Map<RightCtx, Object> ctx)
            throws BPMException;

    // --------------------------------------------- 事务控制方法

    /**
     * 开始事务操作
     *
     * @throws BPMException
     */
    @MethodChinaName(cname = "开始事务操作", returnStr = "beginTransaction()", display = false)
    public void beginTransaction() throws BPMException;

    /**
     * 提交事务操作
     *
     * @throws BPMException
     */
    @MethodChinaName(cname = "提交事务操作", returnStr = "commitTransaction()", display = false)
    public void commitTransaction() throws BPMException;

    /**
     * 回滚事务操作
     *
     * @throws BPMException
     */
    @MethodChinaName(cname = "回滚事务操作", returnStr = "rollbackTransaction()", display = false)
    public void rollbackTransaction() throws BPMException;

    // ---------------------------------------------- 权限相关方法 add by lxl
    // 2004-01-15

//
//

    /**
     * 活动定义相关的权限属性
     *
     * @param activityDefId
     *            活动定义的ID
     * @return 属性值
     */
    @MethodChinaName(cname = "活动定义相关的权限属性", returnStr = "getActivityDefRightAttribute($R('activityDefId'))")
    public ActivityDefRight getActivityDefRightAttribute(String activityDefId) throws BPMException;

    @MethodChinaName(cname = "活动定义相关的属性", returnStr = "getActivityDefAttributes($R('activityDefId')")
    public List<AttributeDef> getActivityDefAttributes(String activityDefId) throws BPMException;
//
//
//

    @MethodChinaName(cname = "活动定义相关的属性", returnStr = "getActivityDefEventAttribute($R('activityDefId')")
    public ActivityDefEvent getActivityDefEventAttribute(String activityDefId) throws BPMException;


    /**
     *
     * @param activityDefId
     * @return
     * @throws BPMException=]-
     */
    @MethodChinaName(cname = "活动定义相关的设备属性", returnStr = "getActivityDefDeviceAttribute($R('activityDefId'))")
    public ActivityDefDevice getActivityDefDeviceAttribute(String activityDefId) throws BPMException;


    /**
     * 活动实例的权限属性
     *
     * @param activityInstId
     *            活动实例的ID
     * @param attName
     *            属性名称
     * @return 属性值
     * @throws BPMException
     */
    @MethodChinaName(cname = "getActivityInstRightAttribute", returnStr = "getActivityInstRightAttribute($R('activityInstId'), $R('attName'), $CTX)")
    public List<Person> getActivityInstPersons(String activityInstId,
                                               ActivityInstRightAtt attName) throws BPMException;


    /**
     *
     * @param activityInstId
     * @param attName
     * @return
     * @throws BPMException
     */
    @MethodChinaName(cname = "参与运行的设备", returnStr = "getActivityInstDevices($R('activityInstId'), $R('attName'))")
    public List<DeviceEndPoint> getActivityInstDevices(String activityInstId, ActivityInstRightAtt attName) throws BPMException;


    /**
     *
     * @param activityInstId
     * @return
     * @throws BPMException
     */
    @MethodChinaName(cname = "路由定义相关的设备属性", returnStr = "getActivityInstCommand($R('activityInstId'))")
    public List<Command> getActivityInstCommands(String activityInstId) throws BPMException;


    /**
     * 活动实例历史的权限属性
     *
     * @param activityInstHistoryId
     *            活动实例历史的ID
     * @param attName
     *            属性名称
     * @return 属性值
     * @throws BPMException
     */
    @MethodChinaName(cname = "活动实例历史的权限属性", returnStr = "getActivityInstRightAttribute($R('activityInstHistoryId'), $R('attName'))")
    public List<Person> getActivityInstHistoryPersons(
            String activityInstHistoryId, ActivityInstHistoryAtt attName)
            throws BPMException;


    /**
     * 判断当前人对活动实例的权限
     *
     * @param activityInstId
     * @param ctx
     *            权限相关上下文参数
     * @return 属性值
     * @throws BPMException
     */
    @MethodChinaName(cname = "判断当前人对活动实例的权限", returnStr = "queryPermissionToActivityInst($R('activityInstId'), $CTX)")
    public boolean queryPermissionToActivityInst(String activityInstId, Map<RightCtx, Object> ctx)
            throws BPMException;

    /**
     * 得到当前人对活动实例的所有权限列表
     *
     * @param activityInstId
     * @param ctx
     *            权限相关上下文参数
     * @return
     * @throws BPMException
     */
    @MethodChinaName(cname = "得到当前人对活动实例的所有权限列表", returnStr = "queryPermissionToActivityInst($R('activityInstId'), $CTX)")
    public List<RightPermission> queryAllPermissionToActivityInst(String activityInstId, Map<RightCtx, Object> ctx)
            throws BPMException;

    public DataEngine getMapDAODataEngine();


    /**
     * 获取表单数据
     *
     * @return
     */
    @MethodChinaName(cname = "获取表单数据")
    public DataMap getActivityInstFormValues(String activityInstID) throws BPMException;


    /**
     * 更新表单数据
     *
     * @return
     */
    @MethodChinaName(cname = "更新表单数据")
    public void updateActivityHistoryFormValues(String activityHistoryID, DataMap dataMap) throws BPMException;


    /**
     * 获取表单数据
     *
     * @return
     */
    @MethodChinaName(cname = "获取表单数据")
    public DataMap getActivityHistoryFormValues(String activityHistoryID) throws BPMException;


    /**
     * 更新表单数据
     *
     * @return
     */
    @MethodChinaName(cname = "更新表单数据")
    public void updateActivityInstFormValues(String activityHistoryID, DataMap dataMap) throws BPMException;


    /**
     * 获取表单数据
     *
     * @return
     */
    @MethodChinaName(cname = "获取表单数据")
    public DataMap getProcessInstFormValues(String processInstId) throws BPMException;


    /**
     * 更新表单数据
     *
     * @return
     */
    @MethodChinaName(cname = "更新表单数据")
    public void updateProcessInstFormValues(String processInstId, DataMap dataMap) throws BPMException;

    /**
     *  开始展示活动表单
     *
     * @param activityInstId
     *            权限相关上下文参数
     * @return
     * @throws BPMException
     */
    @MethodChinaName(cname = "开始展示活动表单", returnStr = "updateActivityInstMapDAO($R('activityInstId')", display = false)
    public ReturnType display(String activityInstId) throws BPMException;

    /**
     * 为办理人添加历史节点标记
     * @param activityInstHistoryID
     * @param tagName
     * @param ctx
     * @return
     * @throws BPMException
     */
    @MethodChinaName(cname = " 为办理人添加历史节点标记", returnStr = "addPersonTagToHistory($R('activityInstHistoryID'),$R('tagName'),$CTX)", display = false)
    public ReturnType addPersonTagToHistory(String activityInstHistoryID, ActivityInstHistoryAtt tagName, Map<RightCtx, Object> ctx) throws BPMException;

    /**
     * 删除办理人标记
     * @param activityInstHistoryID
     * @param tagName
     * @param ctx
     * @return
     * @throws BPMException
     */
    @MethodChinaName(cname = " 为办理人添加历史节点标记", returnStr = "deletePersonTagToHistory($R('activityInstHistoryID'),$R('tagName'),$CTX)", display = false)
    public ReturnType deletePersonTagToHistory(String activityInstHistoryID, ActivityInstHistoryAtt tagName, Map<RightCtx, Object> ctx) throws BPMException;


    public ActivityDef getFirstActivityDefInProcess(String processDefVersionId) throws BPMException;


    public void setOrgManager(OrgManager orgManager);

    public FileEngine getfileEngine();

    public JDSClientService getJdsClient();


}
