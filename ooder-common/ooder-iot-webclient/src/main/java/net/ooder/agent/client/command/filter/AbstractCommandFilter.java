
package net.ooder.agent.client.command.filter;

import  net.ooder.common.JDSCommand;
import  net.ooder.engine.JDSSessionHandle;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title: JDSSMS系统
 * </p>
 * <p>
 * Description: 查询结果过滤器实现，用于对短信链条进行对象级的再次过滤。
 * </p>
 * 此抽象类实现了一个过滤器链，每个继承此类的实现类都可以具有过滤器链的功能。
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 * 
 * @author wenzhang li
 * @version 1.0
 */
public abstract class AbstractCommandFilter implements CommandFilter {

	protected List childFilterList = new ArrayList();

	/**
	 * 添加下级过滤器， 实现类需要在FilterObject方法中调用 <code>processChildFilter()</code>方法才能使下级过滤器正常运行
	 * 
	 * @param filter
	 */
	public void addFilter(CommandFilter filter) {
		if (filter != null) {
			childFilterList.add(filter);
		}
	}

	/**
	 * 调用下级过滤器，实现类不需要覆盖此方法， 在实现filterObject()方法时调用此方法即可。
	 * 
	 * @param obj
	 * @return
	 */
	protected boolean processChildFilter(JDSCommand command, JDSSessionHandle handle) {
		boolean result = false;
		for (int i = 0; i < childFilterList.size(); i++) {
			CommandFilter filter = (CommandFilter) childFilterList.get(i);
			if (filter.filterObject(command,handle)) {
				result = true;
				break;
			}
		}
		return result;
	}

}
