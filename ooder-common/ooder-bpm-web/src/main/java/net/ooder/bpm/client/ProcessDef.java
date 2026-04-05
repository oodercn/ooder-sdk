/**
 * $RCSfile: ProcessDef.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:26:08 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.client;

import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.enums.process.ProcessDefAccess;
import net.ooder.annotation.MethodChinaName;

import java.util.List;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 流程定义客户端接口
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
public interface ProcessDef extends java.io.Serializable{

    /**
     * 返回流程ID 此属性为流程基本属性，与版本无关，
     * 
     * @return 流程的UUID
     */
    public String getProcessDefId();

    /**
     * 流程定义的名称， 此属性为流程基本属性，与版本无关，
     * 
     * @return 流程定义的名称
     */
    public String getName();

    /**
     * 流程定义的描述， 此属性为流程基本属性，与版本无关，
     * 
     * @return 流程定义的描述
     */
    public String getDescription();

    /**
     * 流程定义的分类， 此属性为流程基本属性，与版本无关，
     * 
     * @return 流程定义的分类
     */
    public String getClassification();

    /**
     * 流程所属的应用系统，如：OA,CMS等
     * 
     * @return 字符串，应用系统的代码，如："SP","CMS"
     */
    public String getSystemCode();

    /**
     * 流程的访问级别，也就对应着流程的类型 此属性为流程基本属性，与版本无关，
     * 
     * @return 两种返回值：
     *         <li>ACCESS_PUBLIC: 可以独立启动
     *         <li>ACCESS_PRIVATE: 不可以独立启动，只能作为Subflow
     */
    @MethodChinaName(cname = "流程的访问级别")
    public ProcessDefAccess getAccessLevel();

    /**
     * 取得指定版本的流程定义
     *
     * @param version
     *            版本号
     * @return 如果不存在此版本，返回null
     */
    @MethodChinaName(cname = "取得指定版本的流程定义")
    public ProcessDefVersion getProcessDefVersion(int version) throws BPMException;

    /**
     * 返回此流程的所有版本的UUID
     * 
     * @return List内保存的是UUID。如果没有则List为空
     */
    @MethodChinaName(cname = "流程的所有版本的UUID")
    public List<String> getAllProcessDefVersionIds()  ;

    /**
     * 返回此流程的所有版本
     * 
     * @return List内保存的是EIProcessDefVersion对象。如果没有则List为空
     */
    @MethodChinaName(cname = "流程的所有版本定义")
    public List<ProcessDefVersion> getAllProcessDefVersions() throws BPMException;

    /**
     * 返回当前激活的版本，如果没有激活的版本，则返回null
     * 
     * @return 如果没有激活的版本，则返回null
     */
    @MethodChinaName(cname = "返回当前激活的版本")
    public ProcessDefVersion getActiveProcessDefVersion() throws BPMException;



}
