/**
 * $RCSfile: ServerEventFactory.java,v $
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
package net.ooder.cluster.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.ooder.cluster.event.RegistEventBean;
import net.ooder.common.EsbFlowType;
import net.ooder.common.JDSConstants;
import net.ooder.common.JDSException;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.config.UserBean;
import net.ooder.context.JDSActionContext;
import net.ooder.context.JDSContext;
import net.ooder.esb.config.manager.EsbBeanFactory;
import net.ooder.esb.config.manager.ExpressionTempBean;
import net.ooder.esb.config.manager.ServiceBean;
import net.ooder.jds.core.User;
import net.ooder.org.conf.OrgConstants;
import net.ooder.server.JDSServer;
import net.ooder.web.RemoteConnectionManager;
import org.apache.http.client.fluent.*;
import org.apache.http.concurrent.FutureCallback;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.Future;

public class ServerEventFactory {

    private static ServerEventFactory instance;

    public static final String THREAD_LOCK = "Thread Lock";

    private String serverUrl;

    private Map<String, Set<? extends ServiceBean>> repeatEventCache = new HashMap<String, Set<? extends ServiceBean>>();

    private Map<String, ServiceBean> eventServiceBean = new HashMap<String, ServiceBean>();

    public static final String APPLICATION_REGISTEREVENT = "/api/sys/registerEvent";

    public static final String APPLICATION_REGISTERJSONEVENT = "/api/sys/registerEventJSON";

    public static final String APPLICATION_CLEAREVENTKEYS = "/api/sys/clearEventKeys";

    public static final String APPLICATION_REMOVEENENT = "/api/sys/removeEvent";

    public static Integer udpEventPort = 8090;


    public static final String APPLICATION_GETALLREGISTEREVENT = "/api/sys/getAllRegisterEvent";


    public static final String APPLICATION_GETREGISTEREVENTBYCODE = "/api/sys/getRegisterEventByCode";


    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, ServerEventFactory.class);

    public static ServerEventFactory getInstance() {
        if (instance == null) {
            synchronized (THREAD_LOCK) {
                if (instance == null) {
                    instance = new ServerEventFactory();
                }
            }
        }
        return instance;
    }

    public ServerEventFactory() {
    }


    public void initEvent(User user) throws JDSException {
        if (!UserBean.getInstance().getConfigName().equals(OrgConstants.UDPCONFIG_KEY)) {
            String udpUrl = UserBean.getInstance().getUdpUrl();
            if (udpUrl != null && !udpUrl.equals("")) {
                this.serverUrl = udpUrl;
            } else {
                String url = UserBean.getInstance().getServerUrl();
                url = url.substring("http://".length());
                if (url.indexOf(":") > -1) {
                    udpEventPort = Integer.valueOf(url.split(":")[1]);
                }
                this.serverUrl = "http://" + user.getUdpIP() + ":" + udpEventPort;
            }

            this.clearEvent();
            try {
                this.registerJSONEvent(getLocalRegisterEvent());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    List<? extends ServiceBean> reLoadServerEvent(String sysCode) {
        String url = APPLICATION_GETREGISTEREVENTBYCODE;
        List<ExpressionTempBean> serviceBeans = new ArrayList<ExpressionTempBean>();
        try {
            final Request request = Request.Post(this.serverUrl + url);
            request.setHeader("Connection", "close");
            Form form = Form.form();
            form.add("sysCode", sysCode);
            request.bodyForm(form.build(), Charset.forName("utf-8"));
            form.add(JDSContext.JDSUSERID, JDSServer.getInstance().getAdminUser().getId());

            Response response = request.execute();
            JSONObject jsonObj = JSONObject.parseObject(response.returnContent().asString());
            Integer status = Integer.valueOf(jsonObj.get("requestStatus").toString());

            if (status == 0) {
                String data = jsonObj.getString("data");
                serviceBeans = JSONArray.parseArray(data, ExpressionTempBean.class);
                for (ServiceBean serviceBean : serviceBeans) {
                    eventServiceBean.put(serviceBean.getId(), serviceBean);
                }
            }


        } catch (Exception e) {
            logger.warn("http failed[" + APPLICATION_GETREGISTEREVENTBYCODE + "] currentThread=" + Thread.currentThread().getId());
            e.printStackTrace();
        }
        return serviceBeans;
    }

    void reload() throws JDSException {
        String url = APPLICATION_GETALLREGISTEREVENT;
        final Request request = Request.Post(this.serverUrl + url);
        request.setHeader("Connection", "close");
        Async async = Async.newInstance().use(RemoteConnectionManager.getConntctionService(serverUrl));
        Future<Content> future = async.execute(request);
        try {
            String json = future.get().asString();
            JSONObject jsonObj = JSONObject.parseObject(json);
            Integer status = Integer.valueOf(jsonObj.get("requestStatus").toString());
            if (status == 0) {
                String data = jsonObj.getString("data");
                List<RegistEventBean> serverEventBeans = JSONArray.parseArray(data, RegistEventBean.class);
                for (RegistEventBean serverEventBean : serverEventBeans) {
                    Set<ExpressionTempBean> serviceBeans = serverEventBean.getEventService();
                    for (ServiceBean serviceBean : serviceBeans) {
                        eventServiceBean.put(serviceBean.getId(), serviceBean);
                    }
                    repeatEventCache.put(serverEventBean.getSysCode(), serverEventBean.getEventService());
                }
            } else {
                throw new JDSException("无法获取注册事件集合");
            }

        } catch (Exception e) {
            logger.warn("未发现V2.1，应用进入兼容模式..");
            logger.warn("该模式下无法完成，可能会影响部分事件功能。");
            e.printStackTrace();
            //throw new JDSException("无法获取注册事件集合。");
        }
    }


    public ServiceBean getServiceBeanById(String serviceId) {
        ServiceBean serviceBean = eventServiceBean.get(serviceId);
        if (serviceBean == null) {
            serviceBean = EsbBeanFactory.getInstance().getEsbBeanById(serviceId);
            eventServiceBean.put(serviceId, serviceBean);
        }
        return serviceBean;
    }


    public void clearEvent() throws JDSException {

        String url = APPLICATION_CLEAREVENTKEYS;
        final Request request = Request.Post(this.serverUrl + url);
        request.setHeader("Connection", "close");
        Form form = Form.form();
        form.add("systemCode", JDSServer.getInstance().getCurrServerBean().getId());
        form.add(JDSContext.JDSUSERID, JDSServer.getInstance().getAdminUser().getId());
        form.add(JDSContext.SYSCODE, JDSServer.getInstance().getCurrServerBean().getId());
        request.bodyForm(form.build(), Charset.forName("utf-8"));
        Async async = Async.newInstance().use(RemoteConnectionManager.getConntctionService(serverUrl));
        Future<Content> future = async.execute(request, new FutureCallback<Content>() {
            public void failed(final Exception ex) {
                logger.warn("http failed[" + APPLICATION_CLEAREVENTKEYS + "] currentThread=" + Thread.currentThread().getId());
                logger.warn("事件清空失败，兼容性错误。");
            }

            public void completed(final Content content) {
            }

            public void cancelled() {
            }
        });
    }


    public void removeEvent(String serviceKey) throws JDSException {
        String url = APPLICATION_REMOVEENENT;
        final Request request = Request.Post(this.serverUrl + url);
        request.setHeader("Connection", "close");
        Form form = Form.form();
        form.add("systemCode", JDSServer.getInstance().getCurrServerBean().getId());
        form.add(JDSContext.SYSCODE, JDSServer.getInstance().getCurrServerBean().getId());
        form.add(JDSContext.JDSUSERID, JDSServer.getInstance().getAdminUser().getId());
        form.add("eventKey", serviceKey);
        form.add(JDSActionContext.JSESSIONID, JDSServer.getInstance().getAdminUser().getSessionId());
        request.bodyForm(form.build(), Charset.forName("UTF-8"));
        Async async = Async.newInstance().use(RemoteConnectionManager.getConntctionService(serverUrl));
        Future<Content> future = async.execute(request, new FutureCallback<Content>() {
            public void failed(final Exception ex) {
                logger.warn("事件移除失败");
                ex.printStackTrace();
            }

            public void completed(final Content content) {
            }

            public void cancelled() {
            }
        });


    }

    int times = 0;

    public void registerJSONEvent(List<? extends ServiceBean> msgEvent) throws JDSException, IOException {
        String url = APPLICATION_REGISTERJSONEVENT;
        times = times + 1;
        final Request request = Request.Post(this.serverUrl + url);
        request.setHeader("Connection", "close");
        Form form = Form.form();
        form.add("systemCode", JDSServer.getInstance().getCurrServerBean().getId());
        form.add("json", JSON.toJSONString(msgEvent));
        form.add(JDSContext.SYSCODE, JDSServer.getInstance().getCurrServerBean().getId());
        form.add(JDSContext.JDSUSERID, JDSServer.getInstance().getAdminUser().getId());
        form.add(JDSActionContext.JSESSIONID, JDSServer.getInstance().getAdminUser().getSessionId());
        request.bodyForm(form.build(), Charset.forName("UTF-8"));
        Async async = Async.newInstance().use(RemoteConnectionManager.getConntctionService(serverUrl));
        try {
            Future<Content> content = async.execute(request, new FutureCallback<Content>() {
                public void failed(final Exception ex) {
                    //  ex.printStackTrace();
                    logger.warn("http failed[" + APPLICATION_REGISTERJSONEVENT + "] currentThread=" + Thread.currentThread().getId());
                    logger.warn("事件清空失败，兼容性错误。");
                }

                public void completed(final Content content) {

                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                                reload();
                            } catch (JDSException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }

                public void cancelled() {
                }
            });
            String json = content.get().asString();
            JSONObject jsonObj = JSONObject.parseObject(json);
            Integer status = Integer.valueOf(jsonObj.get("requestStatus").toString());
            if (status == 0) {
                String data = jsonObj.getString("data");

            } else {
                throw new JDSException("注册集群监听器失败！");
            }

        } catch (Exception e) {

            if (times < 100) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                registerJSONEvent(msgEvent);
            } else {
                registerEvent(msgEvent);
            }

        }

    }


    public void registerEvent(List<? extends ServiceBean> msgEvent) throws JDSException {
        String url = APPLICATION_REGISTEREVENT;
        final Request request = Request.Post(this.serverUrl + url);
        request.setHeader("Connection", "close");
        Form form = Form.form();
        form.add("systemCode", JDSServer.getInstance().getCurrServerBean().getId());
        form.add(JDSContext.SYSCODE, JDSServer.getInstance().getCurrServerBean().getId());
        form.add(JDSContext.JDSUSERID, JDSServer.getInstance().getAdminUser().getId());
        form.add(JDSActionContext.JSESSIONID, JDSServer.getInstance().getAdminUser().getSessionId());
        StringBuffer keyBuffer = new StringBuffer();

        for (ServiceBean bean : msgEvent) {
            keyBuffer.append("$" + bean.getId() + ";");
        }
        form.add("eventKey", keyBuffer.toString());
        request.bodyForm(form.build(), Charset.forName("UTF-8"));
        Async async = Async.newInstance().use(RemoteConnectionManager.getConntctionService(serverUrl));
        Future<Content> future = async.execute(request, new FutureCallback<Content>() {
            public void failed(final Exception ex) {
                logger.warn("http failed[" + APPLICATION_REGISTEREVENT + "] currentThread=" + Thread.currentThread().getId());
                ex.printStackTrace();
            }

            public void completed(final Content content) {
            }

            public void cancelled() {
            }
        });
        String json = "";
        try {
            json = future.get().asString();
            JSONObject jsonObj = JSONObject.parseObject(json);
            Integer status = Integer.valueOf(jsonObj.get("requestStatus").toString());
            if (status == 0) {
                String data = jsonObj.getString("data");
            } else {
                throw new JDSException("注册集群监听器失败！");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new JDSException("注册集群监听器失败！");

        }

    }

    public List<? extends ServiceBean> getLocalRegisterEvent() {
        return EsbBeanFactory.getInstance().getServiceBeanByFlowType(EsbFlowType.msgRepeat);
    }


    public Set<ExpressionTempBean> getAllRegisterEvent() {
        Set<ExpressionTempBean> serviceSet = new LinkedHashSet<ExpressionTempBean>();
        Set<String> serviceIdSet = new LinkedHashSet<String>();
        Set<String> codeSet = repeatEventCache.keySet();

        for (String sysCode : codeSet) {
            Set<ExpressionTempBean> codeSetBeans = (Set<ExpressionTempBean>) repeatEventCache.get(sysCode);
            for (ExpressionTempBean bean : codeSetBeans) {
                if (!serviceIdSet.contains(bean.getId())) {
                    serviceIdSet.add(bean.getId());
                    serviceSet.add(bean);
                }
            }
        }
        return serviceSet;
    }

    public List<ExpressionTempBean> getRegisterEventByCode(String sysCode) {
        List<ExpressionTempBean> serviceBeans = new ArrayList<ExpressionTempBean>();
        if (sysCode == null || sysCode.equals("-all-")) {
            serviceBeans = (List<ExpressionTempBean>) getLocalRegisterEvent();
        } else {
            Set<ExpressionTempBean> eventKeys = (Set<ExpressionTempBean>) repeatEventCache.get(sysCode);
            if (eventKeys == null) {
                eventKeys = new LinkedHashSet<>();
                serviceBeans = (List<ExpressionTempBean>) this.reLoadServerEvent(sysCode);
                if (serviceBeans != null) {
                    for (ServiceBean serviceBean : serviceBeans) {
                        eventKeys.add((ExpressionTempBean) serviceBean);
                        EsbBeanFactory.getInstance().registerService(sysCode, serviceBean);
                    }
                    repeatEventCache.put(sysCode, eventKeys);
                }

            } else {
                for (ExpressionTempBean serviceBean : eventKeys) {
                    if (serviceBean != null) {
                        serviceBeans.add(serviceBean);
                    }
                }
            }
        }
        return serviceBeans;
    }

    ;

//
//    private SysEventWebManager getEventManager() {
//        SysEventWebManager service = (SysEventWebManager) EsbUtil.parExpression("$SysEventWebManager");
//        return service;
//    }

}
