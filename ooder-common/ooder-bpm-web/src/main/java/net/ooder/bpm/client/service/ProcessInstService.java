package net.ooder.bpm.client.service;

import net.ooder.bpm.client.ActivityInst;
import net.ooder.bpm.client.AttributeInst;
import net.ooder.bpm.client.ProcessInst;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.query.WebRightCondition;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;

import net.ooder.annotation.MethodChinaName;

import java.util.List;

public interface ProcessInstService {

    /**
     * 取得符合条件的流程实例列表
     *
     * @param condition
     *            查询条件，例如根据流程状态，流程类型，流程实例 ID或用户ID进行查询。
     * @return 所有符合条件的ProcessInst列表
     * @throws BPMException
     */
    @MethodChinaName(cname="取得符合条件的流程实例列表",returnStr="getProcessInstList($condition")
    public ListResultModel<List<String>> getProcessInstList(WebRightCondition condition);

    /**
     * 取得符合条件的流程实例列表
     *
     * @param processInstIds
     *            查询条件，例如根据流程状态，流程类型，流程实例 ID或用户ID进行查询。
     * @return 所有符合条件的ProcessInst列表
     * @throws BPMException
     */
    @MethodChinaName(cname="取得符合条件的流程实例列表",returnStr="getProcessInstList(processInstIds)")
    public ListResultModel<List<ProcessInst>> loadProcessInstLists(String[] processInstIds);



    /**
     * 取得符合条件的所有活动实例。
     *
     * @param condition
     *            查询条件，例如根据活动状态(待办或在办的 活动)或所属流程实例进行查询。
     * @return 所有符合条件的ActivityInst列表
     * @throws BPMException
     */
    @MethodChinaName(cname="取得符合条件的所有活动实例",returnStr="getActivityInstList($condition)")
    public ListResultModel<List<String>> getActivityInstList(WebRightCondition condition) ;




    /**
     * 取得符合条件的所有活动实例。
     *
     * @param activityInstIds
     * @return 所有符合条件的ActivityInst列表
     * @throws BPMException
     */
    @MethodChinaName(cname="取得符合条件的所有活动实例",returnStr="loadActivityInstList(activityInstIds)")
    public ListResultModel<List<ActivityInst>> loadActivityInstList(String[] activityInstIds) ;




    /**
     * 取得从指定历史分裂出的活动实例
     *
     * @param activityInstHistoryId
     *            活动实例历史ID
     * @return List<ActivityInst>
     * @throws BPMException
     */
    public ListResultModel<List<String>> getActivityInstListByOutActvityInstHistory(String activityInstHistoryId) ;

    /**
     * 取得某个活动实例的所有可提交路由的列表
     *
     * @param startActivityInstID
     *            起始活动实例ID

     * @return 路由实例的列表
     * @throws BPMException
     */
    @MethodChinaName(cname="取得某个活动实例的所有可提交路由的列表",returnStr="getNextRoutes($R('activityInstId')")
    public ListResultModel<List<String>> getNextRoutes(String startActivityInstID) ;




    /**
     * 流程实例相关的属性
     *
     * @param processInst 流程实例版本的ID
     * @return 属性值
     * @throws BPMException
     */
    @MethodChinaName(cname = "流程实例相关的属性", returnStr = "loadProcessInstArrtibuteIds($R('processInst')")
    public ListResultModel<List<AttributeInst>> loadProcessInstArrtibutes(String processInst);





    /**
     * 活动实例相关的属性
     *
     * @param activityInstId 活动实例相关的属性
     * @return 属性值
     */
    @MethodChinaName(cname = "活动实例相关的属性", returnStr = "loadActivityInstArrtibuteIds($R('activityInstId')")
    public ListResultModel<List<AttributeInst>> loadActivityInstArrtibutes(String activityInstId);


    /**
     * 设置活动实例扩展属性
     * @param activityInstId
     * @param name
     * @param value
     */
    public ResultModel<Boolean> setActivityInstAttribute(String activityInstId, String name, String value);

    /**
     * 设置流程实例扩展属性
     * @param processInstId
     * @param name
     * @param value
     */
    public ResultModel<Boolean> setProcessInstAttribute(String processInstId, String name, String value);

}
