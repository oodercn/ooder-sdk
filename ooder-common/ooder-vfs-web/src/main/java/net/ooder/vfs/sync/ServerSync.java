package net.ooder.vfs.sync;

import  net.ooder.common.JDSConstants;
import  net.ooder.common.JDSException;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.vfs.FileInfo;
import  net.ooder.vfs.Folder;
import  net.ooder.vfs.ct.CtVfsFactory;
import  net.ooder.vfs.ct.CtVfsService;
import  net.ooder.web.RemoteConnectionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ServerSync {


    private String vfsPath;

    private String sUrl;
    private String tUrl;
    private long syncDelayTime = 0;

    private int maxTaskSize = 10;

    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, ServerSync.class);

    ServerSync(String sUrl, String tUrl, String vfsPath) {
        this.sUrl = sUrl;
        this.tUrl = tUrl;
        this.vfsPath = vfsPath;
    }

    static List<SyncUPLoadTask<TaskResult<String>>> tasks = new ArrayList<SyncUPLoadTask<TaskResult<String>>>();

    public ScheduledFuture<String> push() throws IOException {
        ScheduledExecutorService service = SyncFactory.getServerService(vfsPath);
        final ServerFileVisitor visitor = new ServerFileVisitor(sUrl, tUrl, vfsPath, syncDelayTime, maxTaskSize, tasks);
        logger.info("vfsPath=" + vfsPath);

        logger.info("start syncFolder vfspaht=" + vfsPath);
        Folder rootFolder = null;
        try {
            rootFolder = getVfsService().getFolderByPath(vfsPath);
            List<Folder> folders = getVfsService().getFolderById(rootFolder.getID()).getChildrenRecursivelyList();
            for (Folder folder : folders) {
                visitor.SynceFolder(folders);
            }
        } catch (JDSException e) {
            e.printStackTrace();
        }


        List<FileInfo> vfsfiles = rootFolder.getFileListRecursively();

        visitor.visitFile(vfsfiles);


        ScheduledFuture<String> scheduledService = service.schedule(new Callable<String>() {
            @Override
            public String call() throws Exception {
                int k = 0;
                List<String> errorPaths = new ArrayList<String>();
                List<String> updatePaths = new ArrayList<String>();
                List<Future<TaskResult<String>>> results = null;
                try {
                    results = RemoteConnectionManager.getConntctionService("SyncUPLoadTask").invokeAll(tasks);
                    for (Future<TaskResult<String>> resultFuture : results) {
                        k = k + 1;
                        TaskResult<String> result = resultFuture.get();
                        if (result.getResult() == -1) {
                            errorPaths.add(result.getData());
                        }
                        if (result.getResult() == 1) {
                            updatePaths.add(result.getData());
                        }
                    }
                    System.out.println("上传结束：共计" + tasks.size() + "个  成功更新：" + updatePaths.size() + "个 失败：" + errorPaths.size() + "个");
                    for (String path : errorPaths) {
                        logger.info(" 错误文件地址：" + path);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return vfsPath;
            }

        }, 0, TimeUnit.MILLISECONDS);
        return scheduledService;
    }


    public CtVfsService getVfsService() {

        CtVfsService client = CtVfsFactory.getCtVfsService();
        if (client == null) {
            this.logger.error("$VFSClientService  load err");
        }

        return client;

    }

    /**
     * 解析path 获取文件夹路径
     *
     * @param path
     * @return
     */
    public static String getFolderPath(String path) {
        path = path.replaceAll("\\\\", "/");
        path = path.replaceAll("//", "/");
        String[] paths = path.split("/");
        String folderPath = "";
        for (int i = 0; i < paths.length - 1; i++) {
            folderPath += paths[i] + "/";
        }
        return folderPath;
    }

    /**
     * 解析path 获取文件夹路径
     *
     * @param path
     * @return
     */
    public static String formartPath(String path) {

        path = path.replaceAll("\\\\", "/");
        path = path.replaceAll("//", "/");

        if (!path.endsWith("/") && path.indexOf(".") == -1) {
            path = path + "/";
        }

        return path;
    }

    public static void main(String[] args) throws IOException {

        String localPaht = "d:/test/root/";
        String vfs = "root/cdiskroot/";
        String root = "d:/test/";

        String localPath = "d:/test/root/";

        String cpath = localPath.substring(0, localPath.indexOf("root/")) + vfs;

        // String cpath = StringUtility.replace(formartPath(localPaht), vfs, formartPath(root));

        System.out.print(cpath);
    }

}
