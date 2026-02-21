/**
 * $RCSfile: EventControl.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:26:02 $
 * <p>
 * Copyright (C) 2025 ooder, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of ooder, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: EventControl.java,v $
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

import net.ooder.common.JDSConstants;
import net.ooder.common.JDSEvent;
import net.ooder.common.JDSException;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.ClassUtility;
import net.ooder.config.JDSConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: JDSServer系统事件控制核心，所有引擎事件都在这里中转处理
 * </p>
 * <p>,
 * Copyright: Copyright (c) 2025
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author wenzhang li
 * @version 1.0
 */
public class EventControl implements JDSEventDispatcher {

    private static final Log logger = LogFactory.getLog(
            JDSConstants.CONFIG_KEY, EventControl.class);

    // singleton instance
    private static EventControl instance = null;

    // 核心服务器事件监听器列表
    public List<EIServerListener> coreServerEventListeners = new ArrayList<EIServerListener>();


    public static EventControl getInstance() {
        if (instance == null) {
            synchronized (EventControl.class) {
                if (instance == null) {
                    instance = new EventControl();
                }
            }
        }
        return instance;
    }

    protected EventControl() {
        // 初始化核心事件监听器
        initCoreListeners();
    }



    /**
     * 分发核心服务器事件
     *
     * @param event
     *            核心服务器事件
     */
    private void dispatchCoreServerEvent(final EIServerEvent event)
            throws JDSException {
        EIServerEvent se = event;
        for (Iterator it = coreServerEventListeners.iterator(); it.hasNext(); ) {
            try {
                EIServerListener listener = (EIServerListener) it.next();
                switch (se.getID()) {
                    case serverStarting:
                        // 服务器正在启动
                        listener.serverStarting(se);
                        break;
                    case serverStarted:
                        // 服务器已启动
                        listener.serverStarted(se);
                        break;
                    case serverStopping:
                        // 服务器正在停止
                        listener.serverStopping(se);
                        break;
                    case serverStopped:
                        // 服务器已停止
                        listener.serverStopped(se);
                        break;

                    case systemSaving:
                        // 开始创建子系统
                        listener.systemSaving(se);
                        break;
                    case systemSaved:
                        // 子系统创建完成
                        listener.systemSaved(se);
                        break;
                    case systemDeleting:
                        // 开始删除子系统
                        listener.systemDeleting(se);
                        break;
                    case systemDeleted:
                        // 子系统删除完成
                        listener.systemDeleted(se);
                        break;
                    case systemActivating:
                        // 开始激活子系统
                        listener.systemActivating(se);
                        break;
                    case systemActivated:
                        // 子系统激活完成
                        listener.systemActivated(se);
                        break;

                    case systemFreezing:
                        // 开始冻结子系统
                        listener.systemFreezing(se);
                        break;
                    case systemFreezed:
                        // 冻结子系统完成
                        listener.systemFreezed(se);
                        break;

                    default:
                        throw new JDSException("Unsupport server event type: "
                                + se.getID(),
                                JDSException.UNSUPPORTSERVEREVENTERROR);
                }
            } catch (Throwable e) {
                logger.warn("Listener execute failed!", e);
            }
        }
    }

    protected void initCoreListeners() {
        String[] serverListeners = JDSConfig
                .getValues("event.ServerEventListeners.listener");


        String listener;
        // 装载服务器事件监听器
        if (serverListeners != null)
            for (int i = 0; i < serverListeners.length; i++) {
                listener = serverListeners[i];
                try {
                    EIServerListener serverListener = (EIServerListener) ClassUtility
                            .loadClass(listener).newInstance();
                    coreServerEventListeners.add(serverListener);
                } catch (Exception e) {
                    logger.error("", e);
                }
            }


        coreServerEventListeners = Collections .unmodifiableList(coreServerEventListeners);

    }


    /**
     * 事件分发方法，所有的SERVER事件都通过该方法进行分发
     *
     * @param event
     *            SERVER事件
     */
    public void dispatchEvent(JDSEvent event) throws JDSException {
        if (event != null) {
            if (event instanceof EIServerEvent) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dispatchCoreServerEvent((EIServerEvent) event);
                        } catch (JDSException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        }
    }


}


