/**
 * $RCSfile: CAJDSService.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:25:44 $
 *
 * Copyright (C) 2025 ooder, Inc. All rights reserved.
 *
 * This software is the proprietary information of ooder, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: CAJDSService.java,v $
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
package net.ooder.config;

import java.io.Serializable;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 对应应用配置文件中的RightEngine元素
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 * 
 * @author wenzhang
 * @version 2.0
 */
public class CAJDSService implements Serializable {
	private String implementation;

	public String getImplementation() {
		return implementation;
	}

	public void setImplementation(String implementation) {
		this.implementation = implementation;
	}
}
