/**
 * $RCSfile: ChromeProxy.java,v $
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

import java.net.URL;

public interface ChromeProxy {


    public void sendDownLoadCommand(String path);

    public void printLog(String msg, boolean ismsg);

    public void printError(String msg);

    public void printWarn(String msg);

    public void sendMsg(String msg);

    public void execScript(String script);

    public void screenModule(String projectVersionName, String className, Integer deploy);

    public void screenPage(URL url, Integer deploy);

}
