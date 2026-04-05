package net.ooder.bpm.client.service;

import net.ooder.bpm.client.*;
import net.ooder.common.ReturnType;
import net.ooder.config.ResultModel;
import net.ooder.annotation.MethodChinaName;

import java.util.List;

public interface IDSClientService {


    /**
     * 按照活动实例的ID取得活动实例对象
     *
     * @param activityInstID 活动实例ID
     * @return 指定活动实例ID的ActivityInst对象
     */
    @MethodChinaName(cname = "按照活动实例的ID取得活动实例对象", returnStr = "getActivityInst($R('activityInstId'))")
    public ResultModel<ActivityInst> getActivityInst(String activityInstID);


    /**
     * 获得指定流程定义ID的流程定义
     *
     * @param processDefID 流程定义ID
     * @return 流程定义
     */
    @MethodChinaName(cname = "获得指定流程定义ID的流程定义", returnStr = "getProcessDef($R('processDefId'))")
    public ResultModel<ProcessDef> getProcessDef(String processDefID);


    /**
     * 获得指定活动定义ID的活动定义
     *
     * @param activityDefID 活动定义ID
     * @return 活动定义
     */
    @MethodChinaName(cname = "获得指定活动定义ID的活动定义", returnStr = "getActivityDef($R('activityDefId'))")
    public ResultModel<ActivityDef> getActivityDef(String activityDefID);

    /**
     * 获得指定路由定义ID的路由定义
     *
     * @param routeDefId 路由定义ID
     * @return 路由定义
     */
    @MethodChinaName(cname = "获得指定路由定义ID的路由定义", returnStr = "getRouteDef($R('routeDefId'))")
    public ResultModel<RouteDef> getRouteDef(String routeDefId);

    /**
     * 开始一个流程
     *
     * @param processDefId    要创建的流程定义ID
     * @param processInstName 流程实例名称
     * @return 创建的流程实例
     */
    @MethodChinaName(cname = "开始一个流程", returnStr = "newProcess($R('processDefId'),$R('processInstName'),$R('urgency'),$R('initType'),$CTX)", display = false)
    public ResultModel<ActivityInst> newProcess(String processDefId, String processInstName);


    /**
     * 更新流程实例名称（公文标题）
     *
     * @param name 新名称，长度在100字节以内
     * @return 结果标识
     */
    @MethodChinaName(cname = "更新流程实例名称（公文标题）", returnStr = "updateProcessInstName($R('processInstId'),$R('newProcessInstName'))")
    public ResultModel<ReturnType> updateProcessInstName(String processInstId, String name);

    /**
     * 获得指定流程版本定义ID的流程版本定义
     *
     * @param processDefVersionID 流程定义ID
     * @return 流程定义
     */
    @MethodChinaName(cname = "获得指定流程版本定义ID的流程版本定义", returnStr = "getProcessDefVersion($R('processDefVersionID'))")
    public ResultModel<ProcessDefVersion> getProcessDefVersion(String processDefVersionID);

    /**
     * 按照流程实例的ID取得流程实例对象
     *
     * @param processInstID 流程实例ID
     * @return 指定流程实例ID的ProcessInst对象
     */
    @MethodChinaName(cname = "按照流程实例的ID取得流程实例对象", returnStr = "getProcessInst($R('processInstId'))")
    public ResultModel<ProcessInst> getProcessInst(String processInstID);


    /**
     * 更新流程实例紧急程度
     *
     * @param urgency 新的紧急程度
     * @return 结果标识
     */
    @MethodChinaName(cname = "更新流程实例紧急程度", returnStr = "updateProcessInstUrgency($R('processInstId'),$R('urgency'))")
    public ResultModel<ReturnType> updateProcessInstUrgency(String processInstId, String urgency);

    /**
     * 将当前用户完成决策的对象转储形成新的实例，本函数完成所有提交后的处理。 ActivityInstWrapper:当前活动实例
     *
     * @param routeBean 权限相关上下文参数，由应用（包含应用客户端和引擎中权 限部分插件间的可交互参数）决定其中参数的具体含义。可能包含用户
     *                  （selectedDealer或selectedReader）的条件参数。
     * @return 结果标识
     */
    @MethodChinaName(cname = "并行发送", returnStr = "mrouteto($RouteBean),display=false")
    public ResultModel<ReturnType>  mrouteto(RouteToBean routeBean);

