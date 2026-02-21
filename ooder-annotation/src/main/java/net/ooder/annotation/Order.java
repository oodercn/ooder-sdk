/**
 * $RCSfile: Order.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/08/25 00:26:08 $
 *
 * Copyright (C) 2025 ooder. All rights reserved.
 *
 * This software is the proprietary information of ooder.
 * Use is subject to license terms.
 */
package net.ooder.annotation;

import net.ooder.common.ConditionKey;

import java.io.Serializable;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 用于排序。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025
 * </p>
 * <p>
 * Company: ooder
 * </p>
 * 
 * @see ConditionKey
 * @author ooder
 * @version 1.0
 */
public class Order<T extends ConditionKey> implements Serializable {
	public T key;

	public boolean asc;

	public boolean isAsc() {
		return asc;
	}

	public void setAsc(boolean asc) {
		this.asc = asc;
	}

	/**
	 * 构造方法，确定排序字段以及排序方法

	 * 
	 * @param key
	 *            排序的字段
	 * @param asc
	 *            排序方法
	 *            <li> true - 升序（ASC）
	 *            <li> false - 降序（DESC）
	 */
	public Order(T key, boolean asc) {
		this.key = key;
		this.asc = asc;
	}

	/**
	 * 得到Order By子句
	 */
	public String toString() {
		return key.getValue()+ (asc ? " ASC" : " DESC");
	}

	public T getKey() {
		return key;
	}

	public void setKey(T key) {
		this.key = key;
	}
}
