/**
 * $RCSfile: RouteDef.java,v $
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
import net.ooder.bpm.enums.route.RouteCondition;
import net.ooder.bpm.enums.route.RouteDirction;
import net.ooder.annotation.MethodChinaName;
import net.ooder.annotation.*;
import net.ooder.annotation.ViewType;

import java.util.List;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 路由定义客户端接口
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
@ESDEntity
public interface RouteDef extends java.io.Serializable {


    /**
     * 取得路由定义的UUID
     *
     * @return the value of routedefId
     */
    @MethodChinaName(cname = "UUID")
    @Uid
    public String getRouteDefId();

    /**
     * 取得路由所属的流程定义UUID
     *
     * @return the value of processdefId
     */
    @MethodChinaName(cname = "所属流程定义UUID", display = false)
    @Pid
    public String getProcessDefId();

    /**
     * 取得路由所属的流程定义版本的UUID
     *
     * @return the value of processdefVersionId
     */
    @MethodChinaName(cname = "所属流程定义版本的UUID", display = false)
    @Pid
    public String getProcessDefVersionId();

    /**
     * 取得路由名称
     *
     * @return the value of routename
     */
    @MethodChinaName(cname = "路由名称")
    public String getName();

    /**
     * 取得路由描述
     *
     * @return the value of description
     */
    @MethodChinaName(cname = "路由描述")
    public String getDescription();

    /**
     * 取得路由起始活动节点
     *
     * @return the value of fromactivitydefId
     */
    @MethodChinaName(cname = "起始活动节点")
    public String getFromActivityDefId();

    /**
     * 取得路由到达活动节点
     *
     * @return the value of toactivitydefId
     */
    @MethodChinaName(cname = "到达活动节点")
    public String getToActivityDefId();

    /**
     * 取得路由的顺序
     *
     * @return the value of routeorder
     */
    @MethodChinaName(cname = "路由的顺序", display = false)
    public int getRouteOrder();

    /**
     * 取得路由的方向
     *
     * @return the value of routedirection
     */
    @MethodChinaName(cname = "路由的方向", display = false)
    public RouteDirction getRouteDirection();

    /**
     * 取得路由的条件
     *
     * @return the value of routecondition
     */
    @MethodChinaName(cname = "路由的条件")
    public String getRouteCondition();

    public RouteCondition getRouteConditionType();

    /**
     * 取得工作流扩展属性值
     *
     * @param name
     *            属性名称
     * @return 属性值
     */
    @MethodChinaName(cname = "扩展属性值", returnStr = "getWorkflowAttribute($R('attName'))", display = false)
    public Object getWorkflowAttribute(String name);

    /**
     * 取得权限扩展属性值
     *
     * @param name
     *            属性名称
     * @return 属性值
     */
    @MethodChinaName(cname = "权限扩展属性值", returnStr = "getRightAttribute($R('attName'))", display = false)
    public Object getRightAttribute(String name);

    /**
     * 取得自定义扩展属性值
     *
     * @param name
     * @return
     */
    @MethodChinaName(cname = "自定义扩展属性值", returnStr = "getAttribute($R('attName'))", display = false)
    public String getAttribute(String name);


    /**
     *
     * @param attributetype
     * @param name
     * @return
     */
    @MethodChinaName(cname = "取得应用扩展属性值", returnStr = "getAppAttribute($R('name'))")
    public Object getAttribute(Attributetype attributetype, String name);


    /**
     * 取得扩展属性值
     *
     * @return 属性值
     */
    @MethodChinaName(cname = "取得扩展属性值")
    /*
     * @see net.ooder.bpm.client.RouteDef#getAllAttribute()
     */
    @Ref(ref = RefType.O2M, view = ViewType.GRID)
    public List<AttributeDef> getAllAttribute();


    /**
     * 取得应用扩展属性值
     *
     * @param name
     *            属性名称
     * @return 属性值
     */
    @MethodChinaName(cname = "应用扩展属性值", returnStr = "getAppAttribute($R('attName'))", display = false)
    public Object getAppAttribute(String name);

//	@MethodChinaName(cname = "应用所有属性值",display=false)
//	public List<Attribute> getAllAttribute();
//

    /**
     * 取得当前路由上上注册的所有路由监听器
     *
     * @return 返回值为Listener对象列表
     */
    @MethodChinaName(cname = "取得当前路由上上注册的所有路由监听器", display = false)
    @Ref(ref = RefType.O2M, view = ViewType.GRID)
    public List<Listener> getListeners();


    /**
     * 取得出发节点的活动定义
     *
     * @return 活动定义
     */
    @MethodChinaName(cname = "出发节点的活动定义")
    @Ref(ref = RefType.REF, view = ViewType.DIC)
    public ActivityDef getFromActivityDef() throws BPMException;

    /**
     * 取得到达节点的活动定义
     *
     * @return 活动定义
     */
    @MethodChinaName(cname = "到达节点的活动定义")
    @Ref(ref = RefType.REF, view = ViewType.DIC)
    public ActivityDef getToActivityDef() throws BPMException;

    /**
     * 取得当前活路由定义所使用的流程定义版本
     *
     * @return 流程定义版本
     * @throws BPMException
     *             有异常发生
     */
    @MethodChinaName(cname = "所使用的流程定义版本", display = false)
    @Ref(ref = RefType.M2O, view = ViewType.DIC)
    public ProcessDefVersion getProcessDefVersion() throws BPMException;

    /**
     * 取得与该路由定义相对应的流程定义
     *
     * @return 流程定义
     */
    @MethodChinaName(cname = "所使用的流程定义", display = false)
    @Ref(ref = RefType.M2O, view = ViewType.DIC)
    public ProcessDef getProcessDef() throws BPMException;

    /**
     * 是否指向结束节点
     * @return
     */
    @MethodChinaName(cname = "是否指向结束节点")
    public boolean isToEnd();
}
