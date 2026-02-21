package net.ooder.agent.client.iot.client.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.ooder.agent.client.command.Command;
import net.ooder.agent.client.command.CommandFactory;
import net.ooder.agent.client.command.task.SersorReportCommandTask;
import net.ooder.agent.client.command.task.SyncTimeCommandTask;
import net.ooder.agent.client.iot.Device;
import net.ooder.agent.client.iot.HomeException;
import net.ooder.agent.client.iot.client.GWAccountWebService;
import net.ooder.agent.client.iot.ct.CtIotCacheManager;
import net.ooder.agent.client.iot.enums.DeviceEventEnums;
import net.ooder.agent.client.iot.json.device.GWUser;
import net.ooder.agent.client.iot.json.device.Gateway;
import net.ooder.agent.client.iot.json.device.GatewayErrorReport;
import  net.ooder.client.JDSSessionFactory;
import net.ooder.cluster.ServerNode;
import  net.ooder.common.ConfigCode;
import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.common.JDSConstants;
import  net.ooder.common.JDSException;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.config.ErrorResultModel;
import  net.ooder.config.ResultModel;
import  net.ooder.context.JDSActionContext;
import  net.ooder.engine.ConnectInfo;
import  net.ooder.engine.JDSSessionHandle;
import  net.ooder.agent.client.home.client.GWClient;
import  net.ooder.agent.client.home.engine.HomeEventControl;
import  net.ooder.agent.client.home.engine.HomeServer;
import  net.ooder.agent.client.home.event.DeviceEvent;
import  net.ooder.jds.core.User;
import  net.ooder.org.OrgManager;
import  net.ooder.org.Person;
import  net.ooder.org.PersonNotFoundException;
import  net.ooder.server.JDSClientService;
import  net.ooder.server.JDSServer;
import  net.ooder.server.OrgManagerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@EsbBeanAnnotation(id = "GWAccountWebService", name = "网关认证服务", expressionArr = "GWAccountWebServiceImpl()", desc = "网关认证服务")
public class GWAccountWebServiceImpl implements GWAccountWebService {

    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, GWAccountWebServiceImpl.class);


    public ResultModel<Gateway> register(Gateway gateway) {
        ResultModel<Gateway> userStatusInfo = new ResultModel<Gateway>();
        try {
            String factoryName = gateway.getFactory();
            String ieee = gateway.getSerialno();
            CtIotCacheManager appEngine = HomeServer.getAppEngine();
            Device device = appEngine.registerGateway(gateway.getDeviceId(), ieee, gateway.getMacno(), factoryName, gateway.getVersion());

            if (device == null) {
                throw new HomeException("设备无效", HomeException.GETWAYIDINVALID);
            }

            gateway.setGatewayAccount(device.getDeviceid());
            gateway.setDeviceId(device.getDeviceid());
            HomeServer.getAppEngine().logging(gateway.getGatewayAccount(), JSONObject.toJSONString(gateway).toString(), "endRegister", "register", gateway.getSerialno());


            userStatusInfo.setData(gateway);
        } catch (HomeException e) {
            //e.printStackTrace();
            logger.error(e.getMessage());
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;
    }

    public ResultModel<Gateway> activate(Gateway gateway) {
        ResultModel<Gateway> userStatusInfo = new ResultModel<Gateway>();
        try {
            Device device = HomeServer.getAppEngine().getDeviceById(gateway.getDeviceId());
            if (device == null) {
                device = HomeServer.getAppEngine().getDeviceByIeee(gateway.getSerialno());
            }

            if (device == null) {
                throw new HomeException("GatewayId 不存在，请先完成注册！ ", HomeException.USERNAMEONTEXITS);
            }

            HomeServer.getAppEngine().logging(device.getBindingaccount(), JSONObject.toJSONString(gateway).toString(), "startActivate", "activate", gateway.getSerialno());
            gateway = HomeServer.getAppEngine().activateGateway(gateway);

            HomeServer.getAppEngine().logging(device.getBindingaccount(), JSONObject.toJSONString(gateway).toString(), "endActivate", "activate", gateway.getSerialno());
            userStatusInfo.setData(gateway);
        } catch (JDSException e) {
            //e.printStackTrace();
            logger.error(e.getMessage());
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;

    }

    public ResultModel<List<Command>> gatewayErrorReport(GatewayErrorReport errorPort) {
        ResultModel<List<Command>> userStatusInfo = new ResultModel<List<Command>>();
        try {

            logger.error("errorPort=" + JSON.toJSONString(errorPort));

            List<Command> commandList = HomeServer.getAppEngine().gatewayErrorReport(errorPort);

            if (errorPort.getGatewayAccount() != null) {
                HomeServer.getAppEngine().logging(errorPort.getGatewayAccount(), JSONObject.toJSONString(errorPort).toString(), "gatewayErrorReport", "gatewayErrorReport", errorPort.getSerialno());
            }
            userStatusInfo.setData(commandList);

        } catch (HomeException e) {
            logger.error(e.getMessage());
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;
    }

    public ResultModel<GWUser> login(GWUser userInfo) {

        ResultModel<GWUser> userStatusInfo = new ResultModel<GWUser>();

        try {
            logger.info("login startLogin");
            String userName = userInfo.getAccount();
            //  HomeServer.getAppEngine().logging(userName, JSONObject.toJSONString(userInfo).toString(), "startLogin");
            if (userInfo.getAccount() == null) {
                throw new HomeException("userName is null!", HomeException.USERNAMEALREADYEXITS);
            }
            OrgManager orgManager = OrgManagerFactory.getOrgManager();
            Person person = null;
            JDSClientService client = null;

            // 根据用户名密码获取用户

            try {
                person = orgManager.getPersonByAccount(userName);
                // 封装登录信息
                ConnectInfo connectInfo = new ConnectInfo(person.getID(), person.getAccount(), person.getPassword());
                // 获取session
                JDSSessionFactory factory = new JDSSessionFactory(JDSActionContext.getActionContext());

                JDSSessionHandle sessionHandle = factory.createSessionHandle();


                JDSActionContext.getActionContext().getContext().put(JDSActionContext.JSESSIONID, sessionHandle.getSessionID());
                Device device = this.getDeviceById(userName);


                String subSystemId = device.getSubsyscode();

                ServerNode serverBean = JDSServer.getClusterClient().getAllServerMap().get(subSystemId);

                if (serverBean == null) {
                    subSystemId = device.getFactory();
                    serverBean = JDSServer.getClusterClient().getAllServerMap().get(subSystemId);
                    if (serverBean == null) {
                        subSystemId = "jds";
                        serverBean = JDSServer.getClusterClient().getAllServerMap().get(subSystemId);
                    }
                }


                logger.info("login newClientService");

                // 将该标识码添加到session中
                JDSActionContext.getActionContext().getSession().put(JDSActionContext.SYSCODE, subSystemId);


                Set<JDSSessionHandle> sessionHandleList = JDSServer.getInstance().getSessionHandleList(connectInfo);

                // 注销掉多余的SESSION
                List<JDSSessionHandle> newsessionHandleList = new ArrayList<JDSSessionHandle>();

                if (sessionHandleList != null) {
                    newsessionHandleList.addAll(sessionHandleList);
                    for (JDSSessionHandle handle : newsessionHandleList) {
                        if (handle != null && !handle.equals(sessionHandle)) {
                            JDSServer.getInstance().disconnect(handle);
                        }

                    }

                }

                client = factory.newClientService(sessionHandle, ConfigCode.gw);

                client.connect(connectInfo);

                GWClient gwclient = HomeServer.getInstance().getGWClient(client);
                logger.info("login gatewayOnLine");
                try {
                    gwclient.gatewayOnLine(device.getDeviceid());
                } catch (HomeException e) {
                    e.printStackTrace();
                }

                final String subsyscode = device.getSubsyscode();

                CommandFactory.getCommandExecutors(device.getSerialno()).shutdownNow();
                SyncTimeCommandTask syscommand = new SyncTimeCommandTask(device.getSerialno(), subsyscode);
                CommandFactory.getCommandExecutors(device.getSerialno()).schedule(syscommand, 34, TimeUnit.SECONDS);
                SersorReportCommandTask command = new SersorReportCommandTask(device.getSerialno(), subsyscode);
                CommandFactory.getCommandExecutors(device.getSerialno()).schedule(command, 28, TimeUnit.SECONDS);


                logger.info("login endLogin");


                User user = getUserByAccount(userName);
                userInfo = new GWUser(user);
                userInfo.setSystemCode(client.getConfigCode().getType());
                userInfo.setSessionId(sessionHandle.getSessionID());
                userStatusInfo.setData(userInfo);
                HomeServer.getAppEngine().login(device.getDeviceid());
                HomeServer.getAppEngine().logging(userName, JSONObject.toJSONString(userInfo).toString(), "endLogin", "login", device.getSerialno());

            } catch (JDSException e) {
                e.printStackTrace();
                logger.error("login error userName=" + userName);
                throw new HomeException("登录失败", HomeException.NOTLOGIN);
            } catch (PersonNotFoundException e) {
                throw new HomeException("登录失败，设备未完成注册！", HomeException.NOTLOGIN);
            }

        } catch (HomeException e) {
            //  e.printStackTrace();
            logger.error(e.getMessage());
            logger.error("login error userName=" + e);
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }
        logger.info(JSONObject.toJSONString(userStatusInfo));
        return userStatusInfo;

    }

    public ResultModel<GWUser> gwLogin(String gwIeee) {
        GWUser user = new GWUser();
        ResultModel<GWUser> userStatusInfo = new ResultModel<GWUser>();
        try {
            Device device = HomeServer.getAppEngine().getDeviceByIeee(gwIeee);
            user.setAccount(device.getBindingaccount());
        } catch (HomeException e) {
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        } catch (JDSException e) {
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return login(user);
    }

    public ResultModel<Boolean> logout() {
        ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();
        try {


            final String sysCode = JDSActionContext.getActionContext().getSystemCode();
            JDSActionContext.getActionContext().getSession().clear();

            final JDSSessionFactory factory = new JDSSessionFactory(JDSActionContext.getActionContext());
            JDSClientService client = (JDSClientService) JDSActionContext.getActionContext().Par("$JDSC");
            if (client != null) {
                client.disconnect();
            }
            client = factory.getClientService(ConfigCode.device);
            if (client != null) {
                client.disconnect();
            }

//            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//            HttpSession session = request.getSession(true);
//            session.invalidate();
        } catch (JDSException e) {
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;

    }

    private Device getDeviceById(String deviceId) throws HomeException {
        CtIotCacheManager appEngine = HomeServer.getAppEngine();
        return appEngine.getDeviceById(deviceId);
    }

    private User getUserByAccount(String account) throws HomeException {

        Person person = null;
        try {
            person = OrgManagerFactory.getOrgManager().getPersonByAccount(account);
        } catch (PersonNotFoundException e) {
            throw new HomeException("账户不存在", HomeException.USERNAMEONTEXITS);
        }
        User user = new User();
        user.setAccount(account);
        user.setId(person.getID());
        user.setEmail(person.getEmail());
        user.setName(person.getName());
        user.setPhone(person.getMobile());

        return user;
    }

    private void fireDeviceEvent(final Device device, final DeviceEventEnums eventID) throws HomeException {
        if (device != null) {
            fireDeviceEvent(device, eventID, null);
        }

    }

    private void fireDeviceEvent(final Device device, final DeviceEventEnums eventID, Map eventContext) throws HomeException {
        DeviceEvent event = new DeviceEvent(device, null, eventID, null);
        event.setContextMap(eventContext);
        try {
            HomeEventControl.getInstance().dispatchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

