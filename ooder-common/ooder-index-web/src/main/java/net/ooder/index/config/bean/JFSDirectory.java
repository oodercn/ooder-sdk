/**
 * $RCSfile: JFSDirectory.java,v $
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

public interface JFSDirectory extends JIndexBean{

    public Class getFsDirectoryClass();
    
    public void setFsDirectoryClass(Class fsclazz);

    // VFS 云端VFS对应路径
    public String getVfsPath();

    // 临时文件路径
    public String getTempPath();

    public String getPath();
    
    public void setPath(String path);
    
    public void setVfsPath(String vfspath);
    
    public void setTempPath(String temppath);
    
    public JSyncListener getSyncListener() ;

    public void setSyncListener(JSyncListener syncListener);
    
    public long getSyncDelayTime();

    public void setSyncDelayTime(long syncDelayTime) ;

    public int getMaxTaskSize();

    public void setMaxTaskSize(int maxTaskSize) ;


}


