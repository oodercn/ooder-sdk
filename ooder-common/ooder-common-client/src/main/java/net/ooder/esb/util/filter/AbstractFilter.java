/**
 * $RCSfile: AbstractFilter.java,v $
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

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title: JDS总线查询过滤器实现
 * <p>
 * Description: 查询结果过滤器实现，用于对总线返回结果进行过滤。
 * <p>
 * 此抽象类实现了一个过滤器链，每个继承此类的实现类都可以具有过滤器链的功能。
 * <p>
 * Copyright: Copyright (c) 2025
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author wenzhang li
 * @version 1.0
 */
public abstract class AbstractFilter implements Filter {

	protected List childFilterList = new ArrayList();

	/**
	 * 添加下级过滤器， 实现类需要在FilterObject方法中调用<code>processChildFilter()</code>方法才能使下级过滤器正常运行
	 */
	public void addFilter(Filter filter) {
		if (filter != null) {
			childFilterList.add(filter);
		}
	}

	/**
	 * 调用下级过滤器，实现类不需要覆盖此方法。在实现filterObject()方法时调用此方法即可。
	 */
	protected boolean processChildFilter(Object obj) {
		boolean result = true;
		for (int i = 0; i < childFilterList.size(); i++) {
			Filter filter = (Filter) childFilterList.get(i);
			if (!filter.filterObject(obj)) {
				result = false;
				break;
			}
		}
		return result;
	}

}
