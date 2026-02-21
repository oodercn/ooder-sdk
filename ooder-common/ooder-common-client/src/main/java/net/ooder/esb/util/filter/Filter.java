/**
 * $RCSfile: Filter.java,v $
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
package net.ooder.esb.util.filter;

/**
 * <p>
 * Title: JDSESB
 * </p>
 * <p>
 * Description: 过滤器接口
 * </p>
 * <p>
 * Copyright: Copyright (c) 2010
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 * 
 * @author wenzhang li
 * @version 1.0
 */
public interface Filter {

	/**
	 * 应用应该实现的过滤方法。
	 * 
	 * @param obj
	 *            需要过滤的对象
	 * @return
	 */
	public abstract boolean filterObject(Object obj);
}