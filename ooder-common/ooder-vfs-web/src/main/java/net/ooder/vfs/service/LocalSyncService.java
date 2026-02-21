package net.ooder.vfs.service;

import java.io.IOException;
import java.nio.file.Path;

import  net.ooder.common.md5.MD5InputStream;
import  net.ooder.config.ResultModel;
import  net.ooder.vfs.FileInfo;
import  net.ooder.vfs.Folder;

public interface LocalSyncService {

    /**
     * 创建文件夹
     * 
     * @param path
     * @return
     */
    public ResultModel<Folder> mkDir(String path) ;

    /**
     * 创建文件
     * 
     * @param path
     * @return
     */
    public ResultModel<FileInfo> creatFile(String path);

    /**
     * 获取文件夹地址
     * 
     * @param path
     * @return
     */
    public ResultModel<Folder> getFolderByPath(String path);

    /**
     * 获取文件
     * 
     * @param path
     * @return
     */
    public ResultModel<FileInfo> getFileByPath(String path);

    /**
     * 删除文件
     * 
     * @param path
     * @return
     */
    public ResultModel<Integer> delete(String path);

    /**
     * 上传文件
     * 
     * @param path
     * @param in
     */
    public ResultModel<Boolean> upload(String path, MD5InputStream in,String userId);

    /**
     * 强制覆盖本地文件
     * 
     * @param path
     * @param vfsPaht
     * @throws IOException
     */
    public ResultModel<Boolean>  downLoad(Path path, String vfsPaht) ;

    /**
     * 差异性上传
     * 
     * @param path
     * @param vfsPaht
     * @throws IOException
     */
    public ResultModel<Boolean>  syncUpload(Path path, String vfsPaht) ;

}