    @MethodChinaName(cname = "发送", returnStr = "routeto($RouteBean),display=false")
    public ResultModel<ReturnType> routeto(RouteBean routeBean);


//
//    /**
//     * 从指定活动历时实例中新增加一个从指定定义节点出发的实例（主要用于补发等需求）
//     * 1，返回的是最近分裂的主活动对应的实例节点
//     * @param activityHistoryInstId
//     * @return ActivityInst
//
//     */
//    @MethodChinaName(cname="补发",returnStr="getNextRoutes($R('activityInstId')),display=false")
//    public ResultModel<ActivityInst> copyActivityInstByHistory(String activityHistoryInstId) ;


    /**
     * 从指定活动历时实例中新增加一个从指定定义节点出发的实例（主要用于补发等需求）
     * 1，返回的是最近分裂的主活动对应的实例节点
     * 2，从主活动上拷贝实例
     * 3，重置活动状态
     *
     * @param activityHistoryInstId
     * @param isnew
     * @return ActivityInst
     */
    @MethodChinaName(cname = "补发", returnStr = "getNextRoutes($R('activityInstId'),$RL('nextActivityDefIDs'),$R('isnew'))", display = false)
    public ResultModel<ActivityInst> copyActivityInstByHistory(String activityHistoryInstId, Boolean isnew);


    /**
     * 从指定流程实例中新增加一个从指定定义节点出发的实例（主要用于补发等需求）
     *
     * @param processInstId 指定流程实例
     * @param activityDefId 定义出发节点
     * @return
     */
    @MethodChinaName(cname = "从指定流程实例中新增加一个从指定定义节点出发的实例", returnStr = "newActivityInstByActivityDefId($R('processInstId'),$R('activityDefId'))", display = false)
    public ResultModel<ActivityInst> newActivityInstByActivityDefId(String processInstId, String activityDefId);


    /**
     * 抄送处理将当前活动进行复制每人产生一个COPY
     *
     * @param activityHistoryInstId 起始活动历史实例ID
     * @param readers               抄送人员。
     * @return 结果标识
     */
    @MethodChinaName(cname = "抄送处理将当前活动进行复制每人产生一个COPY", returnStr = "copyTo($R('activityHistoryInstId'),$readers)", display = false)
    public ResultModel<ReturnType> copyTo(String activityHistoryInstId,
                                          String[] readers);

    // --------------------------------------------- 收回相关方法

    /**
     * 判断当前活动能否收回
     *
     * @param activityInstID 活动实例ID
     * @return true-可以收回；false-不能收回。
     * ;
     */
    @MethodChinaName(cname = "判断当前活动能否收回", returnStr = "canTakeBack($R('activityInstId'))")
    public ResultModel<Boolean> canTakeBack(String activityInstID);


    /**
     * 活动收回操作
     *
     * @param activityInstID 活动实例ID
     * @return 结果标识
     */
    @MethodChinaName(cname = "活动收回操作", returnStr = "takeBack($R('activityInstId'))", display = false)
    public ResultModel<ReturnType> takeBack(String activityInstID)
    ;


    //----------------------------------------阅闭操作

    /**
     * 判断当前活动能否阅闭
     *
     * @param activityInstID 活动实例ID
     * @return true-可以阅闭；false-不能阅闭。
     */
    @MethodChinaName(cname = "判断当前活动能否阅闭", returnStr = "canEndRead($R('activityInstId'))")
    public ResultModel<Boolean> canEndRead(String activityInstID)
    ;

    /**
     * 活动阅毕操作
     *
     * @param activityInstID 活动实例ID
     * @return 结果标识
     */
    @MethodChinaName(cname = "活动阅毕操作", returnStr = "endRead($R('activityInstId'))", display = false)
    public ResultModel<ReturnType> endRead(String activityInstID)
    ;


    /**
     * 活动结束操作（任务已成功完成）
     *
     * @param activityInstID 活动实例ID
     * @return 结果标识
     */
    @MethodChinaName(cname = "任务完成", returnStr = "endTask($R('activityInstId'))", display = false)
    public ResultModel<ReturnType> endTask(String activityInstID)
    ;

    /**
     * 任务失败
     *
     * @param activityInstID 活动实例ID
     * @return 结果标识
     */
    @MethodChinaName(cname = "任务失败", returnStr = "abortedTask($R('activityInstId'))", display = false)
    public ResultModel<ReturnType> abortedTask(String activityInstID);


