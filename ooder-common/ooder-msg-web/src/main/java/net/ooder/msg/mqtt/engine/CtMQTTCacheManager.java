/**
 * $RCSfile: CtMQTTCacheManager.java,v $
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
package net.ooder.msg.mqtt.engine;

import com.alibaba.fastjson.JSONObject;
import net.ooder.common.JDSConstants;
import net.ooder.common.JDSException;
import net.ooder.common.cache.Cache;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.msg.CommandMsg;
import net.ooder.msg.MsgClient;
import net.ooder.msg.MsgFactroy;
import net.ooder.msg.mqtt.command.MQTTCommand;
import net.ooder.msg.mqtt.enums.MQTTCommandEnums;
import net.ooder.org.conf.OrgConstants;

public class CtMQTTCacheManager {

    private static CtMQTTCacheManager cacheManager;

    private static final Log log = LogFactory.getLog(OrgConstants.CONFIG_KEY.getType(), CtMQTTCacheManager.class);

    private boolean cacheEnabled = true;

    protected Cache<String, CommandMsg> commandCache;

    public static CtMQTTCacheManager getInstance() {
        if (cacheManager == null) {
            cacheManager = new CtMQTTCacheManager();
        }
        return cacheManager;
    }

    /**
     * Creates a new cache manager.
     */
    public CtMQTTCacheManager() {
        initCache();
    }

    /**
     * Initializes all caches with the correct size and expiration time.
     */
    private void initCache() {

        commandCache = CacheManagerFactory.createCache(JDSConstants.CONFIG_KEY, "CtCommandmsg", 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

    }

    /**
     * @param commandId
     * @return
     * @throws JDSException
     */
    public <T extends MQTTCommand> T getCommand(String commandId) throws JDSException {

        T command = null;
        CommandMsg msg = null;
        MsgClient<CommandMsg> client = MsgFactroy.getInstance().getClient(null, CommandMsg.class);
        if (!cacheEnabled) {
            msg = client.getMsgById(commandId);

        } else { // cache enabled
            msg = this.commandCache.get(commandId);
            if (command == null) {
                msg = client.getMsgById(commandId);
                commandCache.put(commandId, msg);
            } else {
                msg = commandCache.get(commandId);
            }
        }

        if (msg != null) {
            String msgbody = msg.getBody();
            JSONObject jsonobj = JSONObject.parseObject(msgbody);
            command = (T) JSONObject.parseObject(msgbody, MQTTCommandEnums.fromByName(jsonobj.getString("command")).getCommand());
            command.setCommandId(msg.getId());
            command.setResultCode(msg.getResultCode());
        }


        return command;
    }

}


