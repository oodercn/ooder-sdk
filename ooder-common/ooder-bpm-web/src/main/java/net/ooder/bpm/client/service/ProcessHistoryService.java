package net.ooder.bpm.client.service;

import net.ooder.bpm.client.ActivityInstHistory;
import net.ooder.bpm.client.AttributeInst;
import net.ooder.bpm.engine.query.WebRightCondition;
import net.ooder.bpm.enums.activityinsthistory.ActivityInstHistoryAtt;
import net.ooder.common.ReturnType;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;

import net.ooder.annotation.MethodChinaName;

import java.util.List;

public interface ProcessHistoryService {


    /**
     * 取得从指定历史分裂出的活动实例
     *
     * @param activityInstHistoryId
     *            活动实例历史ID
     * @return List<ActivityInst>
     
     */
    public ListResultModel<List<String>> getActivityInstListByOutActvityInstHistory(String activityInstHistoryId) ;
    /**
     * 取得活动的历史数据 根据活动实例，这能列出属于当前活动实例分支上的历史活动节点 按照历史的经过得时间顺序排列
     *
     * @param actvityInstId
     *            活动实例ID
     * @return 活动实例历史（ActivityInstHistory）的列表
     
     */
    @MethodChinaName(cname="取得活动的历史数据 根据活动实例",returnStr="getActivityInstHistoryListByActvityInst($R('actvityInstId'))")
    public ListResultModel<List<String>> getActivityInstHistoryListByActvityInst(String actvityInstId) ;


    /**
     * 取得符合条件的所有活动历史实例。
     *
     * @param condition
     *            查询条件，例如根据历史活动办理状态或所属流程实例进行查询。

     * @return 所有符合条件的ActivityInstHistory列表
     
     */
    @MethodChinaName(cname="取得符合条件的所有历史活动实例",returnStr="getActivityInstHistoryList($condition)")
    public ListResultModel<List<String>> getActivityInstHistoryList(WebRightCondition condition)
            ;


    /**
     * 取得上一步活动的历史数据 根据活动实例，这能列出属于当前活动实例分支上的历史活动节点 按照历史的经过得时间顺序排列
     *
     * @param actvityInstId
     *            活动实例ID
     * @return 活动实例历史（ActivityInstHistory）的列表
     
     */
    @MethodChinaName(cname="取得上一步活动的历史数据 根据活动实例",returnStr="getActivityInstHistoryListByActvityInst($R('actvityInstId')")
    public ListResultModel<List<String>> getLastActivityInstHistoryListByActvityInst(String actvityInstId)  ;




    /**
     * 根据指定历史分裂节点获分裂出去的所有历史
     * @param historyHisroryId
     * @param noSplit//是否包含分裂的节点
     * @return
     
     */
    @MethodChinaName(cname="根据指定历史分裂节点获分裂出去的所有历史",returnStr="getAllOutActivityInstHistoryByActvityInstHistory($R('actvityInstHistoryId'),true)")
    public ListResultModel<List<String>> getAllOutActivityInstHistoryByActvityInstHistory(String historyHisroryId,Boolean noSplit);


    /**
     * 取得活动的历史数据， 根据流程实例，列出所有属于当前流程实例的历史数据， 按照历史的前后顺序排序
     *
     * @param processInstId
     *            流程实例ID
     * @return 活动实例历史（ActivityInstHistory）的列表
     
     */
    @MethodChinaName(cname="取得活动的历史数据， 根据流程实例",returnStr="getActivityInstHistoryListByProcessInst($R('processInstId'))")
    public ListResultModel<List<String>> getActivityInstHistoryListByProcessInst(String processInstId) ;



    // --------------------------------------------- 退回相关方法

    /**
     * 删除历史操作
     *
     * @param activityInstHistoryID
     *            活动实例ID
     * @return 结果标识
     
     */
    @MethodChinaName(cname="删除历史操作操作",returnStr="deleteHistroy($R('activityInstHistoryID'))",display=true)

    public ResultModel<ReturnType> deleteHistory(String activityInstHistoryID) ;

    /**
     * 还原历史操作
     *
     * @param activityInstHistoryID
     *            活动实例ID
     * @return 结果标识
     
     */
    @MethodChinaName(cname="还原操作操作",returnStr="restoreHistory($R('activityInstHistoryID'))",display=true)

    public ResultModel<ReturnType> restoreHistory(String activityInstHistoryID);

    /**
     * 彻底删除历史操作
     *
     * @param activityInstHistoryID
     *            活动实例ID
     * @return 结果标识
     
     */
    @MethodChinaName(cname="彻底删除历史操作",returnStr="clearHistroy($R('activityInstHistoryID'))",display=false)

    public ResultModel<ReturnType> clearHistory(String activityInstHistoryID) ;


    /**
     *
     * @param activityInstHistoryIds
     * @return
     */
    public ListResultModel<List<ActivityInstHistory>> loadHistoryActivityInstHistoryList(String[] activityInstHistoryIds) ;


    /**
     * 活动实例历史的权限属性
     *
     * @param activityInstHistoryId
     *            活动实例历史的ID
     * @return 属性值
     
     */
    @MethodChinaName(cname="活动实例历史的权限属性",returnStr="getActivityInstRightAttribute($R('activityInstHistoryId'), $R('attName'), $CTX)")
    public ListResultModel<List<String>> loadHistoryAttribute( String activityInstHistoryId) ;


    /**
     * 为办理人添加历史节点标记
     * @param activityInstHistoryID
     * @param tagName
     * @return
     
     */
    @MethodChinaName(cname=" 为办理人添加历史节点标记",returnStr="addPersonTagToHistory($R('activityInstHistoryID'),$R('tagName'))",display=false)
    public ResultModel<ReturnType> addPersonTagToHistory(String activityInstHistoryID,ActivityInstHistoryAtt tagName) ;

    /**
     * 删除办理人标记
     * @param activityInstHistoryID
     * @param tagName
     * @return
     
     */
    @MethodChinaName(cname=" 为办理人添加历史节点标记",returnStr="deletePersonTagToHistory($R('activityInstHistoryID'))",display=false)
    public ResultModel<ReturnType> deletePersonTagToHistory(String activityInstHistoryID,ActivityInstHistoryAtt tagName) ;


    /**
     * 相关历史的属性
     *
     * @param historyId); 相关历史的属性
     * @return 属性值ID
     */
    @MethodChinaName(cname = "相关历史的属性", returnStr = "loadActivityInstHistoryArrtibuteIds($R('processInstVersionId')")
    public ListResultModel<List<String>> loadActivityInstHistoryArrtibuteIds(String historyId);



    /**
     * 相关历史的属性
     *
     * @param attributeIds 相关历史的属性
     * @return 属性值
     */
    @MethodChinaName(cname = "相关历史的属性", returnStr = "loadActivityInstHistoryArrtibutes($R('attributeIds')")
    public ListResultModel<List<AttributeInst>> loadActivityInstHistoryArrtibutes(String[] attributeIds);

    /**
     *
     * @param activityInstId
     * @return
     */
    public ListResultModel<List<String>>  getRouteBackActivityHistoryInstList(String activityInstId);

    /**
     *
     * @param activityDefId
     * @param name
     * @param value
     * @return
     */
    public ResultModel<Boolean> setActivityHistoryAttribute(String activityDefId, String name, String value);

}
