package net.ooder.vfs.service;

import  net.ooder.common.md5.MD5InputStream;
import  net.ooder.config.ResultModel;
import  net.ooder.vfs.FileInfo;
import  net.ooder.vfs.Folder;

public interface UpdateData {

    /**
     *
     * @param userId
     * @param vfsPath
     * @param fileName
     * @param json
     * @return
     */
    public ResultModel<Integer> updateRemote(String userId, String vfsPath, String fileName, String json) ;
    
    
   
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
    public ResultModel<Boolean> delete(String path);

    /**
     * 上传文件
     * 
     * @param path
     * @param in
     */
    public ResultModel<Boolean> upload(String path, MD5InputStream in);

  

}
