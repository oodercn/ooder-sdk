/**
 * $RCSfile: ActivityInst.java,v $
 * $Revision: 1.2 $
 * $Date: 2016/01/23 16:29:55 $
 * <p>
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.client;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.bpm.client.data.DataMap;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.enums.activityinst.*;
import net.ooder.common.CommonYesNoEnum;
import net.ooder.common.ReturnType;
import net.ooder.annotation.MethodChinaName;
import net.ooder.annotation.ViewType;
import net.ooder.org.Person;
import net.ooder.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 活动实例客户端接口
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
@MethodChinaName(cname = "活动实例")
@Aggregation(type = AggregationType.MODULE, sourceClass = ActivityInst.class, rootClass = ActivityInst.class,userSpace = UserSpace.SYS)
public interface ActivityInst extends java.io.Serializable {
    /**
     * 取得活动实例的UUID
     *
     * @return 活动实例的UUID
     */
    @MethodChinaName(cname = "活动实例的UUID")
    @Pid
    public String getActivityInstId();

    /**
     * 活动实例所属的流程实例UUID
     *
     * @return 流程实例UUID
     */
    @MethodChinaName(cname = "活动实例所属的流程实例UUID")
    @Pid
    public String getProcessInstId();

    /**
     * 取得活动实例所在活动定义节点的UUID
     *
     * @return 活动定义的UUID
     */
    @MethodChinaName(cname = "取得活动实例所在活动定义节点的UUID")
    @Pid
    public String getActivityDefId();

    /**
     * 取得活动实例所在的流程定义版本的UUID
     *
     * @return 流程定义版本的UUID
     */
    @MethodChinaName(cname = "取得活动实例所在的流程定义版本的UUID")
    @Pid
    public String getProcessDefId();

    @MethodChinaName(cname = "取得活动实例所在的流程定义版本的流程定义")
    @Ref(ref = RefType.REF, view = ViewType.FORM)
    public ProcessDef getProcessDef() throws BPMException;

    /**
     * 取得当前活动实例的状态
     *
     * @return 状态代码
     * <li> STATE_RUNNING - 活动处理中
     * <li> STATE_NOTSTARTED - 活动未开始处理
     * <li> STATE_SUSPENDED - 活动挂起
     * <li> STATE_COMPLETED - 活动处理完毕
     */
    @MethodChinaName(cname = "取得当前活动实例的状态")
    public ActivityInstStatus getState();

    /**
     * 取得活动的紧急程度
     *
     * @return 紧急程度可以由应用定制
     */
    @MethodChinaName(cname = "取得活动的紧急程度")
    public String getUrgency();

    /**
     * 取得活动到达时间
     *
     * @return 活动到达得时间
     */
    @MethodChinaName(cname = "取得活动到达时间")
    public Date getArrivedTime();

    /**
     * 活动的限制时间，也就是活动到期的时间。
     *
     * @return 活动到期时间
     */
    @MethodChinaName(cname = "活动到期时间")
    public Date getLimitTime();

    /**
     * 也就是活动预警的时间。
     *
     * @return 活动预警时间
     */
    @MethodChinaName(cname = "活动预警时间")
    public Date getAlertTime();


    /**
     * 取得活动开始处理的时间
     *
     * @return 如果未开始，则返回null
     */
    @MethodChinaName(cname = "取得活动开始处理的时间")
    public Date getStartTime();

    /**
     * 取得活动到达方式
     *
     * @return 字符串，取值范围如下：
     * <li> RECEIVEMETHOD_BACK - 退回
     * <li> RECEIVEMETHOD_SEND - 发送
     * <li> RECEIVEMETHOD_SPECIAL - 特送
     */
    @MethodChinaName(cname = "取得活动到达方式")
    public ActivityInstReceiveMethod getReceiveMethod();

    /**
     * 取得当前活动办理状态
     *
     * @return 返回值如下：
     * <li> DEALMETHOD_NORMAL - 正常
     * <li> DEALMETHOD_INSTEAD - 代办
     */
    @MethodChinaName(cname = "取得当前活动办理状态")
    public ActivityInstDealMethod getDealMethod();

    /**
     * 取得运行时间状况
     *
     * @return 返回值如下：
     * <li> RUNSTATUS_NORMAL - 正常
     * <li> RUNSTATUS_DELAY - 延期
     * <li> RUNSTATUS_URGENCY - 催办
     * <li> RUNSTATUS_ALERT - 报警
     */
    @MethodChinaName(cname = "运行时间状况")
    public ActivityInstRunStatus getRunStatus();

    /**
     * 返回是否可以收回
     *
     * @return 返回值如下：
     * <li>CANTAKEBACK_YES - 可以收回
     * <li>CANTAKEBACK_NO - 不能收回
     */
    @MethodChinaName(cname = "是否可以收回")
    public CommonYesNoEnum getCanTakeBack();


    /**
     * 获取表单数据
     *
     * @return
     */
    @MethodChinaName(cname = "获取表单数据")
    @Ref(ref = RefType.REF, view = ViewType.DIC)
    public DataMap getFormValues() throws BPMException;


    /**
     * 更新表单数据
     *
     * @return
     */
    @MethodChinaName(cname = "更新表单数据")
    public void updateFormValues(DataMap dataMap) throws BPMException;

    /*
     * 取得活动中的流程属性值
     *
     * @param name
     *            属性名称
     * @return 属性值
     */
    @JSONField(serialize = false)
    @MethodChinaName(cname = "取得活动中的流程属性值")
    public Object getWorkflowAttribute(String name);


    /*
     * 取得活动中的性值
     *
     * @return AttributeInst
     */
    @JSONField(serialize = false)

    @MethodChinaName(cname = "取得活动中的属性值")
    @Ref(ref = RefType.O2M, view = ViewType.GRID)
    public List<AttributeInst> loadAllAttribute();


    /**
     * 取得活动中的权限属性值
     *
     * @return 属性值
     */
    @JSONField(serialize = false)

    @MethodChinaName(cname = "取得活动中的权限属性值")
    @Ref(ref = RefType.REF, view = ViewType.GRID)
    public List<Person> getRightAttribute(ActivityInstRightAtt group);

    /**
     * 取得活动中的应用属性值
     *
     * @param name 属性名称
     * @return 属性值
     */
    @JSONField(serialize = false)

    @MethodChinaName(cname = "取得活动中的应用属性值", returnStr = "getAppAttribute($R('attName'))")
    @Ref(ref = RefType.O2M, view = ViewType.DIC)
    public Object getAppAttribute(String name);

    /**
     * 取得活动中的定制属性值
     *
     * @param name 属性名称
     * @return 属性值
     */
    @JSONField(serialize = false)
    @MethodChinaName(cname = "取得活动中的定制属性值", returnStr = "getAttribute($R('attName'))", display = false)
    public String getAttribute(String name);

    /**
     * 设置定制属性
     *
     * @param name  属性名称
     * @param value 属性值
     * @throws BPMException
     */
    @MethodChinaName(cname = "设置定制属性", returnStr = "setAttribute($R('attName'),$R('attValue'))", display = false)
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
     * 取得当前活动所对应的流程实例
     *
     * @return 流程实例
     * @throws BPMException 有异常发生
     */
    @JSONField(serialize = false)

    @MethodChinaName(cname = "取得当前活动所对应的流程实例", display = false)
    @Ref(ref = RefType.O2M, view = ViewType.FORM)
    public ProcessInst getProcessInst() throws BPMException;

    /**
     * 取得与该活动实例相对应的流程定义
     *
     * @return 流程定义
     */
    @MethodChinaName(cname = "取得与该活动实例相对应的流程定义", display = false)
    @Ref(ref = RefType.REF, view = ViewType.DIC)
    public ProcessDefVersion getProcessDefVersion() throws BPMException;


    /**
     * 取得与该活动实例相对应的流程定义
     *
     * @return 流程定义
     */
    @MethodChinaName(cname = "取得与该活动实例相对应的流程定义", display = false)
    public String getProcessDefVersionId();


    /**
     * 取得与该活动实例相对应的活动定义
     *
     * @return 活动定义
     */
    @MethodChinaName(cname = "取得与该活动实例相对应的活动定义")
    @Ref(ref = RefType.M2O, view = ViewType.FORM)
    public ActivityDef getActivityDef() throws BPMException;

    /**
     * 取得活动实例的所有可提交路由的列表
     *
     * @return 路由定义的列表
     * @throws BPMException
     */
    @MethodChinaName(cname = "取得活动实例的所有可提交路由的列表")
    @Ref(ref = RefType.O2M, view = ViewType.GRID)
    public List<RouteDef> getNextRoutes() throws BPMException;


    /**
     * 判断当前活动能否收回
     *
     * @return true-可以收回；false-不能收回。
     * @throws BPMException
     */
    @MethodChinaName(cname = "判断当前活动能否收回", returnStr = "isCanTakeBack()")
    public Boolean isCanTakeBack() throws BPMException;


    /**
     * 是否允许特送
     *
     * @return
     * @throws BPMException
     */
    @MethodChinaName(cname = "是否允许特送", returnStr = "isCanSpecialSend()")
    public Boolean isCanSpecialSend() throws BPMException;

    /**
     * 是否可以阅闭
     *
     * @return
     * @throws BPMException
     */
    @MethodChinaName(cname = "是否可以阅闭", returnStr = "isCanEndRead()")
    public Boolean isCanEndRead() throws BPMException;

    /**
     * 阅闭该文件
     *
     * @return
     * @throws BPMException
     */
    @MethodChinaName(cname = "阅闭该文件", returnStr = "endRead()", display = false)
    public ReturnType endRead() throws BPMException;

    /**
     * 是否可以办理
     *
     * @return
     * @throws BPMException
     */
    @MethodChinaName(cname = "是否可以办理", returnStr = "isCanPerform()")
    public Boolean isCanPerform() throws BPMException;

    /**
     * 活动收回操作
     *
     * @return 结果标识
     * @throws BPMException
     */
    @MethodChinaName(cname = "活动收回操作", returnStr = "takeBack()", display = false)
    public ReturnType takeBack() throws BPMException;

    /**
     * 判断某活动能否退回
     *
     * @return true-可以退回；false-不能退回。
     * @throws BPMException
     */
    @MethodChinaName(cname = "判断某活动能否退回", returnStr = "isCanRouteBack()")
    public Boolean isCanRouteBack()
            throws BPMException;

    /**
     * 取得可以退回的所有活动历史的列表
     *
     * @return 活动实例历史的列表
     * @throws BPMException
     */
    @JSONField(serialize = false)

    @MethodChinaName(cname = "取得可以退回的所有活动历史的列表")
    @Ref(ref = RefType.O2M, view = ViewType.GRID)
    public List<ActivityInstHistory> getRouteBackActivityHistoryInstList() throws BPMException;


    /**
     * 退回操作
     *
     * @return 结果标识
     * @throws BPMException
     */
    @MethodChinaName(cname = "退回操作", returnStr = "routeBack($R('toActivityInstHistoryID'))", display = false)
    public ReturnType routeBack(String toActivityInstHistoryID) throws BPMException;

    /**
     * 能否签收
     *
     * @throws BPMException
     */
    @MethodChinaName(cname = "能否签收", returnStr = "isCanSignReceive()")
    public Boolean isCanSignReceive()
            throws BPMException;


    /**
     * 能否结束
     *
     * @throws BPMException
     */
    @MethodChinaName(cname = "是否可以结束流程", returnStr = "isCanCompleteProcessInst()")
    public Boolean isCanCompleteProcessInst()
            throws BPMException;


    /**
     * 能否结束
     *
     * @throws BPMException
     */
    @MethodChinaName(cname = "是否可以强制重新发送", returnStr = "isCanReSend()")
    public Boolean isCanReSend()
            throws BPMException;


    /**
     * 签收操作
     *
     * @return 结果标识
     * @throws BPMException
     */
    @MethodChinaName(cname = "签收操作", returnStr = "signReceive()", display = false)
    public ReturnType signReceive()
            throws BPMException;

    /**
     * 活动挂起
     *
     * @return 结果标识
     * @throws BPMException
     */
    @MethodChinaName(cname = "活动挂起", returnStr = "suspendActivityInst()", display = false)
    public ReturnType suspendActivityInst()
            throws BPMException;

    /**
     * 继续活动实例
     *
     * @return 结果标识
     * @throws BPMException
     */
    @MethodChinaName(cname = "继续活动实例", returnStr = "resumeActivityInst()", display = false)
    public ReturnType resumeActivityInst()
            throws BPMException;

    /**
     * 取得活动的历史数据 根据活动实例，这能列出属于当前活动实例分支上的历史活动节点 按照历史的经过得时间顺序排列
     *
     * @return 活动实例历史（ActivityInstHistory）的列表
     * @throws BPMException
     */
    @JSONField(serialize = false)
    @MethodChinaName(cname = "取得活动的历史数据")
    @Ref(ref = RefType.O2M, view = ViewType.GRID)
    public List<ActivityInstHistory> getActivityInstHistoryListByActvityInst() throws BPMException;


}
