/**
 * $RCSfile: GetJDSClient.java,v $
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

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.common.ConfigCode;
import net.ooder.common.ContextType;
import net.ooder.common.JDSConstants;
import net.ooder.common.JDSException;
import net.ooder.common.expression.function.AbstractFunction;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.md5.MD5;
import net.ooder.context.JDSActionContext;
import net.ooder.context.JDSContext;
import net.ooder.engine.ConnectInfo;
import net.ooder.jds.core.esb.util.ActionContext;
import net.ooder.org.OrgManager;
import net.ooder.org.Person;
import net.ooder.org.PersonNotFoundException;
import net.ooder.server.JDSClientService;
import net.ooder.server.JDSServer;
import net.ooder.server.OrgManagerFactory;

@EsbBeanAnnotation(id = "JDSC", name = "获取基础服务", expressionArr = "GetJDSClient()", desc = "获取基础服务", dataType = ContextType.Action)

public class GetJDSClient extends AbstractFunction {
    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, GetJDSClient.class);

    public JDSClientService perform() {
        JDSContext context = JDSActionContext.getActionContext();
        JDSSessionFactory factory = new JDSSessionFactory(context);
        ConnectInfo connectInfo = null;
        ConfigCode configCode = JDSActionContext.getActionContext().getConfigCode();
        if (configCode == null) {
            configCode = ConfigCode.org;
        }
        String sessionId = JDSActionContext.getActionContext().getSessionId();
        String userid = (String) JDSActionContext.getActionContext().getParams(JDSContext.JDSUSERID);
        JDSClientService client = null;
        try {
            client = factory.getClientService(configCode);
            if (client == null && sessionId != null) {
                client = factory.getJDSClientBySessionId(sessionId, configCode);
            }
            if (client != null && client.getConnectInfo() == null) {
                if (userid != null && !userid.equals("")) {
                    connectInfo = this.getConnectInfo(userid);
                } else if (JDSServer.getClusterClient().getUDPClient().getUser() != null) {
                    connectInfo = JDSServer.getInstance().getConnectInfo(client.getSessionHandle());
                }
                if (connectInfo != null) {
                    client.connect(connectInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return client;
    }


    private ConnectInfo getConnectInfo(String personId) throws JDSException {
        ActionContext.getContext().put("JDSUSERID", personId);
        OrgManager orgManager = OrgManagerFactory.getOrgManager();
        Person person = null;
        // 根据用户名密码获取用户
        ConnectInfo connectInfo = null;
        try {
            person = orgManager.getPersonByID(personId);
            connectInfo = new ConnectInfo(person.getID(), person.getAccount(), person.getPassword());
        } catch (PersonNotFoundException e) {
            throw new JDSException("登录失败", JDSException.NOTLOGINEDERROR);
        }

        return connectInfo;
    }

    public static void main(String[] args) {
        System.out.println(MD5.getHashString("123456"));
    }

}
