/**
 * $RCSfile: AttributeDef.java,v $
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
import net.ooder.common.CommonYesNoEnum;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 扩展属性定义客户端接口 （包括流程定义、活动定义和路由定义的扩展属性）
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author wenzhang li
 * @version 2.0
 */
public interface AttributeDef extends Attribute {



	/**
	 * 取得此属性定义的解释类
	 *
	 * @return 类名
	 */
	public AttributeInterpretClass getInterpretClass();

	/**
	 * 属性的类型
	 * 
	 * @return 预定义的类型，见Attribute中的常量
	 * @see net.ooder.bpm.client.Attribute
	 */
	public Attributetype getType();

	/**
	 * 取得此属性的父属性UUID
	 * 
	 * @return the value of parentpropId
	 */


	public Integer getIsExtension();

	/**
	 * 取得此属性是否可以实例化
	 * 
	 * @return 返回值：
	 *         <li> INSTANTIATE_YES - 可以实例化
	 *         <li> INSTANTIATE_NO - 不能实例化
	 */
	public CommonYesNoEnum getCanInstantiate();

}
