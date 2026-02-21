/**
 * $RCSfile: ListenerLoad.java,v $
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
package net.ooder.esb.event;

import net.ooder.esb.config.manager.ServiceBean;

import java.util.EventListener;
import java.util.List;

public interface ListenerLoad {

    public <T extends EventListener> List<T> getListenerByType(Class<T> listenerClass);

    public List<ServiceBean> getAllListener();

}
