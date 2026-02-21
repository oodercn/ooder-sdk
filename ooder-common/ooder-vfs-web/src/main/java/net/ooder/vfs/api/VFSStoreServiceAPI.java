package net.ooder.vfs.api;

import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.annotation.MethodChinaName;
import  net.ooder.common.ContextType;
import  net.ooder.common.TokenType;
import  net.ooder.config.ListResultModel;
import  net.ooder.config.ResultModel;
import  net.ooder.jds.core.esb.EsbUtil;
import  net.ooder.vfs.FileObject;
import  net.ooder.vfs.adapter.FileAdapter;
import  net.ooder.vfs.service.VFSStoreService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Controller
@RequestMapping("/api/vfs/store/")
@MethodChinaName(cname = "分布式存储服务")
@EsbBeanAnnotation(dataType = ContextType.Server, tokenType = TokenType.admin)
public class VFSStoreServiceAPI implements VFSStoreService {


    @Override
    @RequestMapping(method = RequestMethod.POST, value = "GetFileObjectByHash")
    @MethodChinaName(cname = "获取HASH映射")
    public @ResponseBody
    ResultModel<FileObject> getFileObjectByHash(String hash) {
        return getVfsService().getFileObjectByHash(hash);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "WriteLine")
    @MethodChinaName(cname = "增量写入")
    public @ResponseBody
    ResultModel<Integer> writeLine(String fileObjectId, String json) {
        return getVfsService().writeLine(fileObjectId, json);
    }


    @Override
    public ResultModel<List<String>> readLine(String fileObjectId, List<Integer> lines) {
        //暂不支持远程读取
        return null;
    }


    @Override
    @RequestMapping(method = RequestMethod.POST, value = "downLoadByHash")
    @MethodChinaName(cname = "下载")
    public ResultModel<InputStream> downLoadByHash(String hash) {
        return getVfsService().downLoadByHash(hash);
    }

//    @Override
//    @RequestMapping(method = RequestMethod.POST, value = "/downLoadByObjectId")
//    public ResultModel<InputStream> downLoadByObjectId(String objectId) {
//        return getVfsService().downLoadByObjectId(objectId);
//    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "GetFileAdapter")
    @MethodChinaName(cname = "文件适配器")
    public @ResponseBody
    ResultModel<FileAdapter> getFileAdapter() {
        return getVfsService().getFileAdapter();
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "DeleteFileObject")
    @MethodChinaName(cname = "删除HASH")
    public @ResponseBody
    ResultModel<Boolean> deleteFileObject(String ID) {
        return getVfsService().deleteFileObject(ID);
    }

    @Override
    @MethodChinaName(cname = "更新HASH信息")
    @RequestMapping(method = RequestMethod.POST, value = "updateFileObject")
    public @ResponseBody
    ResultModel<Boolean> updateFileObject(@RequestBody FileObject fileObject) {
        return getVfsService().updateFileObject(fileObject);
    }

    @Override
    @MethodChinaName(cname = "获取文件信息")
    @RequestMapping(method = RequestMethod.POST, value = "GetFileObjectByID")
    public @ResponseBody
    ResultModel<FileObject> getFileObjectByID(String id) {
        return getVfsService().getFileObjectByID(id);
    }

    @Override
    @MethodChinaName(cname = "创建文件对象")
    @RequestMapping(method = RequestMethod.POST, value = "createFileObject")
    public @ResponseBody
    ResultModel<FileObject> createFileObject(@RequestParam("file") MultipartFile file) {
        return getVfsService().createFileObject(file);
    }


    @Override
    @MethodChinaName(cname = "装载文件信息")
    @RequestMapping(method = RequestMethod.POST, value = "loadFileObjectList")
    public @ResponseBody
    ListResultModel<List<FileObject>> loadFileObjectList(@RequestBody String[] ids) {
        return getVfsService().loadFileObjectList(ids);
    }

    public VFSStoreService getVfsService() {
        return (VFSStoreService) EsbUtil.parExpression(VFSStoreService.class);
    }

}
