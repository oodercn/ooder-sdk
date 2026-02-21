
package net.ooder.agent.client.command.filter;

import  net.ooder.common.JDSConstants;
import  net.ooder.common.JDSEvent;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 结果过滤器接口
 * </p>
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 * 
 * @author wenzhang li
 * @version 2.0
 */
public interface EventFilter {
    public static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, EventFilter.class);

    /**
     * 应用应该实现的过滤方法。
     * 
     * @param obj
     *            需要过滤的对象
     * @return
     */
    public abstract boolean filterObject(JDSEvent event);
}
