/**
 * $RCSfile: ServiceEvent.java,v $
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

import net.ooder.common.JDSEvent;
import net.ooder.esb.config.manager.ServiceBean;

/**
 * <p>
 * Title: VFS管理系统
 * </p>
 * <p>
 * Description: 核心文件事件
 * </p>
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author  ooder
 * @version 2.0
 */
@SuppressWarnings("all")
public class ServiceEvent<T extends ServiceBean> extends JDSEvent<T> {


    @Override
    public T getSource() {
        return super.getSource();
    }

    /**
     * ServerEvent
     *
     * @param path
     * @param eventID
     */
    public ServiceEvent(T ServiceBean, ServiceEventEnums eventID, String sysCode) {
        super(ServiceBean, null);
        id = eventID;
        this.systemCode = sysCode;
    }

    @Override
    public ServiceEventEnums getID() {
        return (ServiceEventEnums) id;
    }


}
