/**
 * $RCSfile: PersonNotFoundException.java,v $
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
package net.ooder.org;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * 人员不存在的异常类。
 * <p>Title: ooder组织机构中间件</p>
 * <p>Description:  </p>
 * <p></p>
 * <p>Copyright: Copyright (c) 2003-2008</p>
 * <p>Company: raddev.cn</p>
 * @author  ooder
 * @version 2.0
 */
public class PersonNotFoundException extends Exception {

	private Throwable nestedThrowable = null;

	public PersonNotFoundException() {
		super();
	}

	public PersonNotFoundException(String msg) {
		super(msg);
	}

	public PersonNotFoundException(Throwable nestedThrowable) {
		this.nestedThrowable = nestedThrowable;
	}

	public PersonNotFoundException(String msg, Throwable nestedThrowable) {
		super(msg);
		this.nestedThrowable = nestedThrowable;
	}

	public void printStackTrace() {
		super.printStackTrace();
		if (nestedThrowable != null) {
			nestedThrowable.printStackTrace();
		}
	}

	public void printStackTrace(PrintStream ps) {
		super.printStackTrace(ps);
		if (nestedThrowable != null) {
			nestedThrowable.printStackTrace(ps);
		}
	}

	public void printStackTrace(PrintWriter pw) {
		super.printStackTrace(pw);
		if (nestedThrowable != null) {
			nestedThrowable.printStackTrace(pw);
		}
	}
}