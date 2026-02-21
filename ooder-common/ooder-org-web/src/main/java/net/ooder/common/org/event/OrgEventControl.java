/**
 * $RCSPerson: VFSEventControl.java,v $
 * $Revision: 1.2 $
 * $Date: 2025/10/30 12:28:24 $
 * <p>
 * Copyright (C) 2025 ooder, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of ooder, Inc.
 * Use is subject to license terms.
 */
package net.ooder.common.org.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.ooder.common.JDSEvent;
import net.ooder.common.JDSException;
import net.ooder.common.JDSListener;
import net.ooder.org.Person;
import net.ooder.org.Role;
import net.ooder.cluster.udp.ClusterEvent;
import net.ooder.common.JDSConstants;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.org.listener.OrgListener;
import net.ooder.common.org.listener.PersonListener;
import net.ooder.common.org.listener.RoleListener;
import net.ooder.common.util.ClassUtility;
import net.ooder.context.JDSActionContext;
import net.ooder.engine.event.JDSEventDispatcher;
import net.ooder.esb.config.manager.EsbBeanFactory;
import net.ooder.esb.config.manager.ExpressionTempBean;
import net.ooder.esb.config.manager.ServiceBean;
import net.ooder.org.*;
import net.ooder.org.conf.OrgConstants;
import net.ooder.org.enums.OrgEventEnums;
import net.ooder.org.enums.PersonEventEnums;
import net.ooder.org.enums.RoleEventEnums;
import net.ooder.server.JDSServer;

import java.util.*;
import java.util.Map.Entry;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author wenzhang li
 * @version 2.0
 */
