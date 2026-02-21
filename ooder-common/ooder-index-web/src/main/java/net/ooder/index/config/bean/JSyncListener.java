/**
 * $RCSfile: JSyncListener.java,v $
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
package net.ooder.index.config.bean;

public interface JSyncListener extends JIndexBean{
    


    public String getVfsPath();

    public void setVfsPath(String vfsPath);

    public String getTempPath() ;

    public void setTempPath(String tempPath);

    public String getVfsRootPath() ;

    public void setVfsRootPath(String vfsRootPath) ;

    public String getTempRootPath() ;

    public void setTempRootPath(String tempRootPath) ;

    public Class getSyncListener() ;

    public void setSyncListener(Class syncListener) ;

}


