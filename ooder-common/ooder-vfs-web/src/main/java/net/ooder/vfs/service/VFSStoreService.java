package net.ooder.vfs.service;

import  net.ooder.annotation.MethodChinaName;
import  net.ooder.config.ListResultModel;
import  net.ooder.config.ResultModel;
import  net.ooder.vfs.FileObject;
import  net.ooder.vfs.adapter.FileAdapter;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface VFSStoreService {

    @MethodChinaName(cname = "上传实体")
    public ResultModel<FileObject> createFileObject(MultipartFile localMultipartFile);



    @MethodChinaName(cname = "下载实体")
    public ResultModel<InputStream> downLoadByHash(String hash);
//
//    @MethodChinaName(cname = "下载实体")
//    public ResultModel<InputStream> downLoadByObjectId(String objectId);

    @MethodChinaName(cname = "批量装载")
    public ListResultModel<List<FileObject>> loadFileObjectList(String[] ids);

    @MethodChinaName(cname = "获取文件实体")
    public ResultModel<FileObject> getFileObjectByHash(String hash);

    @MethodChinaName(cname = "获取文件实体")
    public ResultModel<FileObject> getFileObjectByID(String id);


    @MethodChinaName(cname = "删除文件实体")
    public ResultModel<Boolean> deleteFileObject(String ID);


    @MethodChinaName(cname = "更新OBJ")
    public ResultModel<Boolean> updateFileObject(FileObject fileObject);

    @MethodChinaName(cname = "获取文件适配器")
    public ResultModel<FileAdapter> getFileAdapter();


    @MethodChinaName(cname = "追加数据")
    public ResultModel<Integer> writeLine(String fileObjectId, String json);


    @MethodChinaName(cname = "追加数据")
    public ResultModel<List<String>> readLine(String fileObjectId, List<Integer> lines);

}
