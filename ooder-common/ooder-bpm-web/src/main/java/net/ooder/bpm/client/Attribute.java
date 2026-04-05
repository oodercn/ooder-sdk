/**
 * $RCSfile: Attribute.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:26:08 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.client;

import java.util.List;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 所有扩展属性的基类. 定义扩展属性和实例扩展属性接口的父接口，定义了在这两个接口中共用的方法。 此接口本身一般不会直接使用
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author wenzhang li
 * @version 1.0
 */
public interface Attribute  extends java.io.Serializable{


	/**
	 * 取得此属性定义的UUID
	 *
	 * @return 属性定义的UUID
	 */
	public String getId();

	public void setId(String id);
	/**
	 * 取得属性的名称
	 * 
	 * @return the value of propname
	 */
	public String getName();

	public void setName(String name);

	/**
	 * 取得属性的值，此值已经经过了解释<br>
	 * 解释方法根据此属性设置的解释类来决定 如果未定义则直接返回原字符串
	 * 
	 * @return 解释后的属性值
	 */
	public Object getInterpretedValue();


	/**
	 * 取得属性值，这个值是数据库中储存的字符串，未经解释
	 * 
	 * @return 未解释前的属性值
	 */
	public String getValue();

	public void setValue(String value);


	/**
	 * 取得此属性的父属性UUID
	 *
	 * @return the value of parentpropId
	 */
	public String getParentId();

	public void setParentId(String parentId);
	/**
	 * 取得此属性的父属性
	 * 
	 * @return 父属性
	 */
	public Attribute getParent();

	/**
	 * 取得此属性的所有子属性
	 * 
	 * @return 子属性列表，保存类型为Attribute
	 */
	public List<Attribute> getChildren();

	/**
	 * 取得此属性的所有子属性ID
	 *
	 * @return 子属性列表，保存类型为Attribute
	 */
	public List<String> getChildrenIds();

	public void setChildrenIds(List<String> ids);

	/**
	 * 取得指定名称的子属性
	 * 
	 * @param name
	 *            子属性名
	 * @return 属性对象
	 */
	public Attribute getChild(String name);

}