    /**
     * 退回操作
     *
     * @param fromActivityInstID      需要执行退回操作的活动实例
     * @param toActivityInstHistoryID 要退回到达的活动实例历史
     * @return 结果标识
     */
    @MethodChinaName(cname = "退回操作", returnStr = "getRouteBackActivityHistoryInstList($R('activityInstId'),$R('activityInstHistoryId'))", display = false)
    public ResultModel<ReturnType> routeBack(String fromActivityInstID,
                                             String toActivityInstHistoryID);

    // --------------------------------------------- 签收相关方法

    /**
     * 能否banli
     *
     * @param activityInstID
     */
    @MethodChinaName(cname = "能否Perform", returnStr = "canPerform($R('activityInstId'))")
    public ResultModel<Boolean> canPerform(String activityInstID)
    ;


    /**
     * 能否签收
     *
     * @param activityInstID
     */
    @MethodChinaName(cname = "能否签收", returnStr = "canSignReceive($R('activityInstId'))")
    public ResultModel<Boolean> canSignReceive(String activityInstID);


    /**
     * 签收操作
     *
     * @param activityInstID
     * @return 结果标识
     */
    @MethodChinaName(cname = "签收操作", returnStr = "signReceive($R('activityInstId')", display = false)
    public ResultModel<ReturnType> signReceive(String activityInstID);

    // --------------------------------------------- 活动状态转换方法


    /**
     * 活动挂起
     *
     * @param activityInstID 活动实例ID
     * @return 结果标识
     */
    @MethodChinaName(cname = "活动挂起", returnStr = "suspendActivityInst($R('activityInstId'))", display = false)
    public ResultModel<ReturnType> suspendActivityInst(String activityInstID);

    /**
     * 继续活动实例
     *
     * @param activityInstID 活动实例ID
     * @return 结果标识
     */
    @MethodChinaName(cname = "继续活动实例", returnStr = "resumeActivityInst($R('activityInstId'))", display = false)
    public ResultModel<ReturnType> resumeActivityInst(String activityInstID);

    // --------------------------------------------- 流程状态转换方法

    /**
     * 流程实例挂起
     *
     * @param processInstID 流程实例ID
     * @return 结果标识
     */
    @MethodChinaName(cname = "流程实例挂起", returnStr = "suspendProcessInst($R('processInstId'))", display = false)
    public ResultModel<ReturnType> suspendProcessInst(String processInstID);

    /**
     * 继续流程实例
     *
     * @param processInstID 流程实例ID
     * @return 结果标识
     */
    @MethodChinaName(cname = "继续流程实例", returnStr = "resumeProcessInst($R('processInstId'))", display = false)
    public ResultModel<ReturnType> resumeProcessInst(String processInstID);

    /**
     * 中止流程实例
     *
     * @param processInstID 流程实例ID
     * @return
     */
    @MethodChinaName(cname = "中止流程实例", returnStr = "abortProcessInst($R('processInstId'))", display = false)
    public ResultModel<ReturnType> abortProcessInst(String processInstID);

    /**
     * 流程实例完成
     *
     * @param processInstID 流程实例ID
     * @return 结果标识
     */
    @MethodChinaName(cname = "流程实例完成", returnStr = "completeProcessInst($R('processInstId'))", display = false)
    public ResultModel<ReturnType> completeProcessInst(String processInstID);

    /**
     * 删除流程实例
     *
     * @param processInstID 流程实例ID
     * @return 结果标识
     */
    @MethodChinaName(cname = "删除流程实例", returnStr = "deleteProcessInst($R('processInstId'))", display = false)
    public ResultModel<ReturnType> deleteProcessInst(String processInstID);

    // --------------------------------------------- 退回相关方法

    /**
     * 开始展示活动表单
     *
     * @param activityInstId 权限相关上下文参数
     * @return
     */
    @MethodChinaName(cname = "开始展示活动表单", returnStr = "display($R('activityInstId')", display = false)
    public ResultModel<ReturnType> display(String activityInstId);


    /**
     * 按照活动实例历史的ID取得活动实例历史对象
     *
     * @param activityInstHistoryID 活动实例历史ID
     * @return 指定活动实例历史ID的ActivityInst对象
     */
    @MethodChinaName(cname = "按照活动实例历史的ID取得活动实例历史对象", returnStr = "getActivityInstHistory($R('activityInstHistoryId'))")
    public ResultModel<ActivityInstHistory> getActivityInstHistory(String activityInstHistoryID);

    ResultModel<Boolean> canRouteBack(String activityInstID);

    ResultModel<RouteInst> getRouteInst(String routeInstId);
}


