
/**
 * $RCSfile: ClearSessionCheckTask.java,v $
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
package net.ooder.server;

import net.ooder.common.JDSConstants;
import net.ooder.common.cache.Cache;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.JDSSessionHandle;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ClearSessionCheckTask implements Runnable {

    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, ClearSessionCheckTask.class);

    private long expireTime;

    private String systemCode;

    public ClearSessionCheckTask(long expireTime) {

        this.expireTime = expireTime * 60 * 1000;
        this.expireTime = expireTime;
    }

    public ClearSessionCheckTask(long expireTime, String systemCode) {
        this.expireTime = expireTime * 60 * 1000;
        this.systemCode = systemCode;

    }

    public void run() {
        try {
            if (JDSServer.getInstance().started()) {
                List<JDSSessionHandle> invalidSessionList = new ArrayList<JDSSessionHandle>();
                long currentTime = System.currentTimeMillis();
                // 验证Session是否有需要进行
                Cache<String, JDSSessionHandle> cache = JDSServer.getInstance().getSessionHandleCache();
                Set<String> ites = cache.keySet();
                Integer size = ites.size();
                Thread.currentThread().setName("ClearSessionCheckTask size" + ites.size());
                for (String sessionId : ites) {
                    JDSSessionHandle handle = cache.get(sessionId);
                    if (handle != null) {
                        ConnectInfo connectInfo = JDSServer.getInstance().getConnectInfo(handle);
                        if (connectInfo != null && connectInfo.getLoginName() != null) {
                            Long loginTime = JDSServer.getInstance().getConnectTimeCache().get(handle);
                            if ((currentTime - loginTime.longValue()) > expireTime) {
                                invalidSessionList.add(handle);
                            }
                        }
                    }
                    Thread.currentThread().setName("ClearSessionCheckTask size" + size--);
                }
                JDSServer.getInstance().invalidateSession(invalidSessionList);
            }
        } catch (Exception workflowee) {
            logger.error("checkSession instance failed.", workflowee);
        }

    }

}
