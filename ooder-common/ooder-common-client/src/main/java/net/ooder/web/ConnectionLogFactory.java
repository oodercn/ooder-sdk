/**
 * $RCSfile: ConnectionLogFactory.java,v $
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
package net.ooder.web;

import net.ooder.common.JDSConstants;
import net.ooder.common.cache.Cache;
import net.ooder.common.cache.CacheManagerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConnectionLogFactory {

    boolean isLogger = true;
    public static final String THREAD_LOCK = "Thread Lock";

    Cache<String, RuntimeLog> logMap = CacheManagerFactory.createCache(JDSConstants.ORGCONFIG_KEY, "Connetciontlog", 30 * 1024 * 1024, 60 * 60 * 1000);
    Cache<String, Set<String>> urllogListMap = CacheManagerFactory.createCache(JDSConstants.LOGCONFIG_KEY, "UrllogSet", 30 * 1024 * 1024, 60 * 60 * 1000);
    static ConnectionLogFactory instance;

    ConnectionLogFactory() {

    }


    public static ConnectionLogFactory getInstance() {
        if (instance == null) {
            synchronized (THREAD_LOCK) {
                if (instance == null) {
                    instance = new ConnectionLogFactory();
                }
            }
        }
        return instance;
    }

    public RuntimeLog getLog(String logId) {
        RuntimeLog log = logMap.get(logId);
        return log;
    }

    public List<RuntimeLog> findLogs(String urlPattern, String bodyPattern, String sessionId, long time) {
        List<RuntimeLog> logs = new ArrayList<RuntimeLog>();
        if (urlPattern == null || urlPattern.equals("")) {
            urlPattern = ".*";
        }
        Pattern p = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
        Set<String> keySet = urllogListMap.keySet();
        for (String key : keySet) {
            Matcher matcher = p.matcher(key);
            if (matcher.find()) {
                logs.addAll(getLogs(key, bodyPattern, sessionId, time));
            }
        }

        Collections.sort(logs, new Comparator<RuntimeLog>() {
            public int compare(RuntimeLog o1, RuntimeLog o2) {
                //TimeSort bug
                if (o1.getStartTime() == o2.getStartTime()) {
                    return 0;
                }
                return o1.getStartTime() > o2.getStartTime() ? -1 : 1;
            }
        });
        return logs;
    }


    Set<RuntimeLog> getLogs(String url, String pattern, String sessionId, long time) {
        Set<RuntimeLog> logs = new LinkedHashSet<>();
        Set<String> logSet = urllogListMap.get(url);
        if (pattern == null || pattern.equals("")) {
            pattern = ".*";
        }

        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        for (String logid : logSet) {
            RuntimeLog log = logMap.get(logid);
            if (log != null) {
                if (pattern != null && !pattern.equals("")) {
                    Matcher rmatcher = p.matcher(log.getBodyJson() == null ? "" : log.getBodyJson());
                    Matcher bmatcher = p.matcher(log.getRequestJson() == null ? "" : log.getRequestJson());
                    if (sessionId == null || sessionId.equals("") || (log.getSessionId() != null && sessionId != null && sessionId.equals(log.getSessionId()))) {
                        if ((rmatcher.find() || bmatcher.find()) && (log.getTime() >= time || log.getTime() < 0)) {
                            logs.add(log);
                        }
                    }
                } else {
                    logs.add(log);
                }

            }
        }

        return logs;
    }


    public RuntimeLog createLog(String logid, String serviceKey, String url, String sessionId) {
        String key = serviceKey + url;
        // synchronized (key) {
        RuntimeLog log = logMap.get(logid);
        if (log == null) {
            log = new RuntimeLog(logid, serviceKey, url, sessionId);
            logMap.put(logid, log);
        }

        Set<String> logSet = urllogListMap.get(key);
        if (logSet == null) {
            logSet = new LinkedHashSet<String>();
            urllogListMap.put(key, logSet);
        }
        logSet.add(logid);
        return log;
        //   }

    }


    public void clear(String url) {
        Set<String> logSet = urllogListMap.get(url);
        if (logSet != null && logSet.size() > 0) {
            for (String logid : logSet) {
                RuntimeLog log = logMap.remove(logid);
            }
            urllogListMap.remove(url);
        }

    }

}
