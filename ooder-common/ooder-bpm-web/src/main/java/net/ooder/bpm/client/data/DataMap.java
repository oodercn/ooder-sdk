/**
 * $RCSfile: DataMap.java,v $
 * $Revision: 1.1 $
 * $Date: 2013/05/28 12:16:28 $
 * <p>
 * Copyright (C) 2008 itjds, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.client.data;

import net.ooder.bpm.engine.BPMException;

import java.util.HashMap;

public abstract class DataMap extends HashMap {
    private static final long serialVersionUID = 1L;

    public String userId;

    public Object source;

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public abstract DataMap clone(DataMap source) throws BPMException;

    public abstract <T> T getDAO(String key, Class<T> clazz);


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


}
	
