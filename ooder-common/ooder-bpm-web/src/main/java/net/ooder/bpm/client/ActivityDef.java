/**
 * $RCSfile: ActivityDef.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:26:08 $
 * <p>
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.client;

import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.enums.activitydef.*;
import net.ooder.common.CommonYesNoEnum;
import net.ooder.config.ActivityDefImpl;
import net.ooder.annotation.MethodChinaName;
import net.ooder.annotation.*;
import net.ooder.annotation.ViewType;

import java.util.List;


/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 活动定义客户端接口
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
public interface ActivityDef extends java.io.Serializable {


    /**
     * 活动所属流程UUID
     *
     * @return
     */
    @MethodChinaName(cname = "活动所属流程UUID")
    @Pid
    public String getProcessDefId();

    /**
     * 活动所属流程版本UUID
     *
     * @return
     */
    @MethodChinaName(cname = "活动所属流程版本UUID")
    @Pid
    public String getProcessDefVersionId();

    /**
     * 活动UUID
     *
     * @return
     */
    @MethodChinaName(cname = "活动UUID")
    @Uid
    public String getActivityDefId();

    /**
     * 活动名称
     *
     * @return
     */
    @MethodChinaName(cname = "活动名称")
    public String getName();

    /**
     * 活动描述
     *
     * @return
     */
    @MethodChinaName(cname = "活动描述")
    public String getDescription();

    /**
     * 活动位置
     *
     * @return <li> POSITION_NORMAL 一般活动
     * <li> POSITION_START 起始活动
     * <li> POSITION_END 结束活动
     */
    @MethodChinaName(cname = "活动位置")
    public ActivityDefPosition getPosition();

    /**
     * 活动实现方式
     *
     * @return <li> IMPL_NO 手动活动
     * <li> IMPL_TOOL 自动活动
     * <li> IMPL_SUBFLOW 子流程活动
     * <li> IMPL_OUTFLOW 跳转流程活动
     */
    @MethodChinaName(cname = "活动实现方式")
    public ActivityDefImpl getImplementation();

    /**
     * 自动活动实现类
     *
     * @return
     */
    @MethodChinaName(cname = "自动活动实现类")
    public String getExecClass();

    /**
     * 是否等待
     *
     * @return
     * @throws BPMException
     */
    @MethodChinaName(cname = "是否等待")
    public CommonYesNoEnum getIswaitreturn() throws BPMException;

    /**
     * 返回子流程
     *
     * @return
     * @throws BPMException
     */
    @MethodChinaName(cname = "返回子流程")
    @Ref(ref = RefType.M2O,view = ViewType.DIC)
    public ProcessDefVersion getSubFlow() throws BPMException;


    public String getSubFlowId();

    /**
     * 活动时间限制，单位由DurationUnit决定
     *
     * @return
     */
    @MethodChinaName(cname = "活动时间限制")
    public int getLimit();

    /**
     * 活动报警时间，单位由DurationUnit决定
     *
     * @return
     */
    @MethodChinaName(cname = "活动报警时间，单位由DurationUnit决定")
    public int getAlertTime();

    /**
     * 时间单位，如果没有设定则使用流程定义
     *
     * @return
     */
    @MethodChinaName(cname = "时间单位")
    public DurationUnit getDurationUnit();

    /**
     * 到达时间限制后的操作
     *
     * @return <li> DEADLINEOPERATION_DEFAULT 默认处理
     * <li> DEADLINEOPERATION_DELAY 延期办理
     * <li> DEADLINEOPERATION_TAKEBACK 自动收回
     * <li> DEADLINEOPERATION_SURROGATE 代办人自动接收
     */
    @MethodChinaName(cname = "到达时间限制后的操作")
    public ActivityDefDeadLineOperation getDeadlineOperation();

    /**
     * 是否可以退回
     *
     * @return
     */
    @MethodChinaName(cname = "是否可以退回")
    public CommonYesNoEnum getCanRouteBack();

    /**
     * 如果可以退回，退回的方法
     *
     * @return <li> ROUTEBACKMETHOD_LAST 退回上一步
     * <li> ROUTEBACKMETHOD_ANY 退回前面经过得任意一步
     * <li> ROUTEBACKMETHOD_SPECIFY 退回到指定的活动节点上
     */
    @MethodChinaName(cname = "如果可以退回，退回的方法")
    public ActivityDefRouteBackMethod getRouteBackMethod();

    /**
     * 是否可以特送
     *
     * @return
     */
    @MethodChinaName(cname = "是否可以特送")
    public CommonYesNoEnum getCanSpecialSend();


    /**
     * 是否可以补发
     *
     * @return
     */
    @MethodChinaName(cname = "是否可以补发")
    public CommonYesNoEnum getCanReSend();

    /**
     * 到达此活动节点路由的处理方法
     *
     * @return
     */
    @MethodChinaName(cname = "到达此活动节点路由的处理方法")
    public ActivityDefJoin getJoin();

    /**
     * 从此活动节点出发的路由
     *
     * @return
     */
    @MethodChinaName(cname = "从此活动节点出发的路由")
    public ActivityDefSplit getSplit();

    /**
     * 从此活动节点出发的所有路由
     *
     * @return
     */
    @MethodChinaName(cname = "从此活动节点出发的所有路由Id")
    public List<String> getOutRouteIds() throws BPMException;

    /**
     * 进入从此活动节点的所有路由
     * e
     *
     * @return
     */
    @MethodChinaName(cname = "进入从此活动节点的所有路由id")
    public List<String> getInRouteIds() throws BPMException;

    /**
     * 取得扩展属性值
     *
     * @return 属性值
     */
    @MethodChinaName(cname = "取得扩展属性值")
    /*
     * @see net.ooder.bpm.client.ActivityDef#getAllAttribute()
     */
    @Ref(ref = RefType.O2M,view = ViewType.GRID)
    public List<AttributeDef> getAllAttribute();


    /**
     * 取得工作流扩展属性值
     *
     * @param name 属性名称
     * @return 属性值
     */
    @MethodChinaName(cname = "取得工作流扩展属性值")
    public String getWorkflowAttribute(String name);

    /**
     * 取得权限扩展属性值
     *
     * @return 属性值
     */
    @MethodChinaName(cname = "取得权限扩展属性值")
    @Ref(ref = RefType.O2O,view = ViewType.GRID)
    public ActivityDefRight getRightAttribute();

    /**
     * 取得应用扩展属性值
     *
     * @param name 属性名称
     * @return 属性值
     */
    @MethodChinaName(cname = "取得应用扩展属性值", returnStr = "getAppAttribute($R('name'))")

    public Object getAppAttribute(String name);

    /**
     *
     * @param attributetype
     * @param name
     * @return
     */
    @MethodChinaName(cname = "取得应用扩展属性值", returnStr = "getAppAttribute($R('name'))")
    public Object getAttribute(Attributetype attributetype, String name);
    /**
     * 取得当前活动上注册的所有活动监听器
     *
     * @return 返回值为Listener对象列表
     */
    @MethodChinaName(cname = "取得当前活动上注册的所有活动监听器", display = false)
    @Ref(ref = RefType.O2M,view = ViewType.GALLERY)
    public List<Listener> getListeners();


    /**
     * 当前活动所属的流程版本对象。
     *
     * @return 返回流程版本对象
     * @throws BPMException
     */
    @MethodChinaName(cname = "当前活动所属的流程版本对象")
    @Ref(ref = RefType.M2O,view = ViewType.DIC)
    public ProcessDefVersion getProcessDefVersion() throws BPMException;

    /**
     * 取得与该活动定义相对应的流程定义
     *
     * @return 返回流程定义对象
     * @throws BPMException
     */
    @MethodChinaName(cname = "取得与该活动定义相对应的流程定义")
    @Ref(ref = RefType.M2O,view = ViewType.DIC)
    public ProcessDef getProcessDef() throws BPMException;

    /**
     * 取得自定义扩展属性值
     *
     * @param name
     * @return
     */
    @MethodChinaName(cname = "取得自定义扩展属性值", returnStr = "getAttribute($R('attName'))")
    public String getAttribute(String name);


}
