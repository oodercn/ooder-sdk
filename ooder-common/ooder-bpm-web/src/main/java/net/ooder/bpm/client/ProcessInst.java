/**
 * $RCSfile: ProcessInst.java,v $
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
import net.ooder.bpm.enums.process.ProcessInstAtt;
import net.ooder.bpm.enums.process.ProcessInstStatus;
import net.ooder.common.ReturnType;
import net.ooder.annotation.MethodChinaName;
import net.ooder.annotation.*;
import net.ooder.annotation.ViewType;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 流程实例客户端接口
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 *
 * @author wenzhang li
 * @version 2.0
 */
@ESDEntity
@MethodChinaName(cname = "流程实例")
@Aggregation(type = AggregationType.MODULE, sourceClass = ProcessInst.class, rootClass = ProcessInst.class,userSpace = UserSpace.SYS)
public interface ProcessInst extends java.io.Serializable {


    /**
     * 取得流程实例UUID
     *
     * @return 流程实例UUID
     */
    @MethodChinaName(cname = "流程实例UUID")
    @Uid
    public String getProcessInstId();

    /**
     * 取得流程定义的UUID
     *
     * @return 流程定义UUID
     */
    @MethodChinaName(cname = "流程定义UUID")
    @Pid
    public String getProcessDefId();

    /**
     * 取得流程定义版本UUID
     *
     * @return 流程定义版本UUID
     */
    @MethodChinaName(cname = "流程定义版本UUID")
    @Pid
    public String getProcessDefVersionId();

    /**
     * 取得流程实例名称
     *
     * @return 流程实例名称
     */
    @MethodChinaName(cname = "流程实例名称")
    public String getName();

    /**
     * 取得紧急程度
     *
     * @return 紧急程度
     */
    @MethodChinaName(cname = "紧急程度")
    public String getUrgency();

    /**
     * 取得流程实例状态，有以下几种取值：
     * <li>ProcessInst.STATE_RUNNING 流程运行中状态
     * <li>ProcessInst.STATE_NOTSTARTED 草稿状态
     * <li>ProcessInst.STATE_SUSPENDED 挂起状态
     * <li>ProcessInst.STATE_ABORTED 流程中止
     * <li>ProcessInst.STATE_COMPLETED 流程结束
     *
     * @return 流程实例状态
     * @see net.ooder.bpm.client.ProcessInst
     */
    @MethodChinaName(cname = "流程实例状态")
    public ProcessInstStatus getState();

    /**
     * 取得流程实例副本数量
     *
     * @return 流程实例副本数量
     */
    @MethodChinaName(cname = "流程实例副本数量")
    public int getCopyNumber();

    /**
     * 取得流程实例启动时间
     *
     * @return 流程实例启动时间
     */
    @MethodChinaName(cname = "程实例启动时间")
    public Date getStartTime();

    /**
     * 取得流程实例办结时间
     *
     * @return 流程实例办结时间
     */
    @MethodChinaName(cname = "流程实例办结时间")
    public Date getEndTime();

    /**
     * 取得流程实例时间限制
     *
     * @return 流程实例时间限制
     */
    @MethodChinaName(cname = " 流程实例时间限制")
    public Date getLimitTime();

    /**
     * 取得流程实例运行状况，有以下几种取值：
     * <li>ProcessInst.NORMAL --> 正常
     * <li>ProcessInst.DELAY --> 延期
     * <li>ProcessInst.URGENCY --> 催办
     * <li>ProcessInst.ALERT --> 报警
     *
     * @return 流程实例运行状况
     * @see net.ooder.bpm.client.ProcessInst
     */
    @MethodChinaName(cname = "流程实例状态")
    public ProcessInstStatus getRunStatus();

    /**
     * 取得当前流程实例所使用的流程定义版本
     *
     * @return 流程定义版本
     * @throws BPMException 有异常发生
     */
    @MethodChinaName(cname = "流程定义版本")
    @Ref(ref = RefType.M2O, view = ViewType.GRID)
    public ProcessDefVersion getProcessDefVersion() throws BPMException;

    /**
     * 取得与该流程实例相对应的流程定义
     *
     * @return 流程定义
     */
    @MethodChinaName(cname = "流程定义")
    @Ref(ref = RefType.M2O, view = ViewType.DIC)
    public ProcessDef getProcessDef() throws BPMException;

    /**
     * 取得当前流程实例对应的所有活动实例
     *
     * @return 活动实例列表
     * @throws BPMException 有异常发生
     */
    @MethodChinaName(cname = "活动实例")
    @Ref(ref = RefType.O2M, view = ViewType.GRID)
    public List<ActivityInst> getActivityInstList() throws BPMException;

