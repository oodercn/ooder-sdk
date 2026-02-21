/**
 * $RCSfile: JDSSessionFactory.java,v $
 * $Revision: 1.11 $
 * $Date: 2017/02/12 12:47:30 $
 * <p>
 * Copyright (C) 2025 ooder, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of ooder, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: JDSSessionFactory.java,v $
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
package net.ooder.client;

import net.ooder.common.ConfigCode;
import net.ooder.common.JDSException;
import net.ooder.common.cache.Cache;
import net.ooder.context.JDSActionContext;
import net.ooder.context.JDSContext;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.server.JDSClientService;
import net.ooder.server.JDSServer;

import java.util.Map;
import java.util.UUID;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: JDS系统会话工厂类，用于获取客户端接口实现。应用程序需要通过该类获取与引擎交互的客户端接口实现类。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author wenzhang li
 * @version 4.0
 */
public class JDSSessionFactory {
    // 分布式CACHE

    private JDSContext context;

    public JDSSessionFactory(JDSContext context) {
        if (context == null) {
            context = JDSActionContext.getActionContext();
        }

        this.context = context;
    }

    /**
     * 根据request参数取得SessionHandle
     *
     * @return
     */
    public JDSSessionHandle getSessionHandle() {
        Map session = context.getSession();
        String sessionId = context.getSessionId();
        Cache<String, JDSSessionHandle> sessionHandleCache = null;
        try {
            sessionHandleCache = JDSServer.getInstance().getSessionHandleCache();
        } catch (JDSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JDSSessionHandle sessionHandle = sessionHandleCache.get(sessionId);
        if (sessionHandle == null) {
            if (session.get("sessionHandle") != null) {
                sessionHandle = (JDSSessionHandle) session.get("sessionHandle");
            } else {
                sessionHandle = newSessionHandle();
                session.put("sessionHandle", sessionHandle);
            }
            sessionHandleCache.put(sessionId, sessionHandle);

        }
        return sessionHandle;
    }

    /**
     * 创建一个SessionHandle
     *
     * @return
     */
    private static JDSSessionHandle newSessionHandle() {
        String sessionId = UUID.randomUUID().toString();
        Cache sessionHandleCache = null;
        try {
            sessionHandleCache = JDSServer.getInstance().getSessionHandleCache();
        } catch (JDSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JDSSessionHandle handle = new JDSSessionHandle(sessionId);
        sessionHandleCache.put(sessionId, handle);
        return handle;
    }

    /**
     * 创建一个SessionHandle
     *
     * @return
     */
    public static JDSSessionHandle newSessionHandle(String sessionId) {

        Cache<String, JDSSessionHandle> sessionHandleCache = null;
        try {
            sessionHandleCache = JDSServer.getInstance().getSessionHandleCache();
        } catch (JDSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JDSSessionHandle handle = sessionHandleCache.get(sessionId);
        if (handle == null) {
            handle = new JDSSessionHandle(sessionId);
            sessionHandleCache.put(sessionId, handle);
        }
        return handle;
    }

    /**
     * 创建一个SessionHandle
     *
     * @return
     */
    public JDSSessionHandle createSessionHandle() {
        JDSSessionHandle sessionHandle = newSessionHandle();

        Cache<String, JDSSessionHandle> sessionHandleCache = null;
        try {
            sessionHandleCache = JDSServer.getInstance().getSessionHandleCache();
        } catch (JDSException e) {

            e.printStackTrace();
        }
        Map session = context.getSession();
        String sessionId = context.getSessionId();
        session.put("sessionHandle", sessionHandle);
        sessionHandleCache.put(sessionId, sessionHandle);
        return sessionHandle;
    }

    /**
     * 创建一个SessionHandle
     *
     * @return
     */
    public JDSSessionHandle getSessionHandleBySessionId(String sessionId) {

        Cache<String, JDSSessionHandle> sessionHandleCache = null;
        try {
            sessionHandleCache = JDSServer.getInstance().getSessionHandleCache();
        } catch (JDSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JDSSessionHandle sessionHandle =  sessionHandleCache.get(sessionId);
        return sessionHandle;
    }

    /**
     * 创建一个SessionHandle
     *
     * @return
     * @throws JDSException
     */
    public JDSClientService getJDSClientBySessionId(String sessionId, ConfigCode configCode) throws JDSException {
        JDSSessionHandle sessionHandle = getSessionHandleBySessionId(sessionId);
        JDSClientService client = null;
        if (sessionHandle != null) {
            client = this.getClientService(sessionHandle, configCode);
        }

        return client;
    }

    /**
     * 获取客户端接口实现类的方法。
     *
     * @return 客户接口实现
     * @throws JDSException
     */
    public JDSClientService newClientService(JDSSessionHandle sessionHandle, ConfigCode configCode) throws JDSException {
        JDSClientService client = JDSServer.getInstance().newJDSClientService(sessionHandle, configCode);

        return client;
    }

    /**
     * 根据会话信息及应用信息获取已有客户端接口
     *
     * @param sessionHandle 会话处理信息
     * @return
     * @throws JDSException
     */
    private JDSClientService getClientService(JDSSessionHandle sessionHandle, ConfigCode configCode) throws JDSException {
        JDSClientService service = JDSServer.getInstance().getJDSClientService(sessionHandle, configCode);
        return service;
    }

    /**
     * 根据request请求信息及应用信息获取已有客户端接口
     *
     * @return
     * @throws JDSException
     */
    public JDSClientService getClientService(ConfigCode configCode) throws JDSException {
        JDSClientService client = null;
        if (context == null) {
            context = JDSActionContext.getActionContext();
        }

        if (configCode == null) {

            configCode = context.getConfigCode();

        }
        JDSSessionHandle sessionHandle = getSessionHandle();
        if (sessionHandle == null) {
            throw new JDSException("Session not available!");
        }
        client = JDSServer.getInstance().getJDSClientService(sessionHandle, configCode);

        return client;
    }

    // /**
    // * 根据客户端接口取得管理端的接口
    // *
    // * @param client
    // * @return
    // * @throws JDSException
    // */
    // public AdminService getAdminService(JDSClientService client)
    // throws JDSException {
    // AdminService aclient=
    // aclient= JDSServer.getInstance().getAdminService(client);
    //
    // return aclient;
    // }

}
