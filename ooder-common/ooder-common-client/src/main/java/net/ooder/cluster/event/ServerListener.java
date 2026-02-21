/**
 * $RCSfile: ServerListener.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.cluster.event;

import net.ooder.common.JDSException;

public interface ServerListener extends java.util.EventListener {

    /**
     * 服务器正在启动
     */
    public void serverStarting(ServerEvent event) throws JDSException;

    /**
     * 服务器已启动
     */
    public void serverStarted(ServerEvent event) throws JDSException;

    /**
     * 服务器正在停止
     */
    public void serverStopping(ServerEvent event) throws JDSException, InterruptedException;

    /**
     * 服务器已停止
     */
    public void serverStopped(ServerEvent event) throws JDSException;


    /**
     * 正在保存子系统
     */
    public void systemSaving(ServerEvent event) throws JDSException;

    /**
     * 保存子系统完成
     */
    public void systemSaved(ServerEvent event) throws JDSException;

    /**
     * 正在删除子系统
     */
    public void systemDeleting(ServerEvent event) throws JDSException;


    /**
     * 子系统删除完成
     */
    public void systemDeleted(ServerEvent event) throws JDSException;

    /**
     * 正在激活子系统
     */
    public void systemActivating(ServerEvent event) throws JDSException;


    /**
     * 子系统激活完成
     */
    public void systemActivated(ServerEvent event) throws JDSException;

    /**
     * 正在冻结子系统
     */
    public void systemFreezing(ServerEvent event) throws JDSException;

    /**
     * 冻结子系统完成
     */
    public void systemFreezed(ServerEvent event) throws JDSException;

}
