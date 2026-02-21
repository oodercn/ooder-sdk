/**
 * $RCSfile: LogSetpLog.java,v $
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

import net.ooder.common.JDSConstants;
import net.ooder.web.TaskLogFactory;

import java.net.URL;

public class LogSetpLog implements ChromeProxy {

    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, LogSetpLog.class);

    @Override
    public void sendDownLoadCommand(String path) {
        logger.info(path);
    }

    @Override
    public void printLog(String msg, boolean ismsg) {
        logger.info(msg);
    }

    @Override
    public void printError(String msg) {
        logger.error(msg);
    }

    @Override
    public void printWarn(String msg) {
        logger.warn(msg);
    }

    @Override
    public void sendMsg(String msg) {
        logger.info(msg);
    }

    @Override
    public void execScript(String script) {
        logger.info(script);
    }

    @Override
    public void screenModule(String projectVersionName, String className, Integer deploy) {

    }

    @Override
    public void screenPage(URL url, Integer deploy) {

    }
}
