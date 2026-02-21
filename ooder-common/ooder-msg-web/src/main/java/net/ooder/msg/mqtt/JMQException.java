/**
 * $RCSfile: JMQException.java,v $
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
package net.ooder.msg.mqtt;

import net.ooder.common.JDSException;

import java.io.PrintStream;
import java.io.PrintWriter;

public class JMQException extends JDSException {

    public static final int COMMANDWAIT = 1000;// 1000: 等待执行结果

    public static final int USERNAMEALREADYEXITS = 1001;// 1001: userName already exits(用户已存"

    public static final int USERNAMENOTSTANDARD = 1002;// userName Not standard (用户不规"

    public static final int USERNAMEONTEXITS = 1003;// 1003: userName is not exits (用户不存"

    public static final int PASSWORDERROR = 1004;// 1004: password error (密码错误)

    public static final int PASSWORDFULL = 2009;// 2009: password error (密码)

    public static final int PASSWORDALERADYEXITS = 2010;// 2009: password error (密码)


    public static final int NOTLOGIN = JDSException.NOTLOGINEDERROR;// 2000: not login

    public static final int MOBILENOTSTANDARD = 2001;// Mobile phone number is not standard(手机号码不规"

    public static final int MOBILEALERADYEXITS = 2002;// Mobile phone already exits (手机号码已存"

    public static final int OTHERERROR = 3001;// 3001: other error （其他错误）

    public static final int NOTIMPLEMENTED = 3002;// 3002: Not implemented （其他错误）

    public static final int MAILNOTSTANDARD = 4001;// 4001 mail is not standard(邮箱号码不规"

    public static final int MAILALERADYEXITS = 4002;// mail already exits (邮箱号码已存"

    public static final int SESSIONIDINVALID = 5001;// sessionId is Invalid

    public static final int GETWAYIDEXITS = 6001;// gatewaySerialNumber is exits

    public static final int GETWAYIDINVALID = 6002;// gatewaySerialNumber is Invalid

    public static final int GETWAYOFFINE = 6003;// 网关离线

    public static final int SENSORIDINVALID = 7001;// sensorSerialNumber is Invalid

    public static final int SENSORIDEXITS = 7002;// sensorSerialNumber is exits

    public static final int SENSORCYCLEEXITS = 7005;// SENSORCYCLEEXITS is exits

    public static final int SENSORALARMEXITS = 7006;// SENSORALARMEXITS is exits

    public static final int SENSOROFFINE = 7007;// 传感器离"

    public static final int SENSORFAULT = 7009;// 设备故障

    public static final int PLACEEXITS = 8001;// place is exits

    public static final int AREAEXITS = 8002;// area is exits

    public static final int AREANOTEXITS = 8003;// place not exits

    public static final int PLACENOTEXITS = 8004;// place not is exits!

    public static final int ALARMNOTEXITS = 8012;// ALARM is NOT exits

    public static final int ALARMISEXITS = 8011;// ALARM is exits

    public static final int DEVICENOEXITS = 9001;// 设备不存"

    /**
     * Exception that might have caused this one.
     */
    private Throwable cause;

    /**
     * Exception code that defined in BILL.
     */
    private int errorCode;

    /**
     * Constructs a build exception with no descriptive information.
     */
    public JMQException() {
        super();
    }

    /**
     * Constructs an exception with the given descriptive message and error code.
     *
     * @param message   A description of or information about the exception. Should not be <code>null</code>.
     * @param errorCode Error code defined in BILL.
     */
    public JMQException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructs an exception with the given descriptive message and error code.
     *
     * @param message   A description of or information about the exception. Should not be <code>null</code>.
     * @param cause     The exception that might have caused this one. May be <code>null</code>.
     * @param errorCode Error code defined in BILL.
     */
    public JMQException(String message, Throwable cause, int errorCode) {
        this(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Constructs an exception with the given descriptive message.
     *
     * @param message A description of or information about the exception. Should not be <code>null</code>.
     */
    public JMQException(String message) {
        super(message);
    }

    /**
     * Constructs an exception with the given message and exception as a root cause.
     *
     * @param message A description of or information about the exception. Should not be <code>null</code> unless a cause is
     *                specified.
     * @param cause   The exception that might have caused this one. May be <code>null</code>.
     */
    public JMQException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    /**
     * Constructs an exception with the given exception as a root cause.
     *
     * @param cause The exception that might have caused this one. Should not be <code>null</code>.
     */
    public JMQException(Throwable cause) {
        super(cause.toString());
        this.cause = cause;
    }

    /**
     * Retrieves the BILL exception code for this <code>BILLException</code> object.
     *
     * @return the vendor's error code
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Returns the nested exception, if any.
     *
     * @return the nested exception, or <code>null</code> if no exception is associated with this one
     */
    public Throwable getCause() {
        return cause;
    }

    /**
     * Returns the nested exception, if any.
     *
     * @return the nested exception, or <code>null</code> if no exception is associated with this one
     */
    public Throwable getException() {
        return cause;
    }

    /**
     * Returns the location of the error and the error message.
     *
     * @return the location of the error and the error message
     */
    public String toString() {
        return getMessage();
    }

    /**
     * Prints the stack trace for this exception and any nested exception to <code>System.err</code>.
     */
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    /**
     * Prints the stack trace of this exception and any nested exception to the specified PrintStream.
     *
     * @param ps The PrintStream to print the stack trace to. Must not be <code>null</code>.
     */
    public void printStackTrace(PrintStream ps) {
        synchronized (ps) {
            if (errorCode != 0) {
                ps.println("Error Code: " + errorCode);
            }
            super.printStackTrace(ps);
            if (cause != null) {
                ps.println("--- Nested Exception ---");
                cause.printStackTrace(ps);
            }
        }
    }

    /**
     * Prints the stack trace of this exception and any nested exception to the specified PrintWriter.
     *
     * @param pw The PrintWriter to print the stack trace to. Must not be <code>null</code>.
     */
    public void printStackTrace(PrintWriter pw) {
        synchronized (pw) {
            if (errorCode != 0) {
                pw.println("Error Code: " + errorCode);
            }
            super.printStackTrace(pw);
            if (cause != null) {
                pw.println("--- Nested Exception ---");
                cause.printStackTrace(pw);
            }
        }
    }

}


