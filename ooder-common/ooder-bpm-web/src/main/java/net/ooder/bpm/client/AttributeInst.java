/**
 * $RCSfile: AttributeInst.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:26:08 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.client;


import net.ooder.annotation.AttributeInterpretClass;
import net.ooder.annotation.Attributetype;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 扩展属性实例客户端接口 （包括流程实例、活动实例和路由实例的扩展属性）
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
public interface AttributeInst extends Attribute {



	/**
	 * 属性的类型
	 * 
	 * @return 预定义的类型，见Attribute中的常量
	 * @see net.ooder.bpm.client.Attribute
	 */
	public Attributetype getType();

	/**
	 * 取得此属性定义的解释类
	 * 
	 * @return 类名
	 */
	public AttributeInterpretClass getInterpretClass();

}
