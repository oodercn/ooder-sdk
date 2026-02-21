/**
 * $RCSfile: VFSEventControl.java,v $
 * $Revision: 1.2 $
 * $Date: 2025/10/30 12:28:24 $
 * <p>
 * Copyright (C) 2025 ooder, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of ooder, Inc.
 * Use is subject to license terms.
 */
package net.ooder.vfs.engine.event;

import com.alibaba.fastjson.JSON;
import  net.ooder.cluster.udp.ClusterEvent;
import  net.ooder.common.*;
import  net.ooder.common.cache.CacheManagerFactory;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.common.util.ClassUtility;
import  net.ooder.context.JDSActionContext;
import  net.ooder.engine.event.JDSEventDispatcher;
import  net.ooder.esb.config.manager.EsbBeanFactory;
import  net.ooder.esb.config.manager.ExpressionTempBean;
import  net.ooder.esb.config.manager.ServiceBean;
import  net.ooder.server.JDSServer;
import  net.ooder.vfs.VFSConstants;
import  net.ooder.vfs.VFSException;
import  net.ooder.vfs.enums.*;
import  net.ooder.web.client.ListenerTempAnnotationProxy;

import java.util.*;
import java.util.Map.Entry;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 虚拟文件存储系统事件控制核心，所有引擎事件都在这里中转处理
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
public class VFSEventControl implements JDSEventDispatcher {

    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, VFSEventControl.class);
    // singleton instance
    private static VFSEventControl instance = null;

    public static Map<Class, List<ExpressionTempBean>> listenerMap = new HashMap<Class, List<ExpressionTempBean>>();

    public static Map<String, Long> dataEventMap = CacheManagerFactory.createCache(JDSConstants.ORGCONFIG_KEY, "dataEventTiemMap", 1 * 1024 * 1024, 60 * 60 * 1000);

    public static Map<String, ExpressionTempBean> listenerBeanMap = new HashMap<String, ExpressionTempBean>();

    public static VFSEventControl getInstance() {
        if (instance == null) {
            synchronized (VFSEventControl.class) {
                if (instance == null) {
                    instance = new VFSEventControl();
                }
            }
        }
        return instance;
    }

    protected VFSEventControl() {
        listenerBeanMap.putAll(ListenerTempAnnotationProxy.getListenerBeanMap());
        List<? extends ServiceBean> esbBeans = EsbBeanFactory.getInstance().loadAllServiceBean();
        for (ServiceBean esbBean : esbBeans) {
            if (esbBean instanceof ExpressionTempBean) {
                listenerBeanMap.put(esbBean.getId(), (ExpressionTempBean) esbBean);
            }

        }
        getListenerByType(FolderListener.class);
        getListenerByType(FileObjectListener.class);
        getListenerByType(FileListener.class);
        getListenerByType(FileVersionListener.class);
    }

    @Override
    public void dispatchEvent(JDSEvent event) throws JDSException {
        if (event instanceof FolderEvent) {
            dispatchFolderEvent((FolderEvent) event, false);
        } else if (event instanceof FileVersionEvent) {
            dispatchFileVersionEvent((FileVersionEvent) event, false);
        } else if (event instanceof FileObjectEvent) {
            dispatchFileObjectEvent((FileObjectEvent) event, false);
        } else if (event instanceof FileEvent) {
            dispatchFileEvent((FileEvent) event, false);
        }

    }

    /**
     * 分发文件夹事件
     *
     * @param event 核心活动事件
     */
    public void dispatchFolderEvent(final FolderEvent event, boolean isCluster) throws JDSException {
        if (isCluster) {
            List<FolderListener> listeners = getListenerByType(FolderListener.class);
            for (int k = 0; k < listeners.size(); k++) {
                FolderListener fileListener = listeners.get(k);
                if (!JDSServer.getInstance().getCurrServerBean().getConfigCode().equals(ConfigCode.vfs)) {
                    dispatchFolderEvent(event, listeners.get(k));
                }
            }
        } else {
            event.setSystemCode(JDSServer.getInstance().getCurrServerBean().getId());
            String folderpath = event.getFolderPath();
            String systemCode = event.getSystemCode();
            if (folderpath != null) {
                String key = event.getID().getMethod() + folderpath;
                Long checkOutTime = dataEventMap.get(key);
                if (checkOutTime == null || System.currentTimeMillis() - checkOutTime > 1000) {
                    dataEventMap.put(key, System.currentTimeMillis());
                    repeatEvent(event, key);
                    List<EIFolderListener> listeners = getListenerByType(EIFolderListener.class);
                    for (int k = 0; k < listeners.size(); k++) {
                        EIFolderListener folderListener = (EIFolderListener) listeners.get(k);
                        dispatchEIFolderEvent(event, listeners.get(k));
                    }
                }
            }

        }

    }

    public void dispatchFileEvent(final FileEvent event, boolean isCluster) throws JDSException {

        if (isCluster) {
            event.setSystemCode(JDSServer.getInstance().getCurrServerBean().getId());
            List<FileListener> listeners = getListenerByType(FileListener.class);
            for (int k = 0; k < listeners.size(); k++) {
                FileListener fileListener = listeners.get(k);
                if (!JDSServer.getInstance().getCurrServerBean().getConfigCode().equals(ConfigCode.vfs)) {
                    dispatchFileEvent(event, listeners.get(k));
                }
            }

        } else {
            event.setSystemCode(JDSServer.getInstance().getCurrServerBean().getId());
            String path = event.getFilePath();

            if (path != null && !path.equalsIgnoreCase("")) {
                String key = event.getID().getMethod() + path;
                Long checkOutTime = dataEventMap.get(key);
                if (checkOutTime == null || System.currentTimeMillis() - checkOutTime > 500) {
                    dataEventMap.put(key, System.currentTimeMillis());
                    repeatEvent(event, key);
                    List<EIFileListener> listeners = getListenerByType(EIFileListener.class);
                    for (int k = 0; k < listeners.size(); k++) {
                        EIFileListener fileListener = listeners.get(k);
                        dispatchEIFileEvent(event, listeners.get(k));
                    }
                    EIFileListener customerFileListener = (EIFileListener) event.getContextMap().get(VFSConstants.FileListener);
                    if (customerFileListener != null) {
                        dispatchEIFileEvent(event, customerFileListener);
                    }
                }
            }
        }


    }


    public void dispatchFileObjectEvent(final FileObjectEvent event, boolean isCluster) throws JDSException {
        if (isCluster) {

            List<FileObjectListener> listeners = getListenerByType(FileObjectListener.class);
            for (int k = 0; k < listeners.size(); k++) {
                FileObjectListener fileObjectListener = (FileObjectListener) listeners.get(k);
                if (!JDSServer.getInstance().getCurrServerBean().getConfigCode().equals(ConfigCode.vfs)) {
                    dispatchFileObjectEvent(event, (FileObjectListener) listeners.get(k));
                }
            }
        } else {
            event.setSystemCode(JDSServer.getInstance().getCurrServerBean().getId());
            FileObjectEvent fe = event;
            String path = (String) event.getSource();
            if (path != null && !path.equalsIgnoreCase("")) {
                String key = event.getID().getMethod() + path;
                Long checkOutTime = dataEventMap.get(key);
                if (checkOutTime == null || System.currentTimeMillis() - checkOutTime > 500) {
                    dataEventMap.put(key, System.currentTimeMillis());
                    repeatEvent(event, key);
                    List<EIFileObjectListener> listeners = getListenerByType(EIFileObjectListener.class);
                    for (int k = 0; k < listeners.size(); k++) {
                        EIFileObjectListener fileListener = listeners.get(k);
                        dispatchEIFileObjectEvent(event, listeners.get(k));

                    }
                }
            }

        }
    }


    public void dispatchFileVersionEvent(final FileVersionEvent event, boolean isCluster) throws JDSException {
        FileVersionEvent fe = event;
        String path = (String) event.getSource();
        String systemCode = (String) event.getContextMap().get(VFSConstants.SYSTEM_CODE);
        if (isCluster) {

            List<FileVersionListener> listeners = getListenerByType(FileVersionListener.class);
            for (int k = 0; k < listeners.size(); k++) {
                FileVersionListener fileVersionListener = (FileVersionListener) listeners.get(k);
                if (!JDSServer.getInstance().getCurrServerBean().getConfigCode().equals(ConfigCode.vfs)) {
                    dispatchFileVersionEvent(event, (FileVersionListener) listeners.get(k));
                }

            }


        } else {
            event.setSystemCode(JDSServer.getInstance().getCurrServerBean().getId());
            if (path != null && !path.equalsIgnoreCase("")) {
                String key = event.getID().getMethod() + path;
                Long checkOutTime = dataEventMap.get(key);
                if (checkOutTime == null || System.currentTimeMillis() - checkOutTime > 500) {
                    dataEventMap.put(key, System.currentTimeMillis());
                    repeatEvent(event, key);
                    List<EIFileVersionListener> listeners = getListenerByType(EIFileVersionListener.class);
                    for (int k = 0; k < listeners.size(); k++) {
                        EIFileVersionListener fileListener = listeners.get(k);
                        dispatchEIFileVersionEvent(event, listeners.get(k));
                    }
                }
            }
        }
    }

    ;


    public boolean repeatEvent(VFSEvent event, String msgId) throws JDSException {
        Boolean isSend = false;
        if (JDSServer.getInstance().getCurrServerBean().getId().equals(event.getSystemCode())) {
            VFSEventTypeEnums type = VFSEventTypeEnums.fromEventClass(event.getClass());
            ClusterEvent clusterEvent = new ClusterEvent();
            clusterEvent.setEventId(event.getID().getMethod());
            event.setSystemCode(JDSServer.getInstance().getCurrServerBean().getId());

            if (event.getSource() instanceof String || event.getSource() instanceof Integer || event.getSource() instanceof Double) {
                clusterEvent.setSourceJson(event.getSource().toString());
            } else {
                String source = JSON.toJSONString(event.getSource());
                clusterEvent.setSourceJson(event.getSource().toString());
            }

            clusterEvent.setMsgId(msgId);
            clusterEvent.setSessionId(JDSServer.getInstance().getAdminUser().getSessionId());
            clusterEvent.setSystemCode(JDSServer.getInstance().getCurrServerBean().getId());
            clusterEvent.setEventName(type.getEventName());
            clusterEvent.setExpression(VFSEvent.RepeatVFSEvent);
            String eventStr = JSON.toJSONString(clusterEvent);

            isSend = JDSServer.getClusterClient().getUDPClient().send(eventStr);
            logger.info("success repeatEvent [" + isSend + "]" + event.getID());
        }

        return isSend;

    }

    public <T> void dispatchClusterEvent(String objStr, String eventName, String event, String systemCode) throws JDSException {
        // if (systemCode != null && !systemCode.equals(JDSServer.getInstance().getCurrServerBean().getId())) {
        VFSEventTypeEnums type = VFSEventTypeEnums.fromName(eventName);
        switch (type) {
            case FileEvent:
                String path = objStr;
                FileEvent fileEvent = new FileEvent(path, FileEventEnums.fromMethod(event), systemCode);
                this.dispatchFileEvent(fileEvent, true);
                break;

            case FolderEvent:
                String folderpath = objStr;
                FolderEvent folderEvent = new FolderEvent(folderpath, FolderEventEnums.fromMethod(event), systemCode);
                this.dispatchFolderEvent(folderEvent, true);

                break;

            case FileVersionEvent:
                FileVersionEvent fileVersionEvent = new FileVersionEvent(objStr, FileVersionEventEnums.fromMethod(event), systemCode);
                this.dispatchFileVersionEvent(fileVersionEvent, true);
                break;
            case FileObjectEvent:
                FileObjectEvent objectEvent = new FileObjectEvent(objStr, FileObjectEventEnums.fromMethod(event), systemCode);
                this.dispatchFileObjectEvent(objectEvent, true);
                break;
            default:
                break;
        }
        //     }


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

    private static <T extends EventListener> List<T> getListenerByType(Class<T> listenerClass) {
        List<ExpressionTempBean> tempLst = getListenerTempBeanByType(listenerClass);
        List<T> listeners = new ArrayList<T>();
        for (ExpressionTempBean tempBean : tempLst) {
            T listener = JDSActionContext.getActionContext().Par("$" + tempBean.getId(), listenerClass);
            if (listener != null) {
                listeners.add(listener);
            }
        }
        return listeners;


    }


    private static void dispatchFileObjectEvent(final FileObjectEvent event, final FileObjectListener listener) {
        try {
            switch (event.getID()) {

                case append:
                    listener.append(event);
                    break;
                case share:
                    listener.share(event);
                    break;
                case beforDownLaod:
                    listener.beforDownLoad(event);
                    break;
                case downLaoding:
                    listener.downLoading(event);
                case downLaodEnd:
                    listener.downLoadEnd(event);
                    break;
                case befaultUpLoad:
                    listener.befaultUpLoad(event);
                    break;
                case upLoading:
                    listener.upLoading(event);
                    break;
                case upLoadEnd:
                    listener.upLoadEnd(event);
                    break;
                case upLoadError:
                    listener.upLoadError(event);
                    break;

                default:
                    throw new VFSException("Unsupport FileObjectListener event type: " + event.getID(), VFSException.UNSUPPORTPROCESSEVENTERROR);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            logger.warn("Listener execute failed!", e);
        }
    }


    private static void dispatchEIFileObjectEvent(final FileObjectEvent event, final EIFileObjectListener listener) {
        try {
            switch (event.getID()) {

                case append:
                    listener.append(event);
                    break;
                case share:
                    listener.share(event);
                    break;
                case beforDownLaod:
                    listener.beforDownLoad(event);
                    break;
                case downLaoding:
                    listener.downLoading(event);
                case downLaodEnd:
                    listener.downLoadEnd(event);
                    break;
                case befaultUpLoad:
                    listener.befaultUpLoad(event);
                    break;
                case upLoading:
                    listener.upLoading(event);
                    break;
                case upLoadEnd:
                    listener.upLoadEnd(event);
                    break;
                case upLoadError:
                    listener.upLoadError(event);
                    break;

                default:
                    throw new VFSException("Unsupport FileObjectListener event type: " + event.getID(), VFSException.UNSUPPORTPROCESSEVENTERROR);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            logger.warn("Listener execute failed!", e);
        }
    }

    private static void dispatchFileVersionEvent(final FileVersionEvent event, final FileVersionListener listener) {
        try {
            switch (event.getID()) {
                case lockVersion:
                    listener.lockVersion(event);
                    break;
                case addFileVersion:
                    listener.addFileVersion(event);
                    break;
                case updateFileVersion:
                    listener.updateFileVersion(event);
                    break;
                case deleteFileVersion:
                    listener.deleteFileVersion(event);
                    break;

                default:
                    throw new VFSException("Unsupport FileVersionEvent event type: " + event.getID(), VFSException.UNSUPPORTPROCESSEVENTERROR);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            logger.warn("Listener execute failed!", e);
        }
    }


    private static void dispatchEIFileVersionEvent(final FileVersionEvent event, final EIFileVersionListener listener) {
        try {
            switch (event.getID()) {
                case lockVersion:
                    listener.lockVersion(event);
                    break;
                case addFileVersion:
                    listener.addFileVersion(event);
                    break;
                case updateFileVersion:
                    listener.updateFileVersion(event);
                    break;
                case deleteFileVersion:
                    listener.deleteFileVersion(event);
                    break;

                default:
                    throw new VFSException("Unsupport FileVersionEvent event type: " + event.getID(), VFSException.UNSUPPORTPROCESSEVENTERROR);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            logger.warn("Listener execute failed!", e);
        }
    }


    private static void dispatchFileEvent(final FileEvent event, final FileListener listener) {
        try {
            switch (event.getID()) {
                case beforeDownLoad:
                    listener.beforeDownLoad(event);
                    break;
                case downLoading:
                    listener.downLoading(event);
                    break;
                case downLoadEnd:
                    listener.downLoadEnd(event);
                    break;
                case beforeReName:
                    listener.reNameEnd(event);
                    break;
                case reStore:
                    listener.reStore(event);
                    break;
                case beforeMove:
                    listener.beforeMove(event);
                    break;
                case moveEnd:
                    listener.moveEnd(event);
                    break;
                case beforeDelete:
                    listener.beforeDelete(event);
                    break;
                case deleteEnd:
                    listener.deleteEnd(event);
                    break;
                case send:
                    listener.send(event);
                    break;
                case open:
                    listener.open(event);
                    break;
                case clear:
                    listener.clear(event);
                    break;
                case share:
                    listener.share(event);
                    break;
                case beforeUpLoad:
                    listener.beforeUpLoad(event);
                    break;
                case upLoading:
                    listener.upLoading(event);
                    break;
                case upLoadEnd:
                    listener.upLoadEnd(event);
                    break;
                case upLoadError:
                    listener.upLoadError(event);
                    break;
                case create:
                    listener.create(event);
                    break;
                case beforeCopy:
                    listener.beforeCopy(event);
                    break;
                case copyEnd:
                    listener.copyEnd(event);
                    break;
                case save:
                    listener.save(event);
                    break;
                case beforeUpdate:
                    listener.beforeUpdate(event);
                    break;
                case updateEnd:
                    listener.updateEnd(event);
                    break;


                default:
                    throw new VFSException("Unsupport file event type: " + event.getID(), VFSException.UNSUPPORTPROCESSEVENTERROR);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            logger.warn("Listener execute failed!", e);
        }
    }


    private static void dispatchEIFileEvent(final FileEvent event, final EIFileListener listener) {
        try {
            switch (event.getID()) {
                case beforeDownLoad:
                    listener.beforeDownLoad(event);
                    break;
                case downLoading:
                    listener.downLoading(event);
                    break;
                case downLoadEnd:
                    listener.downLoadEnd(event);
                    break;
                case beforeReName:
                    listener.reNameEnd(event);
                    break;
                case reStore:
                    listener.reStore(event);
                    break;
                case beforeMove:
                    listener.beforeMove(event);
                    break;
                case moveEnd:
                    listener.moveEnd(event);
                    break;
                case beforeDelete:
                    listener.beforDelete(event);
                    break;
                case deleteEnd:
                    listener.deleteEnd(event);
                    break;
                case send:
                    listener.send(event);
                    break;
                case open:
                    listener.open(event);
                    break;
                case clear:
                    listener.clear(event);
                    break;
                case share:
                    listener.share(event);
                    break;
                case beforeUpLoad:
                    listener.beforeUpLoad(event);
                    break;
                case upLoading:
                    listener.upLoading(event);
                    break;
                case upLoadEnd:
                    listener.upLoadEnd(event);
                    break;
                case upLoadError:
                    listener.upLoadError(event);
                    break;
                case create:
                    listener.create(event);
                    break;
                case beforeCopy:
                    listener.beforeCopy(event);
                    break;
                case copyEnd:
                    listener.copyEnd(event);
                    break;
                case save:
                    listener.save(event);
                    break;
                case beforeUpdate:
                    listener.beforUpdate(event);
                    break;
                case updateEnd:
                    listener.updateEnd(event);
                    break;


                default:
                    throw new VFSException("Unsupport file event type: " + event.getID(), VFSException.UNSUPPORTPROCESSEVENTERROR);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            logger.warn("Listener execute failed!", e);
        }
    }


    private static void dispatchFolderEvent(final FolderEvent event, final FolderListener listener) {
        try {
            switch (event.getID()) {

                case create:
                    listener.create(event);
                    break;
                case lock:
                    listener.lock(event);
                    break;
                case beforeReName:
                    listener.beforeReName(event);
                    break;
                case reNameEnd:
                    listener.reNameEnd(event);
                    break;
                case beforeMove:
                    listener.beforeMove(event);
                    break;
                case moving:
                    listener.moving(event);
                    break;
                case moveEnd:
                    listener.moveEnd(event);
                    break;
                case beforeClean:
                    listener.beforeClean(event);
                    break;
                case cleanEnd:
                    listener.cleanEnd(event);
                    break;
                case reStore:
                    listener.reStore(event);
                    break;

                case beforeDelete:
                    listener.beforeDelete(event);
                    break;
                case deleteing:
                    listener.deleteing(event);
                    break;
                case deleteEnd:
                    listener.deleteEnd(event);
                    break;
                case save:
                    listener.save(event);
                    break;

                case beforeCopy:
                    listener.beforeCopy(event);
                    break;
                case copying:
                    listener.copying(event);
                    break;
                case copyEnd:
                    listener.copyEnd(event);
                    break;
                default:
                    throw new VFSException("Unsupport folder event type: " + event.getID(), VFSException.UNSUPPORTPROCESSEVENTERROR);
            }
        } catch (Throwable e) {
            logger.warn("Listener execute failed!", e);
        }
    }


    private static void dispatchEIFolderEvent(final FolderEvent event, final EIFolderListener listener) {
        try {
            switch (event.getID()) {

                case create:
                    listener.create(event);
                    break;
                case lock:
                    listener.lock(event);
                    break;
                case beforeReName:
                    listener.beforeReName(event);
                    break;
                case reNameEnd:
                    listener.reNameEnd(event);
                    break;
                case beforeMove:
                    listener.beforeMove(event);
                    break;
                case moving:
                    listener.moving(event);
                    break;
                case moveEnd:
                    listener.moveEnd(event);
                    break;
                case beforeClean:
                    listener.beforeClean(event);
                    break;
                case cleanEnd:
                    listener.cleanEnd(event);
                    break;
                case reStore:
                    listener.reStore(event);
                    break;

                case beforeDelete:
                    listener.beforeDelete(event);
                    break;
                case deleteing:
                    listener.deleteing(event);
                    break;
                case deleteEnd:
                    listener.deleteEnd(event);
                    break;
                case save:
                    listener.save(event);
                    break;

                case beforeCopy:
                    listener.beforeCopy(event);
                    break;
                case copying:
                    listener.copying(event);
                    break;
                case copyEnd:
                    listener.copyEnd(event);
                    break;
                default:
                    throw new VFSException("Unsupport folder event type: " + event.getID(), VFSException.UNSUPPORTPROCESSEVENTERROR);
            }
        } catch (Throwable e) {
            logger.warn("Listener execute failed!", e);
        }
    }
}
