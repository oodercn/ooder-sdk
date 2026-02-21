/**
 * $RCSfile: ESBBeanDefFilter.java,v $
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

import net.ooder.esb.config.manager.ExpressionTempBean;



/**
 * <p>
 * Title: JDSESB
 * </p>
 * <p>
 * Description: 总线对象过滤器
 * </p>
 * 此类为抽象类，在查询活动定义时使用的过滤器必须继承此类，否则将被忽略。
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 * 
 * @author wenzhang li
 * @version 1.0
 */
public abstract  class ESBBeanDefFilter extends AbstractFilter {

	public boolean filterObject(Object obj) {
		if (obj instanceof ExpressionTempBean) {
			return filterESBBeanDef((ExpressionTempBean) obj);
		} else {
			return true;
		}
	}

	/**
	 * 抽象方法，继承类必须实现此方法来过滤活动定义。
	 * 
	 * @see net.ooder.workflow.engine.query.Filter#filterObject(Object)
	 */
	public abstract boolean filterESBBeanDef(ExpressionTempBean obj);

}