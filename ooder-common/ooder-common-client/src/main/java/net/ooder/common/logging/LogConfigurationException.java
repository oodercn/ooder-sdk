/**
 * $RCSfile: LogConfigurationException.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:26:06 $
 *
 * Copyright (C) 2003 spk, Inc. All rights reserved.
 *
 * This software is the proprietary information of spk, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: LogConfigurationException.java,v $
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
package net.ooder.common.logging;

/**
 * <p>An exception that is thrown only if a suitable <code>LogFactory</code>
 * or <code>Log</code> instance cannot be created by the corresponding
 * factory methods.</p>
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.1 $ $Date: 2025/07/08 00:26:06 $
 */
/**
 * <p>Title: 常用代码打包</p>
 * <p>Description:
 * An exception that is thrown only if a suitable <code>LogFactory</code>
 * or <code>Log</code> instance cannot be created by the corresponding
 * factory methods.</p> 
 *
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: raddev.cn</p>
 * @author wenzhang li
 * @version 1.0
 */
public class LogConfigurationException extends RuntimeException {


    /**
     * Construct a new exception with <code>null</code> as its detail message.
     */
    public LogConfigurationException() {

        super();

    }


    /**
     * Construct a new exception with the specified detail message.
     *
     * @param message The detail message
     */
    public LogConfigurationException(String message) {

        super(message);

    }


    /**
     * Construct a new exception with the specified cause and a derived
     * detail message.
     *
     * @param cause The underlying cause
     */
    public LogConfigurationException(Throwable cause) {

        this((cause == null) ? null : cause.toString(), cause);

    }


    /**
     * Construct a new exception with the specified detail message and cause.
     *
     * @param message The detail message
     * @param cause The underlying cause
     */
    public LogConfigurationException(String message, Throwable cause) {

        super(message);
        this.cause = cause; // Two-argument version requires JDK 1.4 or later

    }


    /**
     * The underlying cause of this exception.
     */
    protected Throwable cause = null;


    /**
     * Return the underlying cause of this exception (if any).
     */
    public Throwable getCause() {

        return (this.cause);

    }


}
