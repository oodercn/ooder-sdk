/**
 * $RCSfile: RemoteConnectionManager.java,v $
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
package net.ooder.web;

import net.ooder.thread.JDSThreadFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RemoteConnectionManager {

    //默认允许连接服务
    private final static Integer defaultConnectionSize = 150;

    static Map<String, ExecutorService> connectionMap = new HashMap<String, ExecutorService>();

    static Map<String, Integer> connectionSize = new HashMap<String, Integer>();


    public static synchronized void initConnection(String serviceId, Integer size) {
        if (size != null && size > 0) {
            connectionSize.put(serviceId, size + 1);
        } else {
            connectionSize.put(serviceId, defaultConnectionSize);
        }
    }


    public static ExecutorService getStaticConntction(String serviceId) {
        synchronized (serviceId) {
            ExecutorService service = connectionMap.get(serviceId);
            if (service == null || service.isShutdown()) {
                service = Executors.newSingleThreadScheduledExecutor(new JDSThreadFactory(serviceId));
                connectionMap.put(serviceId, service);
            }
            return service;
        }

    }

    public static ExecutorService createConntctionService(String serviceId) {
        ExecutorService service = connectionMap.get(serviceId);
        if (service != null && (!service.isShutdown() || !service.isTerminated())) {
            initConnection(serviceId + 1, connectionSize.get(serviceId));
            service = getConntctionService(serviceId + 1);
        } else {
            service = getConntctionService(serviceId);
        }
        return service;
    }

    public static ExecutorService getConntctionService(String serviceId) {
        synchronized (serviceId) {
            ExecutorService service = connectionMap.get(serviceId);
            if (service == null || service.isShutdown()) {
                if (connectionSize.get(serviceId) == null) {
                    service = Executors.newCachedThreadPool(new JDSThreadFactory(serviceId));
                } else {
                    service = Executors.newScheduledThreadPool(connectionSize.get(serviceId), new JDSThreadFactory(serviceId));
                }
                connectionMap.put(serviceId, service);
            }
            return service;
        }

    }
}
