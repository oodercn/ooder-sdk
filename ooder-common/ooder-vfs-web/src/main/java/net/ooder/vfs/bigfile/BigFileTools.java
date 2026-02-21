package net.ooder.vfs.bigfile;

import com.alibaba.fastjson.JSONObject;
import  net.ooder.common.JDSConstants;
import  net.ooder.common.JDSException;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.common.md5.MD5InputStream;
import  net.ooder.vfs.FileObject;
import  net.ooder.vfs.FileVersion;
import  net.ooder.vfs.FileView;
import  net.ooder.vfs.ct.CtVfsFactory;
import  net.ooder.vfs.ct.CtVfsService;
import  net.ooder.vfs.sync.SyncFactory;
import  net.ooder.vfs.sync.TaskResult;
import  net.ooder.web.RemoteConnectionManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

public class BigFileTools {

    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, BigFileTools.class);


    /**
     * 单线程调用
     *
     * @param localPath 本地文件地址
     * @param vfsPath   上传文件地址
     * @param fileCount 文件分割数量
     * @return
     * @throws IOException
     * @throws JDSException
     */
    public static boolean upload(String localPath, String vfsPath, int fileCount) throws IOException, JDSException {

        FileVersion version = CtVfsFactory.getCtVfsService().getFileVersionByPath(vfsPath);
        //分割文件
        List<String> files = BigFileUtil.splitFile(localPath, fileCount);
        int k = 0;
        for (String filePath : files) {
            k = k + 1;
            try {
                FileInputStream stream = new FileInputStream(filePath);
                FileObject fileObject = CtVfsFactory.getCtVfsService().createFileObject(new MD5InputStream(stream));

                version.createView(fileObject.getID(), k);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    /**
     * 单线程下载
     *
     * @param localPath 本地文件地址
     * @param vfsPath   上传文件地址
     * @return
     * @throws IOException
     * @throws JDSException
     */
    public static File downLoad(String vfsPath, String localPath) throws IOException, JDSException {
        List<String> paths = new ArrayList<>();
        FileVersion version = CtVfsFactory.getCtVfsService().getFileVersionByPath(vfsPath);
        List<FileView> viewList = version.getViews();
        for (FileView view : viewList) {
            view.getFileObject().downLoad();
            String blocklocalPath = view.getFileObject().getPath();
            File file = new File(blocklocalPath);
            if (file.exists()) {
                paths.add(view.getFileObject().getPath());
            }
        }
        if (version.getFileObject() != null) {
            localPath = version.getFileObject().getPath();
        }
        BigFileUtil.mergeFiles(paths, localPath);

        return new File(localPath);

    }


    /**
     * 多线程异步上传
     *
     * @param localPath 本地文件地址
     * @param vfsPath   上传文件地址
     * @param fileCount 文件分割数量
     * @return
     * @throws IOException
     */
    public static boolean syncUpload(String localPath, String vfsPath, String personId, Integer fileCount) throws IOException, ExecutionException, InterruptedException {
        ScheduledExecutorService service = SyncFactory.getServerService(vfsPath);
        List<BigFileLoadBlockTask<TaskResult<String>>> tasks = new ArrayList<BigFileLoadBlockTask<TaskResult<String>>>();
        List<String> errorPaths = new ArrayList<String>();
        List<String> updatePaths = new ArrayList<String>();
        List<String> files = null;
        FileVersion version = CtVfsFactory.getCtVfsService().getFileVersionByPath(vfsPath);


        if (fileCount == null || fileCount < 1) {
            files = BigFileUtil.splitFile(localPath, fileCount);
        } else {
            files = BigFileUtil.splitFile(localPath);
        }
        int k = 0;
        for (String path : files) {
            k = k + 1;
            BigFileLoadBlockTask task = new BigFileLoadBlockTask(Paths.get(path), version.getVersionID(), k);
            tasks.add(task);
        }
        List<Future<TaskResult<String>>> results = null;
        results = RemoteConnectionManager.getConntctionService("BigFileTools").invokeAll(tasks);
        for (Future<TaskResult<String>> resultFuture : results) {
            TaskResult<String> result = resultFuture.get();
            if (result.getResult() == -1) {
                errorPaths.add(result.getData());
            }
            if (result.getResult() == 1) {
                updatePaths.add(result.getData());
            }
        }

        List<FileView> viewList = version.getViews();
        String json = JSONObject.toJSONString(viewList);
        FileObject fileObject = CtVfsFactory.getCtVfsService().createFileObjectAsContent(json);
        //更新分裂信息
        CtVfsFactory.getCtVfsService().updateFileVersionInfo(version.getVersionID(), fileObject.getHash());

        //删除临时文件
        for (String filePath : files) {
            File file = new File(filePath);
            file.delete();
        }
        if (errorPaths.size() > 0) {
            return false;
        }


        return true;
    }


    /**
     * 多线程下载
     *
     * @param localPath 本地文件地址
     * @param vfsPath   上传文件地址
     * @return
     * @throws IOException
     */
    public static File syncDownLoad(String vfsPath, String localPath) throws IOException {
        List<BigFileDownLoadTask<TaskResult<String>>> downtasks = new ArrayList<BigFileDownLoadTask<TaskResult<String>>>();
        ScheduledExecutorService service = SyncFactory.getServerService(vfsPath);
        List<String> errorPaths = new ArrayList<String>();
        List<String> downloadPaths = new ArrayList<String>();
        File tempFile = null;

        try {
            List<String> files = null;
            FileVersion version = CtVfsFactory.getCtVfsService().getFileVersionByPath(vfsPath);
            if (version.getFileObject() != null) {
                tempFile = new File(version.getFileObject().getPath());
            } else {
                tempFile = new File(localPath);
            }

            if (!tempFile.exists()) {
                List<FileView> viewList = version.getViews();
                if (viewList.size() > 1) {
                    for (FileView view : viewList) {
                        BigFileDownLoadTask task = new BigFileDownLoadTask(view);
                        downtasks.add(task);
                    }
                    List<Future<TaskResult<String>>> results = null;
                    results = RemoteConnectionManager.getConntctionService("downLoad").invokeAll(downtasks);
                    for (Future<TaskResult<String>> resultFuture : results) {

                        TaskResult<String> result = resultFuture.get();
                        if (result.getResult() == -1) {
                            errorPaths.add(result.getData());
                        }
                        if (result.getResult() == 1) {
                            downloadPaths.add(result.getData());
                        }
                    }
                    //合并文件
                    BigFileUtil.mergeFiles(downloadPaths, localPath);
                }
            }
        } catch (JDSException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        return new File(localPath);


    }


    public CtVfsService getVfsService() {
        CtVfsService client = CtVfsFactory.getCtVfsService();
        if (client == null) {
            this.logger.error("$VFSClientService  load err");
        }

        return client;

    }


}
