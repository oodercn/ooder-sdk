/**
 * $RCSfile: FilterChain.java,v $
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
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 查询结果过滤器的链调用实现。
 * </p>
 * 此实现类本身不做任何判断和过滤，仅仅提供一个过滤器链的载体。
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
public class FilterChain extends AbstractFilter {

	/**
	 * 进行链过滤操作
	 * 
	 * @param obj
	 * @return
	 */
	public boolean filterObject(Object obj) {
		return processChildFilter(obj);
	}
}