/**
 * $RCSfile: RouteInst.java,v $
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
import net.ooder.bpm.enums.route.RouteDirction;
import net.ooder.bpm.enums.route.RouteInstType;
import net.ooder.annotation.MethodChinaName;
import net.ooder.annotation.*;
import net.ooder.annotation.ViewType;

import java.util.Date;

@ESDEntity
@Aggregation(type = AggregationType.MODULE, sourceClass = ActivityInst.class, rootClass = ActivityInst.class,userSpace = UserSpace.SYS)
public interface RouteInst extends java.io.Serializable {

    /**
     * 取得当前路由实例的UUID
     *
     * @return 路由实例的UUID
     */
    @MethodChinaName(cname = "路由实例的UUID")
    @Uid
    public String getRouteInstId();

    /**
     * 取得当前路由实例所属的流程实例UUID
     *
     * @return 流程实例UUID
     */
    @MethodChinaName(cname = "流程实例UUID", display = false)
    @Pid
    public String getProcessInstId();

    /**
     * 取得路由的名称，来源于路由定义
     *
     * @return 路由的名称
     */
    @MethodChinaName(cname = "路由的名称")
    public String getRouteName();

    /**
     * 取得路由的描述，来源于路由定义
     *
     * @return 路由的描述
     */
    @MethodChinaName(cname = "路由描述")
    public String getDescription();

    /**
     * 取得路由出发活动实例历史节点UUID
     *
     * @return 实例历史节点UUID
     */
    @MethodChinaName(cname = "历史节点UUID")
    public String getFromActivityId();

    /**
     * 取得路由到达活动实例节点或者活动实例历史节点的UUID<br>
     * 具体的类型由getRouteType()方法返回值确定，如果返回：
     * <li>ROUTETYPE_HISTORY - 表示到达的是一个活动实例历史节点
     * <li>ROUTETYPE_ACTIVITY - 表示到达的是一个活动实例节点
     *
     * @return 活动实例历史或者活动实例UUID
     * @see #getRouteType()
     */
    @MethodChinaName(cname = "路由到达活动实例节点或者活动实例历史节点UUID")
    public String getToActivityId();

    /**
     * 取得路由方向
     *
     * @return 返回值如下：
     * <li>ROUTEDIRECTION_FORWARD - 前进路由
     * <li>ROUTEDIRECTION_BACK - 退回路由
     * <li>ROUTEDIRECTION_SPECIAL - 特送
     */
    @MethodChinaName(cname = "路由方向")
    public RouteDirction getRouteDirection();

    /**
     * 路由实例的类型
     *
     * @return 返回值如下：
     * <li>ROUTETYPE_HISTORY - 从活动实例历史到达活动实例历史
     * <li>ROUTETYPE_ACTIVITY - 从活动实例历史到达活动实例
     */
    @MethodChinaName(cname = "路由实例的类型")
    public RouteInstType getRouteType();

    /**
     * 取得路由发生的时间，也就是活动完成时间
     *
     * @return 路由时间
     */
    @MethodChinaName(cname = "路由发生时间")
    public Date getRouteTime();

    /**
     * 取得与该路由实例相对应的流程实例
     *
     * @return 流程实例
     */
    @MethodChinaName(cname = "流程实例", display = false)
    @Ref(ref = RefType.M2M, view = ViewType.DIC)
    public ProcessInst getProcessInst() throws BPMException;

}