    /**
     * 取得流程中的流程属性值
     *
     * @param name 属性名称
     * @return 属性值
     */
    @MethodChinaName(cname = "流程属性值", returnStr = "getWorkflowAttribute($R('attName'))", display = false)
    public Object getWorkflowAttribute(ProcessInstAtt name);

    /**
     * 取得流程中的权限属性值
     *
     * @param name 属性名称
     * @return 属性值
     */
    @MethodChinaName(cname = "权限属性值", returnStr = "getRightAttribute($R('attName'))", display = false)
    public Object getRightAttribute(ProcessInstAtt name);

    /**
     * 取得流程中的应用属性值
     *
     * @param name 属性名称
     * @return 属性值
     */
    @MethodChinaName(cname = "应用属性值", returnStr = "getAppAttribute($R('attName'))", display = false)
    public Object getAppAttribute(ProcessInstAtt name);

    /**
     * 取得流程中的定制属性值
     *
     * @param name 属性名称
     * @return 属性值
     */
    @MethodChinaName(cname = "定制属性值", returnStr = "getAttribute($R('attName'))", display = false)
    public String getAttribute(String name);


    /**
     * 取得流程中的所有属性值
     *
     * @return 属性值
     */
    @MethodChinaName(cname = "取得流程中的所有属性值", returnStr = "getAllAttribute()", display = false)
    @Ref(ref = RefType.O2M, view = ViewType.GRID)
    public List<AttributeInst> getAllAttribute();


    /**
     * 取得流程中的个人定制属性值
     *
     * @param personId 用户ID
     * @param name     属性名称
     * @return 属性值
     */
    @MethodChinaName(cname = "个人定制属性值", returnStr = "getAttribute($R('personId'),$R('attName'))", display = true)
    public String getPersonAttribute(String personId, String name);

    /**
     * 设置定制属性
     *
     * @param name  属性名称
     * @param value 属性值
     * @throws BPMException
     */
    @MethodChinaName(cname = "设置定制属性", returnStr = "setAttribute($R('attName'),$R('value'))", display = false)
    public void setAttribute(String name, String value) throws BPMException;


    /**
     * 设置定制属性
     *
     * @param name  属性名称
     * @param value 属性值
     * @throws BPMException
     */
    @MethodChinaName(cname = "设置个人定制属性", returnStr = "setAttribute($R('personId'),$R('attName'),$R('value'))", display = false)
    public void setPersonAttribute(String personId, String name, String value) throws BPMException;


    /**
     * 更新流程实例名称（公文标题）
     *
     * @return 结果标识
     * @throws BPMException
     */
    @MethodChinaName(cname = "更新流程实例名称（公文标题）", returnStr = "updateProcessInstUrgency($R('processInstName'))")
    public ReturnType updateProcessInstName(String name)
            throws BPMException;

    /**
     * 更新流程实例紧急程度
     *
     * @param urgency 新的紧急程度
     * @return 结果标识
     * @throws BPMException
     */
    @MethodChinaName(cname = "更新流程实例紧急程度", returnStr = "updateProcessInstUrgency($R('urgency'))")
    public ReturnType updateProcessInstUrgency(
            String urgency) throws BPMException;

    /**
     * 流程实例挂起
     *
     * @return 结果标识
     * @throws BPMException
     */
    @MethodChinaName(cname = "流程实例挂起", returnStr = "suspendProcessInst()", display = false)
    public ReturnType suspendProcessInst()
            throws BPMException;


    /**
     * 继续流程实例
     *
     * @return 结果标识
     * @throws BPMException
     */
    @MethodChinaName(cname = "继续流程实例", returnStr = "resumeProcessInst()", display = false)
    public ReturnType resumeProcessInst()
            throws BPMException;

    /**
     * 取得活动的历史数据， 根据流程实例，列出所有属于当前流程实例的历史数据， 按照历史的前后顺序排序
     *
     * @return 活动实例历史（ActivityInstHistory）的列表
     * @throws BPMException
     */
    @MethodChinaName(cname = "取得活动的历史数据， 根据流程实例")
    public List<ActivityInstHistory> getActivityInstHistoryListByProcessInst() throws BPMException;


    /**
     * 中止流程实例
     *
     * @return
     * @throws BPMException
     */
    @MethodChinaName(cname = "中止流程实例", returnStr = "abortProcessInst()", display = false)
    public ReturnType abortProcessInst()
            throws BPMException;

    /**
     * 流程实例完成
     *
     * @return 结果标识
     * @throws BPMException
     */
    @MethodChinaName(cname = "流程实例完成", returnStr = "completeProcessInst()", display = false)
    public ReturnType completeProcessInst()
            throws BPMException;

    /**
     * 删除流程实例
     *
     * @return 结果标识
     * @throws BPMException
     */
    @MethodChinaName(cname = "删除流程实例", returnStr = "deleteProcessInst()", display = false)
    public ReturnType deleteProcessInst()
            throws BPMException;


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
