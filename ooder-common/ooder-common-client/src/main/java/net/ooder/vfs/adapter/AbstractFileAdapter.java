/**
 * $RCSfile: AbstractFileAdapter.java,v $
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
package net.ooder.vfs.adapter;

import java.io.InputStream;
import java.util.List;

import net.ooder.common.md5.MD5InputStream;
import net.ooder.common.md5.MD5OutputStream;
import net.ooder.common.util.StringUtility;
import net.ooder.org.conf.OrgConfig;

public abstract class AbstractFileAdapter implements FileAdapter {

    protected static final Integer textBufSize = 1 * 1024 * 1024;

    protected static final String charsetName = "utf-8";

    protected OrgConfig config;

    public String rootPath;


   public AbstractFileAdapter(String rootPath) {
	this.rootPath = rootPath;
    }

    public String getRootPath() {
	String path = null;
	if (rootPath != null) {
	    path = rootPath;
	    
	} else if (config != null) {
	    
	    path = (String) config.getFileAdapterMap().get("serverURL");
	}

	
	return path;
    };

    public abstract void mkdirs(String vfsPath);

    public abstract void delete(String vfsPath);

    public abstract boolean exists(String vfsPath);

    public abstract boolean testConnection(String vfsPath);

    public abstract long write(String vfsPath, MD5InputStream in);

    public abstract long write(String vfsPath, InputStream in);

    public abstract MD5InputStream getMD5InputStream(String vfsPath);

    public abstract InputStream getInputStream(String vfsPath);

    public abstract Integer writeLine(String vfsPath, String str);

    public abstract List<String> readLine(String vfsPath, List<Integer> lineNums);

    public abstract String getMD5Hash(String vfsPath);

    public abstract MD5OutputStream getOutputStream(String vfsPath);

}
