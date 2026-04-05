/**
 * $RCSfile: ActivityInstHistory.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:26:08 $
 * <p>
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.client;

import net.ooder.bpm.client.data.DataMap;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.enums.activityinst.ActivityInstDealMethod;
import net.ooder.bpm.enums.activityinst.ActivityInstReceiveMethod;
import net.ooder.bpm.enums.activityinsthistory.ActivityInstHistoryAtt;
import net.ooder.bpm.enums.activityinsthistory.ActivityInstHistoryStatus;
import net.ooder.annotation.MethodChinaName;
import net.ooder.org.Person;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 活动实例历史客户端接口
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 *
 * @author wenzhang li
 * @version 2.1
 */
public interface ActivityInstHistory extends java.io.Serializable {


    /**
     * Getter method for activityhistoryId
     *
     * @return the value of activityhistoryId
     */
    @MethodChinaName(cname = "历史活动实例Id")
    public String getActivityHistoryId();

    /**
     * Getter method for processinstId
     *
     * @return the value of processinstId
     */
    @MethodChinaName(cname = "历史流程实例Id")
    public String getProcessInstId();

    /**
     * Getter method for activitydefId
     *
     * @return the value of activitydefId
     */
    @MethodChinaName(cname = "活动实例定义Id")
    public String getActivityDefId();

    /**
     * Getter method for urgencyType
     *
     * @return the value of urgencyType
     */
    @MethodChinaName(cname = "流程紧急程度")
    public String getUrgency();

    /**
     * Getter method for arrivedTime
     *
     * @return the value of arrivedTime
     */
    @MethodChinaName(cname = "活动到达时间")
    public Date getArrivedTime();

    /**
     * Getter method for limitTime
     *
     * @return the value of limitTime
     */
    @MethodChinaName(cname = "活动定义时间")
    public Date getLimitTime();

    /**
     * Getter method for startTime
     *
     * @return the value of startTime
     */
    @MethodChinaName(cname = "活动开始时间")
    public Date getStartTime();

    /**
     * Getter method for endTime
     *
     * @return the value of endTime
     */
    @MethodChinaName(cname = "活动结束时间")
    public Date getEndTime();

    /**
     * Getter method for recieveState
     *
     * @return the value of recieveState
     */
    @MethodChinaName(cname = "活动定义执行方法")
    public ActivityInstReceiveMethod getReceiveMethod();

    /**
     * Getter method for dealState
     *
     * @return the value of dealState
     */
    @MethodChinaName(cname = "活动定义执行方法")
    public ActivityInstDealMethod getDealMethod();

    /**
     * Getter method for runState
     *
     * @return the value of runState
     */
    @MethodChinaName(cname = "活动状态")
    public ActivityInstHistoryStatus getRunStatus();

    /**
     * 取得当前活动历史所对应的流程实例
     *
     * @return 流程实例
     * @throws BPMException
     *             有异常发生
     */
    @MethodChinaName(cname = "当前活动历史所对应的流程实例")
    public ProcessInst getProcessInst() throws BPMException;

    /**
     * 取得与该活动历史对应的活动实例
     *
     * @return 活动实例
     */
    @MethodChinaName(cname = "与该活动历史对应的活动实例")
    public ActivityInst getActivityInst() throws BPMException;

    /**
     * 取得与该活动历史对应的活动实例Id
     *
     * @return 活动实例
     */
    @MethodChinaName(cname = "与该活动历史对应的活动实例Id")
    public String getActivityInstId();

    /**
     * 取得与该活动历史相对应的活动定义
     *
     * @return 活动定义
     */
    @MethodChinaName(cname = "与该活动历史相对应的活动定义")
    public ActivityDef getActivityDef() throws BPMException;

    /**
     * 取得活动历史中的流程属性值
     *
     * @param name
     *            属性名称
     * @return 属性值
     */
    @MethodChinaName(cname = "取得活动历史中的流程属性值")
    public String getWorkflowAttribute(String name);

    /**
     * 取得活动历史中的权限属性值
     *
     * @param name
     *            属性名称
     * @return 属性值
     */
    @MethodChinaName(cname = "取得活动历史中的权限属性值")
    public List<Person> getRightAttribute(ActivityInstHistoryAtt name);

    /**
     * 取得活动历史中的应用属性值
     *
     * @param name
     *            属性名称
     * @return 属性值
     */

    @MethodChinaName(cname = "取得活动历史中的应用属性值", returnStr = "getAppAttribute($R('attName'))")
    public String getAppAttribute(String name);

    /**
     * 取得活动历史中的定制属性值
     *
     * @param name
     *            属性名称
     * @return 属性值
     */
    @MethodChinaName(cname = "取得活动历史中的定制属性值", returnStr = "getAttribute($R('attName'))", display = false)
    public String getAttribute(String name);

    /**
     * 设置定制属性
     *
     * @param name
     *            属性名称
     * @param value
     *            属性值
     * @throws BPMException
     */
    @MethodChinaName(cname = "设置定制属性", returnStr = "setAttribute($R('attName'),$R('attValue'))", display = false)

    public void setAttribute(String name, String value) throws BPMException;

    /**
     * 取得流程中的个人定制属性值
     * @param personId 用户ID
     * @param name
     *            属性名称
     * @return 属性值
     */
    @MethodChinaName(cname = "个人定制属性值", returnStr = "getAttribute($R('personId'),$R('attName'))", display = true)
    public String getPersonAttribute(String personId, String name);


    /**
     * 设置定制属性
     *
     * @param name
     *            属性名称
     * @param value
     *            属性值
     * @throws BPMException
     */
    @MethodChinaName(cname = "设置个人定制属性", returnStr = "setAttribute($R('personId'),$R('attName'),$R('value'))", display = false)
    public void setPersonAttribute(String personId, String name, String value) throws BPMException;


    /**
     * 获取表单数据
     *
     * @return
     */
    @MethodChinaName(cname = "获取表单数据")
    public DataMap getFormValues() throws BPMException;


    /**
     * 更新表单数据
     *
     * @return
     */
    @MethodChinaName(cname = "更新表单数据")
    public void updateFormValues(DataMap dataMap) throws BPMException;


}
