/**
 * $RCSfile: FileAdapter.java,v $
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

public interface FileAdapter {

    public String getRootPath();

    public String createFolderPath();

    public void mkdirs(String vfsPath);

    public void delete(String vfsPath);

    public boolean exists(String vfsPath);

    public boolean testConnection(String vfsPath);

    public long write(String vfsPath, MD5InputStream in);

    public long write(String vfsPath, InputStream in);

    public MD5InputStream getMD5InputStream(String vfsPath);

    public InputStream getInputStream(String vfsPath);

    public Integer writeLine(String vfsPath, String str);

    public Long  getLength(String vfsPath);

    public List<String> readLine(String vfsPath, List<Integer> lineNums);

    public String getMD5Hash(String vfsPath);

    public MD5OutputStream getOutputStream(String vfsPath);

}
