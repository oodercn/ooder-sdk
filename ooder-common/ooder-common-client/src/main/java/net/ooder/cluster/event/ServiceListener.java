/**
 * $RCSfile: ServiceListener.java,v $
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
package net.ooder.cluster.event;

public interface ServiceListener extends java.util.EventListener {


    void addService(ServiceEvent event);

    void delService(ServiceEvent event);

    void updateService(ServiceEvent event);

    void addJar(ServiceEvent event);
}
