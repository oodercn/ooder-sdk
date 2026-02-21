/**
 * $RCSfile: PersonPrivateGroupNotFoundException.java,v $
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
package net.ooder.msg;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * 人员工作组不存在的异常类
 * <p>Title: ooder组织机构中间</p>
 * <p>Description:  </p>
 * <p></p>
 * <p>Copyright: Copyright (c) 2025-2008</p>
 * <p>Company: raddev.cn</p>
 * @author  ooder
 * @version 2.0
 */
public class PersonPrivateGroupNotFoundException extends Exception {
	private Throwable nestedThrowable = null;

	public PersonPrivateGroupNotFoundException() {
		super();
	}

	public PersonPrivateGroupNotFoundException(String msg) {
		super(msg);
	}

	public PersonPrivateGroupNotFoundException(Throwable nestedThrowable) {
		this.nestedThrowable = nestedThrowable;
	}

	public PersonPrivateGroupNotFoundException(String msg, Throwable nestedThrowable) {
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