/**
 * $RCSfile: JIndexBean.java,v $
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
package net.ooder.index.config.bean;

import java.io.Serializable;

public interface JIndexBean extends Serializable{

        public String getId();
	public void setClazz(Class clazz);
	public Class getClazz();
	public void  setId(String id);
}


