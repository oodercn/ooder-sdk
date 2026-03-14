package net.ooder.agent.client.context;

import net.ooder.agent.client.iot.HomeException;
import  net.ooder.client.JDSSessionFactory;
import  net.ooder.common.ConfigCode;
import  net.ooder.common.JDSConstants;
import  net.ooder.common.JDSException;
import  net.ooder.common.expression.function.AbstractFunction;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.config.UserBean;
import net.ooder.context.JDSActionContext;
import net.ooder.context.JDSContext;
import  net.ooder.engine.ConnectInfo;
import  net.ooder.engine.JDSSessionHandle;
import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.org.OrgManager;
import  net.ooder.org.Person;
import  net.ooder.org.PersonNotFoundException;
import  net.ooder.server.JDSClientService;
import  net.ooder.server.JDSServer;
import  net.ooder.server.OrgManagerFactory;
import  net.ooder.server.comet.AbstractCometHandle;

@EsbBeanAnnotation(id = "JDSGWC", name = "获取基础服务", expressionArr = "GetJDSGWClient()", desc = "获取基础服务")
public class GetJDSGWClient extends AbstractFunction {
    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, GetJDSGWClient.class);

    public JDSClientService perform() {
        JDSContext context = JDSActionContext.getActionContext();
        JDSSessionFactory factory = new JDSSessionFactory(context);


        // 获取远程参数
        String sessionId = (String) JDSActionContext.getActionContext().getParams("RSESSIONID");

        if (sessionId == null) {
            sessionId = (String) JDSActionContext.getActionContext().getParams("JSESSIONID");
        }

        if (sessionId == null) {
            sessionId = JDSActionContext.getActionContext().getSessionId();
        }

        //配合长链接验证
        String userid = AbstractCometHandle.sessionMapUser.get(sessionId);

        if (userid == null) {
            userid = (String) JDSActionContext.getActionContext().getParams(JDSContext.JDSUSERID);
        }
        logger.info("sessionId==" + sessionId + "     userid=" + userid);

        String systemCode = null;
        Object obj = JDSActionContext.getActionContext().getParams(JDSActionContext.SYSCODE);
        if (obj != null) {
            if (obj.getClass().isArray()) {
                systemCode = ((Object[]) obj)[0].toString();
            } else {
                systemCode = obj.toString();
            }
        }

        if (systemCode == null || systemCode.equals("") || systemCode.equals("null")) {
            systemCode = JDSActionContext.getActionContext().getSystemCode();
        }
        if (systemCode == null) {
            systemCode = "comet";
        }
        ConfigCode configCode = JDSActionContext.getActionContext().getConfigCode();

        JDSClientService client = null;
        try {
            client = factory.getJDSClientBySessionId(sessionId, configCode);
        } catch (JDSException e) {
            e.printStackTrace();
        }

        if (userid != null && !userid.equals("")) {
            if (client == null || client.getConnectInfo() == null || !client.getConnectInfo().getUserID().equals(userid)) {
                try {
                    JDSSessionHandle sessionHandel = factory.newSessionHandle(sessionId);
                    client = JDSServer.getInstance().newJDSClientService(sessionHandel, configCode);
                } catch (Exception e) {

                }
            }
        }
        if (client != null && client.getConnectInfo() == null) {
            try {
                ConnectInfo connectInfo = JDSServer.getInstance().getConnectInfo(client.getSessionHandle());
                if (connectInfo != null) {
                    client.connect(connectInfo);
                } else if (userid != null && !userid.equals("")) {
                    connectInfo = this.getConnectInfo(userid);
                    client.connect(connectInfo);
                }

            } catch (JDSException e) {
                e.printStackTrace();
            }
        }

        // if (client == null) {
        // sessionId = JDSActionContext.getActionContext().getSessionId();
        // try {
        // client = login(sessionId);
        //
        // } catch (JDSException e) {
        //
        // }
        // }
        if (client != null && client.getConnectInfo() != null) {

            try {
                client.connect(client.getConnectInfo());
            } catch (JDSException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return client;
    }

    private ConnectInfo getConnectInfo(String personId) throws HomeException {
        OrgManager orgManager = OrgManagerFactory.getOrgManager();
        Person person = null;
        // 根据用户名密码获取用户
        ConnectInfo connectInfo = null;
        try {

            person = orgManager.getPersonByID(personId);

            // 封装登录信息
            connectInfo = new ConnectInfo(person.getID(), person.getAccount(), person.getPassword());

        } catch (PersonNotFoundException e) {
            e.printStackTrace();
            throw new HomeException("登录失败！", HomeException.NOTLOGIN);
        }

        return connectInfo;
    }

    private JDSClientService login(String sessionId) throws HomeException {
        OrgManager orgManager = OrgManagerFactory.getOrgManager();
        Person person = null;
        JDSClientService client = null;
        // 根据用户名密码获取用户

        try {

            person = orgManager.getPersonByAccount(sessionId);

            // 封装登录信息
            ConnectInfo connectInfo = new ConnectInfo(person.getID(), person.getAccount(), person.getPassword());
            // 获取session
            JDSSessionFactory factory = new JDSSessionFactory(JDSActionContext.getActionContext());
            JDSSessionHandle sessionHandle = factory.getSessionHandle();
            // 根据用户的ID 获取一个标识码？

            ConfigCode configCode = UserBean.getInstance().getConfigName();
            // 将该标识码添加到session中
            JDSActionContext.getActionContext().getSession().put(JDSActionContext.SYSCODE, UserBean.getInstance().getSystemCode());
            client = factory.newClientService(sessionHandle, configCode);
            client.connect(connectInfo);

        } catch (JDSException e) {
            e.printStackTrace();
            throw new HomeException("登录失败", HomeException.NOTLOGIN);
        } catch (PersonNotFoundException e) {
            e.printStackTrace();
            throw new HomeException("登录失败", HomeException.NOTLOGIN);
        }

        return client;

    }
}
