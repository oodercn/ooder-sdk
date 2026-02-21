/**
 * $RCSfile: CPDTElement.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:25:44 $
 *
 * Copyright (C) 2025 ooder, Inc. All rights reserved.
 *
 * This software is the proprietary information of ooder, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: CPDTElement.java,v $
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
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 对应应用配置文件中的PDTElement元素
 * </p>
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
public class CPDTElement implements Serializable {

	private String name;

	private String type;

	private String implementation;

	private List parameters = new ArrayList();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getImplementation() {
		return implementation;
	}

	public void setImplementation(String implementation) {
		this.implementation = implementation;
	}

	public List getParameters() {
		return parameters;
	}

	public void setParameters(List parameters) {
		this.parameters = parameters;
	}

	public void addParameter(CParameter parameter) {
		parameters.add(parameter);
	}

	public String getParameterValue(String name) {
		CParameter param;
		for (int i = 0; i < parameters.size(); i++) {
			param = (CParameter) parameters.get(i);
			if (name.equals(param.getName())) {
				return param.getParameterValue();
			}
		}

		return null;
	}
}
