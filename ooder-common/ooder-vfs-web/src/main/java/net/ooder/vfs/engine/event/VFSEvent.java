/**
 * $RCSfile: VFSEvent.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:25:43 $
 * <p>
 * Copyright (C) 2003 ooder, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of ooder, Inc.
 * Use is subject to license terms.
 */
package net.ooder.vfs.engine.event;

import  net.ooder.common.JDSEvent;
import  net.ooder.common.JDSListener;
import  net.ooder.vfs.ct.CtVfsService;

/**
 * <p>
 * Title: VFS虚拟文件管理系统
 * </p>
 * <p>
 * Description: VFS虚拟文件内所有事件的基类，继承自java.util.EventObject
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author  ooder
 * @version 2.0
 */
public abstract class VFSEvent<T> extends JDSEvent {

    public static final String  RepeatVFSEvent = "$RepeatVFSEvent";

    private JDSListener listener;

    public VFSEvent(T source) {
        super(source);

    }

    public VFSEvent(T source, JDSListener listener) {
        super(source);
        this.listener = listener;
    }

    protected String expression;

    protected CtVfsService client = null;

    /**
     * 设置发生事件时的VFSClientService对象！
     *
     * @param client
     */
    public void setClientService(CtVfsService client) {
        this.client = client;
    }

    /**
     * 取得发生事件时的VFSClientService对象！
     *
     * @return
     */
    public CtVfsService getClientService() {
        return client;
    }

}
