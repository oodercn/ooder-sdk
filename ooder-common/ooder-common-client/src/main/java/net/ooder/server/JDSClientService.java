/**
 * $RCSfile: JDSClientService.java,v $
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
package net.ooder.server;

import net.ooder.annotation.MethodChinaName;
import net.ooder.common.ConfigCode;
import net.ooder.common.JDSException;
import net.ooder.common.ReturnType;
import net.ooder.context.JDSContext;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.ConnectionHandle;
import net.ooder.engine.JDSSessionHandle;

public interface JDSClientService {
    // --------------------------------------------- 登陆注销操作




    /**
     * 取得系统标识
     */
    @MethodChinaName(cname = "取得系统", display = false)
    public ConfigCode getConfigCode();


    /**
     * 取得SessionHandle
     *
     * @return
     */
    @MethodChinaName(cname = "取得SessionHandle", display = false)
    public JDSSessionHandle getSessionHandle();

    /**
     * 登陆
     *
     * @param connInfo 登陆连接信息
     * @throws JDSException
     */
    @MethodChinaName(cname = "登陆", returnStr = "connect($connInfo)", display = false)
    public void connect(ConnectInfo connInfo) throws JDSException;


    /**
     * 注销
     *
     * @return 结果标识
     * @throws JDSException
     */
    @MethodChinaName(cname = "注销", returnStr = "disconnect()", display = false)
    public ReturnType disconnect() throws JDSException;

    /**
     * 取得登录人信息
     *
     * @return
     */
    @MethodChinaName(cname = "取得登录人信息")
    public ConnectInfo getConnectInfo();


    /**
     * 获得UDP控制器
     *
     * @return
     */
    @MethodChinaName(cname = "获取长联接控制器")
    public ConnectionHandle getConnectionHandle();

    /**
     * 设定UDP控制器
     *
     * @return
     */
    @MethodChinaName(cname = "设定长联接控制器")
    public void setConnectionHandle(ConnectionHandle handle);

//		
//		/**
//		 * 取得资源管理器
//		 * 
//		 * @return OrgManager
//		 */
//		@MethodChinaName(cname="取得资源管理器")
//		public OrgManager getOrgManager();
//		
//		/**
//		 * 设定资源管理器
//		 * 
//		 * @return OrgManager
//		 */
//		@MethodChinaName(cname="设定资源管理器")
//		public  void setOrgManager(OrgManager orgManager);
//		


    /**
     * 取得当前环境
     *
     * @return JDSContext
     */
    @MethodChinaName(cname = "取得当前环境")
    public JDSContext getContext();


    /**
     * 设定当前环境
     */
    @MethodChinaName(cname = "设定当前环境")
    public void setContext(JDSContext context);

    public String getSystemCode();
}
