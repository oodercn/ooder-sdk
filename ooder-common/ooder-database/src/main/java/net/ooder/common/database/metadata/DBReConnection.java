/**
 * $RCSfile: DBReConnection.java,v $
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
package net.ooder.common.database.metadata;

public class DBReConnection implements Runnable {

    MetadataFactory factory;

    DBReConnection(MetadataFactory factory) {
        this.factory = factory;
    }

    @Override
    public void run() {

        factory.connect();
    }
}


