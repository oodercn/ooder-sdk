/**
 * $RCSfile: ExpressionTempFtlBean.java,v $
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
package net.ooder.esb.config.manager;

/**
 * time 06-01-01
 *
 * @author wenzhang
 */
public class ExpressionTempFtlBean {
    private String classname;
    private String type;
    private String ftlpath;
    private String name;
    private String id;

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public String getFtlpath() {
		return ftlpath;
	}

	public void setFtlpath(String ftlpath) {
		this.ftlpath = ftlpath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

	
}
