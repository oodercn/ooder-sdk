/**
 * $RCSfile: DAOException.java,v $
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
package net.ooder.common.database.dao;


import java.util.concurrent.ExecutionException;


public class DAOException extends ExecutionException {

    public DAOException() {
        super();
    }

    public DAOException(Throwable e) {
        super(e);
    }

    public DAOException(String msg) {
        super(msg);
    }

    public DAOException(String msg, Throwable e) {
        super(msg);

    }


}

