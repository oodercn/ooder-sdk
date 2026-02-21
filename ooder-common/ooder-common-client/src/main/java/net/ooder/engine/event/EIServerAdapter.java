/**
 * $RCSfile: EIServerAdapter.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:26:02 $
 * <p>
 * Copyright (C) 2025 ooder, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of ooder, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: EIServerAdapter.java,v $
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
package net.ooder.engine.event;

import net.ooder.common.JDSException;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 核心服务器事件监听器适配器
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author  ooder
 * @version 2.0
 */
public class EIServerAdapter implements EIServerListener {

    /**
     * 服务器正在启动
     */
    public void serverStarting(EIServerEvent event) throws JDSException {
    }

    /**
     * 服务器已启动
     */
    public void serverStarted(EIServerEvent event) throws JDSException {
    }

    /**
     * 服务器正在停止
     */
    public void serverStopping(EIServerEvent event) throws JDSException, InterruptedException {
    }

    /**
     * 服务器已停止
     */
    public void serverStopped(EIServerEvent event) throws JDSException {
    }


    public void systemActivated(EIServerEvent event) throws JDSException {
        // TODO Auto-generated method stub

    }

    public void systemActivating(EIServerEvent event) throws JDSException {
        // TODO Auto-generated method stub

    }

    public void systemDeleted(EIServerEvent event) throws JDSException {
        // TODO Auto-generated method stub

    }

    public void systemDeleting(EIServerEvent event) throws JDSException {
        // TODO Auto-generated method stub

    }

    public void systemFreezed(EIServerEvent event) throws JDSException {
        // TODO Auto-generated method stub

    }

    public void systemFreezing(EIServerEvent event) throws JDSException {
        // TODO Auto-generated method stub

    }

    public void systemSaved(EIServerEvent event) throws JDSException {
        // TODO Auto-generated method stub

    }

    public void systemSaving(EIServerEvent event) throws JDSException {
        // TODO Auto-generated method stub

    }


}
