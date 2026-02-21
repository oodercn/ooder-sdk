/**
 * $RCSfile: VFSFolderNotFoundException.java,v $
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
package net.ooder.vfs;

import java.io.PrintStream;
import java.io.PrintWriter;

public class VFSFolderNotFoundException extends Exception {

	private Throwable nestedThrowable = null;

	public VFSFolderNotFoundException() {
		super();
	}

	public VFSFolderNotFoundException(String msg) {
		super(msg);
	}

	public VFSFolderNotFoundException(Throwable nestedThrowable) {
		this.nestedThrowable = nestedThrowable;
	}

	public VFSFolderNotFoundException(String msg, Throwable nestedThrowable) {
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
