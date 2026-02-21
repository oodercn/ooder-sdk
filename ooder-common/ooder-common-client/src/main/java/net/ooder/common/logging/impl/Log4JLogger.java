/**
 * $RCSfile: Log4JLogger.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:25:46 $
 * <p>
 * Copyright (C) 2003 spk, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of spk, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: Log4JLogger.java,v $
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
package net.ooder.common.logging.impl;

//import org.apache.log4j.Logger;
//import org.apache.log4j.Priority;

import net.ooder.common.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Title: 常用代码打包</p>
 * <p>Description:
 * Implementation of {@link Log} that maps directly to a Log4J
 * <strong>Logger</strong>.  Initial configuration of the corresponding
 * Logger instances should be done in the usual manner, as outlined in
 * the Log4J documentation.
 * </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: raddev.cn</p>
 *
 * @author wenzhang li
 * @version 1.0
 */
public final class Log4JLogger implements Log {


    // ------------------------------------------------------------- Attributes

    /**
     * The fully qualified name of the Log4JLogger class.
     */
    private static final String FQCN = Log4JLogger.class.getName();

    /**
     * Log to this logger
     */
    private Logger logger = null;


    // ------------------------------------------------------------ Constructor

    public Log4JLogger() {
    }


    /**
     * Base constructor
     */
    public Log4JLogger(String name) {
        this.logger = LoggerFactory.getLogger(name);
    }

    /**
     * For use with a log4j factory
     */
    public Log4JLogger(Logger logger) {
        this.logger = logger;
    }


    // ---------------------------------------------------------- Implmentation


    /**
     * Log a message to the Log4j Logger with <code>TRACE</code> priority.
     * Currently logs to <code>DEBUG</code> level in Log4J.
     */
    public void trace(Object message) {
        logger.trace("{}", message, null);
    }


    /**
     * Log an error to the Log4j Logger with <code>TRACE</code> priority.
     * Currently logs to <code>DEBUG</code> level in Log4J.
     */
    public void trace(Object message, Throwable t) {
        logger.trace("{}", message, t);
    }


    /**
     * Log a message to the Log4j Logger with <code>DEBUG</code> priority.
     */
    public void debug(Object message) {
        logger.debug("{}", message, null);
    }

    /**
     * Log an error to the Log4j Logger with <code>DEBUG</code> priority.
     */
    public void debug(Object message, Throwable t) {
        logger.debug("{}", message, t);
    }


    /**
     * Log a message to the Log4j Logger with <code>INFO</code> priority.
     */
    public void info(Object message) {
        logger.info("{}", message, null);
    }


    /**
     * Log an error to the Log4j Logger with <code>INFO</code> priority.
     */
    public void info(Object message, Throwable t) {
        logger.info("{}", message, t);
    }


    /**
     * Log a message to the Log4j Logger with <code>WARN</code> priority.
     */
    public void warn(Object message) {
        logger.warn("{}", message, null);
    }


    /**
     * Log an error to the Log4j Logger with <code>WARN</code> priority.
     */
    public void warn(Object message, Throwable t) {
        logger.warn("{},Stack:{}", message, t);
    }


    /**
     * Log a message to the Log4j Logger with <code>ERROR</code> priority.
     */
    public void error(Object message) {
        logger.error("CommonError:{}", message);
    }


    /**
     * Log an error to the Log4j Logger with <code>ERROR</code> priority.
     */
    public void error(Object message, Throwable t) {
        logger.error("CommonError:{}", message, t);
    }


    /**
     * Log a message to the Log4j Logger with <code>FATAL</code> priority.
     */
    public void fatal(Object message) {
        logger.error("FatalError:{}", message);
    }


    /**
     * Log an error to the Log4j Logger with <code>FATAL</code> priority.
     */
    public void fatal(Object message, Throwable t) {
        logger.error(FQCN, message, t);
    }


    /**
     * Return the native Logger instance we are using.
     */
    public Logger getLogger() {
        return (this.logger);
    }


    /**
     * Check whether the Log4j Logger used is enabled for <code>DEBUG</code> priority.
     */
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }


    /**
     * Check whether the Log4j Logger used is enabled for <code>ERROR</code> priority.
     */
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }


    /**
     * Check whether the Log4j Logger used is enabled for <code>FATAL</code> priority.
     */
    public boolean isFatalEnabled() {
        return logger.isErrorEnabled();
    }


    /**
     * Check whether the Log4j Logger used is enabled for <code>INFO</code> priority.
     */
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }


    /**
     * Check whether the Log4j Logger used is enabled for <code>TRACE</code> priority.
     * For Log4J, this returns the value of <code>isDebugEnabled()</code>
     */
    public boolean isTraceEnabled() {
        return logger.isDebugEnabled();
    }

    /**
     * Check whether the Log4j Logger used is enabled for <code>WARN</code> priority.
     */
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }
}
