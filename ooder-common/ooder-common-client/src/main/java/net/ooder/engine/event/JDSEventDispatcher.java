
/**
 * $RCSfile: JDSEventDispatcher.java,v $
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
package net.ooder.engine.event;

import net.ooder.common.JDSEvent;
import net.ooder.common.JDSException;

/**
 * <p>
 * Title: JDS平台
 * </p>
 * <p>
 * Description: WEB事件并非所有事件都拥有该属性只有在由WEB操作时才会触发改时间
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 * 
 * @author  ooder
 * @version 1.0
 */
public interface JDSEventDispatcher {

    /**
     * 分发事件方法，实现此方法的类需要根据事件类型决定如何分发和处理此事件！
     * 
     * @param <T>
     */
    public <T> void dispatchEvent(JDSEvent<T> event) throws JDSException;
}
