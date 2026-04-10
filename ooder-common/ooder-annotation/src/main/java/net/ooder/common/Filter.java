/**
 * $RCSfile: Filter.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/08/25 00:26:08 $
 *
 * Copyright (C) 2025 ooder. All rights reserved.
 *
 * This software is the proprietary information of ooder.
 * Use is subject to license terms.
 */
package net.ooder.common;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 查询结果过滤器接口
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025
 * </p>
 * <p>
 * Company: ooder
 * </p>
 * 
 * @author ooder
 * @version 1.0
 */
public interface Filter {

	/**
	 * 应用应该实现的过滤方法。
	 * 
	 * @param obj
	 *            需要过滤的对象
	 * @return 过滤结果，true表示通过过滤，false表示被过滤
	 */
	public abstract boolean filterObject(Object obj, String systemCode);
}
