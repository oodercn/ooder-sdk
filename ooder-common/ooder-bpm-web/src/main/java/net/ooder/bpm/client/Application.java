/*
 * Created on 2004-3-1
 *
 * The itjds Software License, Version 1.0
 * 
 * Copyright (c) 2004 The itjds Software Foundation.  All rights
 * reserved.
 */

package net.ooder.bpm.client;

import net.ooder.annotation.MethodChinaName;

/**
 * <p>
 * Title: 工作流设计工具（PDT）
 * </p>
 * <p>
 * Description: 工作流应用接口
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: itjds
 * </p>
 * 
 * @author Huchm 2004-3-1 18:24:38
 * @version 1.0
 */
public interface Application extends java.io.Serializable{

	/**
	 * 返回二维数组的工作流列表信息,KEY为分类ID
	 * <p>
	 * String[][0]返回工作流分类ID
	 * </p>
	 * <p>
	 * String[][1]返回工作流分类名称
	 * </p>
	 * 
	 * @param personId
	 * @return 返回二维数组的工作流列表信息
	 */
	 @MethodChinaName(cname="返回二维数组的工作流列表信息,KEY为分类ID")
	public String[][] getFlowKindList(String personId);
}
