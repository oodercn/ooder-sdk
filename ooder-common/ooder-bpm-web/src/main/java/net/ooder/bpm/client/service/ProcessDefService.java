package net.ooder.bpm.client.service;

import net.ooder.bpm.client.AttributeDef;
import net.ooder.bpm.client.Listener;
import net.ooder.bpm.client.ProcessDef;
import net.ooder.bpm.client.ProcessDefVersion;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.query.BPMCondition;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.annotation.MethodChinaName;

import java.util.List;

public interface ProcessDefService {


    /**
     * 取得符合条件的流程定义列表。
     *
     * @param condition 查询条件，例如根据流程定义的名称进行查询。
      * @return 所有符合条件的ProcessDef列表
     * @throws BPMException
     */
    @MethodChinaName(cname = "取得符合条件的流程定义列表", returnStr = "getProcessDefVersionIdList($condition, $filter)")
    public ListResultModel<List<String>> getProcessDefVersionIdList(BPMCondition condition) ;

    /**
     * 取得符合条件的流程定义列表。
     *
     * @param condition 查询条件，例如根据流程定义的名称进行查询。
      * @return 所有符合条件的ProcessDef列表
     * @throws BPMException
     */
    @MethodChinaName(cname = "取得符合条件的流程定义列表", returnStr = "getProcessDefIdList($condition)")
    public ListResultModel<List<String>> getProcessDefIdList(BPMCondition condition);

    /**
     * 取得符合条件的流程定义列表。
     *
     * @param processDefVersionIds 查询条件，例如根据流程定义的名称进行查询。
     * @return 所有符合条件的ProcessDef列表
     * @throws BPMException
     */
    @MethodChinaName(cname = "取得符合条件的流程定义列表", returnStr = "getProcessDefVersionList(processDefVersionIds)")
    public ListResultModel<List<ProcessDefVersion>> getProcessDefVersionList(String [] processDefVersionIds) ;

    /**
     * 取得符合条件的流程定义列表。
     *
     * @param processDefIds 查询条件，例如根据流程定义的名称进行查询。
     * @return 所有符合条件的ProcessDef列表
     * @throws BPMException
     */
    @MethodChinaName(cname = "取得符合条件的流程定义列表", returnStr = "getProcessDefList(processDefIds)")
    public ListResultModel<List<ProcessDef>> getProcessDefList(String [] processDefIds);


//
//    /**
//     * 流程定义相关的属性
//     *
//     * @param processDefVersionId 流程定义版本的ID
//     * @return 属性值
//     * @throws BPMException
//     */
//    @MethodChinaName(cname = "流程定义相关的属性", returnStr = "loadProcessDefArrtibuteIds($R('processDefVersionId')")
//    public ListResultModel<List<String>> loadProcessDefArrtibuteIds(String processDefVersionId);


    /**
     * 流程定义相关的属性
     *
     * @param processDefVersionId 流程定义版本的ID
     * @return 属性值
     * @throws BPMException
     */
    @MethodChinaName(cname = "流程定义相关的属性", returnStr = "loadProcessArrtibute($R('attributeIds')")
    public ListResultModel<List<AttributeDef>> loadProcessDefArrtibutes(String processDefVersionId);


//
//    /**
//     * 活动定义相关的属性
//     *
//     * @param activityDefId 活动定义相关的属性
//     * @return 属性值
//     */
//    @MethodChinaName(cname = "活动定义相关的属性", returnStr = "loadActivityDefArrtibuteIds($R('activityDefId')")
//    public ListResultModel<List<String>> loadActivityDefArrtibuteIds(String activityDefId);
//


    /**
     * 活动定义相关的属性
     *
     * @param activityDefId 活动定义相关的属性
     * @return 属性值
     */
    @MethodChinaName(cname = "活动定义相关的属性", returnStr = "loadActivityDefArrtibutes(activityDefId)")
    public ListResultModel<List<AttributeDef>> loadActivityDefArrtibutes(String activityDefId);


//
//    /**
//     * 路由定义相关的权限属性
//     *
//     * @param routeDefId 流程定义版本的ID
//     * @return 属性值
//     */
//    @MethodChinaName(cname = "路由定义相关的权限属性", returnStr = "loadRouteDefArrtibute($R('routeDefId')")
//    public ListResultModel<List<String>> loadRouteDefArrtibuteIds(String routeDefId);
//

    /**
     * 路由定义相关的权限属性
     *
     * @param routeDefId 流程定义版本的ID
     * @return 属性值
     */
    @MethodChinaName(cname = "路由定义相关的权限属性", returnStr = "loadRouteDefArrtibutes(routeDefId)")
    public ListResultModel<List<AttributeDef>> loadRouteDefArrtibutes(String routeDefId);

    /**
     *
     * @param activityDefId
     * @return
     */

    /**
     * 活動定义相关的監聽器
     *
     * @param activityDefId 定义版本的ID
     * @return 属性值
     */
    @MethodChinaName(cname = "活動定义相关的監聽器", returnStr = "getActivityListeners($R('activityDefId')")
    ListResultModel< List<Listener>> getActivityListeners(String activityDefId);

    /**
     * 取得流程第一个活动对象ID
     *
     * @param processDefVersionId
     * @return
     */
    @MethodChinaName(cname = "取得流程第一个活动对象ID", returnStr = "getFirstActivityDefInProcess($R('processDefVersionId')")
    public ResultModel<String> getFirstActivityDefInProcess(String processDefVersionId);
}
