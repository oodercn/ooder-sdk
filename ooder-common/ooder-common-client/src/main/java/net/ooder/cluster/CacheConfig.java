/**
 * $RCSfile: CacheConfig.java,v $
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
package net.ooder.cluster;

import net.ooder.common.CommonConfig;

import java.util.List;

public class CacheConfig {

    private  String configKey;
    private  String cacheName;
    Boolean enable = true;
    long size;
    long lifeTime;
    List<CacheConfig> cacheList;
    CacheConfig(String configKey) {
        this.configKey = configKey;
        loadProperties();
    }
    CacheConfig(String configKey,String cacheName) {
        this.configKey = configKey;
        this.cacheName=cacheName;
        loadProperties();
    }


    private void loadProperties() {

        String enableStr = CommonConfig.getValue(configKey + ".cache.enable");
         String lifeTimeStr = CommonConfig.getValue(configKey + ".cache.lifeTime");
         String sizeStr = CommonConfig.getValue(configKey + ".cache.size");

        if (cacheName!=null){
             enableStr = CommonConfig.getValue(configKey + ".cache."+cacheName+".enable");
            lifeTimeStr = CommonConfig.getValue(configKey + ".cache."+cacheName+".lifeTime");
             sizeStr = CommonConfig.getValue(configKey + ".cache."+cacheName+".size");
        }

        try {
            if (enableStr != null) {
                this.enable = Boolean.valueOf(enableStr);
            }
            if (lifeTimeStr != null) {
                this.lifeTime = Integer.parseInt(lifeTimeStr);
            }
            if (sizeStr != null) {
                this.size = Integer.parseInt(sizeStr);
            }

        } catch (final Exception e) {
            // log.error("Error: could not parse default pool properties. " + "Make sure the values exist and are correct.", e);
            e.printStackTrace();
        }
    }
}