public class OrgEventControl implements JDSEventDispatcher {

    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, OrgEventControl.class);

    // singleton instance
    private static OrgEventControl instance = null;

    public static Map<Class, List<ExpressionTempBean>> listenerMap = new HashMap<Class, List<ExpressionTempBean>>();

    public static Map<String, Long> dataEventMap = CacheManagerFactory.createCache(JDSConstants.ORGCONFIG_KEY, "dataEventTiemMap");


    public static Map<String, ExpressionTempBean> listenerBeanMap = new HashMap<String, ExpressionTempBean>();

    public static OrgEventControl getInstance() {
        if (instance == null) {
            synchronized (OrgEventControl.class) {
                if (instance == null) {
                    instance = new OrgEventControl();
                }
            }
        }
        return instance;
    }

    protected OrgEventControl() {
        listenerBeanMap.putAll(net.ooder.web.client.ListenerTempAnnotationProxy.getListenerBeanMap());
        List<? extends ServiceBean> esbBeans = EsbBeanFactory.getInstance().loadAllServiceBean();
        for (ServiceBean esbBean : esbBeans) {
            if (esbBean instanceof ExpressionTempBean) {

                listenerBeanMap.put(esbBean.getId(), (ExpressionTempBean) esbBean);
            }

        }
        getListenerByType(OrgListener.class);
        getListenerByType(PersonListener.class);
        getListenerByType(RoleListener.class);
    }

    @Override
    public void dispatchEvent(JDSEvent event) throws JDSException {
        if (event instanceof OrgEvent) {
            dispatchOrgEvent((OrgEvent) event);
        } else if (event instanceof RoleEvent) {
            dispatchRoleEvent((RoleEvent) event);
        } else if (event instanceof PersonEvent) {
            dispatchPersonEvent((PersonEvent) event);
        }

    }

    /**
     * 分发文件夹事件
     *
     * @param event 核心活动事件
     */
    public void dispatchOrgEvent(final OrgEvent event) throws JDSException {
        OrgEvent fe = event;
        Org org = (Org) fe.getSource();
        String systemCode = event.getSystemCode();
        if (org != null) {
            String key = event.getID().getCode() + org.getOrgId();

            Long checkOutTime = dataEventMap.get(key);

            if (checkOutTime == null || System.currentTimeMillis() - checkOutTime > 1000) {
                dataEventMap.put(key, System.currentTimeMillis());
                repeatEvent(event, key);
            }
            List<JDSListener> listeners = getListenerByType(OrgListener.class);
            for (int k = 0; k < listeners.size(); k++) {
                OrgListener OrgListener = (OrgListener) listeners.get(k);
                if (OrgListener.getSystemCode() == null || OrgListener.getSystemCode().equals(systemCode)) {
                    dispatchOrgEvent(event, (OrgListener) listeners.get(k));
                }
            }
        }
    }

    public void dispatchPersonEvent(final PersonEvent event) throws JDSException {
        //   PersonEvent fe = event;
        Person person = (Person) event.getSource();
        String systemCode = event.getSysCode();
        if (person != null) {
            String key = event.getID().getCode() + person.getID();
            Long checkOutTime = dataEventMap.get(key);

            if (checkOutTime == null || System.currentTimeMillis() - checkOutTime > 500) {
                dataEventMap.put(key, System.currentTimeMillis());
                repeatEvent(event, key);
            }
            List<JDSListener> listeners = getListenerByType(PersonListener.class);
            for (int k = 0; k < listeners.size(); k++) {
                PersonListener PersonListener = (PersonListener) listeners.get(k);
                if (PersonListener.getSystemCode() == null || PersonListener.getSystemCode().equals(systemCode)) {
                    dispatchPersonEvent(event, (PersonListener) listeners.get(k));
                }
            }

        }
    }


    public void dispatchRoleEvent(final RoleEvent event) throws JDSException {
        RoleEvent fe = event;
        Role role = (Role) event.getSource();
        String path = role.getRoleId();
        String systemCode = (String) event.getContextMap().get(OrgConstants.CONFIG_KEY.getType());
        if (path != null && !path.equalsIgnoreCase("")) {
            String key = event.getID().getCode() + path;
            Long checkOutTime = dataEventMap.get(key);
            if (checkOutTime == null || System.currentTimeMillis() - checkOutTime > 500) {
                dataEventMap.put(key, System.currentTimeMillis());
                repeatEvent(event, key);
            }
            List<JDSListener> listeners = getListenerByType(RoleListener.class);
            for (int k = 0; k < listeners.size(); k++) {
                RoleListener RoleListener = (RoleListener) listeners.get(k);
                if (RoleListener.getSystemCode() == null || RoleListener.getSystemCode().equals(systemCode)) {
                    dispatchRoleEvent(event, (RoleListener) listeners.get(k));
                }
            }
        }
    }


    public boolean repeatEvent(JDSEvent event, String msgId) throws JDSException {
        Boolean isSend = false;
        String serverBeanId = JDSServer.getInstance().getCurrServerBean().getId();
        if (serverBeanId.equals("org") || serverBeanId.equals("cluster")) {

            OrgEventTypeEnums type = OrgEventTypeEnums.fromEventClass(event.getClass());
            ClusterEvent clusterEvent = new ClusterEvent();
            clusterEvent.setEventId(event.getID().getMethod());

            if (event.getSource() instanceof String || event.getSource() instanceof Integer || event.getSource() instanceof Double) {
                clusterEvent.setSourceJson(event.getSource().toString());
            } else {
                String source = JSON.toJSONString(event.getSource());
                clusterEvent.setSourceJson(source);
            }

            clusterEvent.setMsgId(msgId);
            clusterEvent.setSessionId(JDSServer.getInstance().getAdminUser().getSessionId());
            clusterEvent.setSystemCode(JDSServer.getInstance().getCurrServerBean().getId());
            clusterEvent.setEventName(type.getEventName());
            clusterEvent.setExpression("$RepeatORGEvent");
            String eventStr = JSON.toJSONString(clusterEvent);

            isSend = JDSServer.getClusterClient().getUDPClient().send(eventStr);
            logger.info("success repeatEvent [" + isSend + "]" + event.getID());
        }

        return isSend;

    }

    public <T> void dispatchClusterEvent(String object, String eventName, String event, String systemCode) throws JDSException {
        OrgEventTypeEnums type = OrgEventTypeEnums.fromName(eventName);


        switch (type) {
            case PersonEvent:
                Person person = JSONObject.parseObject(object,Person.class) ;
                PersonEvent PersonEvent = new PersonEvent(person, PersonEventEnums.fromMethod(event), systemCode);
                this.dispatchPersonEvent(PersonEvent);
                break;

            case OrgEvent:
                Org org =  JSONObject.parseObject(object,Org.class);
                OrgEvent OrgEvent = new OrgEvent(org, OrgEventEnums.fromMethod(event), systemCode);
                this.dispatchOrgEvent(OrgEvent);

                break;

            case RoleEvent:
                Role role =JSONObject.parseObject(object,Role.class);
                RoleEvent rightEvent = new RoleEvent(role, RoleEventEnums.fromMethod(event), systemCode);
                this.dispatchRoleEvent(rightEvent);
                break;
            default:
                break;
        }

    }

    public static List<ExpressionTempBean> getListenerTempBeanByType(Class<? extends EventListener> listenerClass) {

        // synchronized (listenerClass.getName().intern()) {
        List<JDSListener> listeners = new ArrayList<JDSListener>();
        Set<Entry<String, ExpressionTempBean>> tempEntry = listenerBeanMap.entrySet();

        List<ExpressionTempBean> tempLst = listenerMap.get(listenerClass);

        if (tempLst == null || tempLst.isEmpty()) {
            tempLst = new ArrayList<ExpressionTempBean>();
            for (Entry<String, ExpressionTempBean> entry : tempEntry) {
                ExpressionTempBean bean = entry.getValue();
                String classType = bean.getClazz();
                Class clazz = null;
                try {
                    clazz = ClassUtility.loadClass(classType);
                } catch (ClassNotFoundException e) {
                    continue;
                }

                if (listenerClass.isAssignableFrom(clazz)) {
                    tempLst.add(bean);
                }
                ;
            }
            listenerMap.put(listenerClass, tempLst);

        }
        return tempLst;
        //}
    }

    ;

    private static List<JDSListener> getListenerByType(Class<? extends EventListener> listenerClass) {
        List<ExpressionTempBean> tempLst = getListenerTempBeanByType(listenerClass);
        List<JDSListener> listeners = new ArrayList<JDSListener>();
        for (ExpressionTempBean tempBean : tempLst) {
            JDSListener listener = (JDSListener) JDSActionContext.getActionContext().Par("$" + tempBean.getId());
            if (listener != null) {
                listeners.add(listener);
            }
        }
        return listeners;


    }

    private static void dispatchRoleEvent(final RoleEvent event, final RoleListener listener) {
        try {
            switch (event.getID()) {
                case ADDROLE:
                    listener.roleAdded(event);
                    break;
                case ADDORG:
                    listener.addOrg(event);
                    break;
                case ADDPERSON:
                    listener.addPerson(event);
                    break;
                case CREATE:
                    listener.roleSave(event);
                    break;
                case DELETE:
                    listener.roleDelete(event);
                    break;
                case RENAME:
                    listener.roleSave(event);
                    break;
                case REMOVEORG:
                    listener.removeOrg(event);
                case REMOVEPERSON:
                    listener.removePerson(event);
                    break;
                default:
                    throw new RoleNotFoundException("Unsupport RoleEvent event type: " + event.getID());
            }
        } catch (Throwable e) {
            e.printStackTrace();
            logger.warn("Listener execute failed!", e);
        }
    }

    private static void dispatchPersonEvent(final PersonEvent event, final PersonListener listener) {
        try {
            switch (event.getID()) {


                case CREATE:

                    listener.personAdded(event);
                    break;
                case UPDATE:

                    listener.personSave(event);
                    break;
                case DELETED:

                    listener.personDelete(event);
                    break;


                default:
                    throw new PersonNotFoundException("Unsupport Person event type: " + event.getID());
            }
        } catch (Throwable e) {
            e.printStackTrace();
            logger.warn("Listener execute failed!", e);
        }
    }

    private static void dispatchOrgEvent(final OrgEvent event, final OrgListener listener) {
        try {


            switch (event.getID()) {
                case CREATE:
                    // 文件正在被添加
                    listener.orgCreate(event);
                    break;
                case ADDPERSON:
                    // 文件已经被添加
                    listener.personAdded(event);
                    break;
                case ADDCHILD:
                    listener.orgAdded(event);
                    break;
                case UPDATE:
                    listener.orgSave(event);
                    break;
                case DELET:
                    listener.orgDelete(event);
                    break;

                default:
                    throw new OrgNotFoundException("Unsupport Org event type: " + event.getID());
            }
        } catch (Throwable e) {
            logger.warn("Listener execute failed!", e);
        }
    }

}
