package net.ooder.vfs.sync;

import  net.ooder.common.JDSConstants;
import  net.ooder.common.JDSException;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.common.md5.MD5;
import  net.ooder.common.util.IOUtility;
import  net.ooder.common.util.StringUtility;
import  net.ooder.vfs.FileInfo;
import  net.ooder.vfs.Folder;
import  net.ooder.vfs.ct.CtVfsFactory;
import  net.ooder.vfs.ct.CtVfsService;
import  net.ooder.web.RemoteConnectionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class SyncLocal {
    private Path localDiskPath;
    private String vfsPath;
    private long syncDelayTime = 0;
    private int maxTaskSize = 50;
    private static String JDSFOLDERJSONNAME = "JDS_FOLDER_JSON.json";
    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, SyncLocal.class);
    List<String> errorPaths = new ArrayList<String>();
    List<String> updatePaths = new ArrayList<String>();

    SyncLocal(Path localDiskPath, String vfsPath) {
        this.vfsPath = formartPath(vfsPath, false);
        this.localDiskPath = localDiskPath;
    }

    SyncLocal(Path localDiskPath) {
        String localRootPath = SyncFactory.getInstance().getLocalRootPath();
        String vfsRootPath = SyncFactory.getInstance().getVfsRootPath();
        this.vfsPath = StringUtility.replace(formartPath(localDiskPath.toFile().getAbsolutePath(), false), formartPath(localRootPath, false), formartPath(vfsRootPath, false));
        this.localDiskPath = localDiskPath;
    }

    SyncLocal(Path localDiskPath, String vfsPath, int maxTaskSize) {
        this.vfsPath = formartPath(vfsPath, false);
        this.localDiskPath = localDiskPath;
        this.maxTaskSize = maxTaskSize;
    }

    SyncLocal(Path localDiskPath, String vfsPath, long syncDelayTime, int maxTaskSize) {
        this.syncDelayTime = syncDelayTime;
        this.maxTaskSize = maxTaskSize;
        this.vfsPath = formartPath(vfsPath, false);
        this.localDiskPath = localDiskPath;
    }


    SyncLocal(Path localDiskPath, long syncDelayTime, int maxTaskSize) {

        this.syncDelayTime = syncDelayTime;
        this.maxTaskSize = maxTaskSize;
        String localRootPath = SyncFactory.getInstance().getLocalRootPath();
        String vfsRootPath = SyncFactory.getInstance().getVfsRootPath();
        this.vfsPath = StringUtility.replace(formartPath(localDiskPath.toFile().getAbsolutePath(), false), formartPath(localRootPath, false), formartPath(vfsRootPath, false));
        this.localDiskPath = localDiskPath;

    }


    /**
     * 获取本地索引地址
     *
     * @param
     * @return
     */
    private void initPath(Path path) {
        if (Files.notExists(path)) {
            try {
                path = Files.createDirectories(path);
            } catch (IOException e) {
                logger.error("Failed to create directory: " + path, e);
            }
        }

    }


    void syncFolder() {
        logger.info("start syncFolder vfspaht=" + vfsPath);
        Folder rootFolder = null;
        try {
            rootFolder = getVfsService().getFolderByPath(vfsPath);
            List<Folder> folders = getVfsService().getFolderById(rootFolder.getID()).getChildrenRecursivelyList();
            for (Folder folder : folders) {
                String localPath = formartPath(localDiskPath.toAbsolutePath().toString(), false);
                String cpath = localPath + formartPath(folder.getPath(), false);
                if (localPath.indexOf("/root/") > -1) {
                    cpath = localPath.substring(0, localPath.indexOf("root/")) + formartPath(folder.getPath(), false);
                } else if (localPath.indexOf("/form/") > -1) {
                    cpath = localPath.substring(0, localPath.indexOf("form/")) + formartPath(folder.getPath(), false);
                }
                Path path = Paths.get(cpath);
                try {
                    Files.createDirectories(path);
//                    String json = JSONObject.toJSONString(folder, true);
//                    InputStream input = new ByteArrayInputStream(json.getBytes(VFSConstants.Default_Encoding));
//                    File localFile = path.resolve(JDSFOLDERJSONNAME).toFile();
//                    this.copyStreamToFile(input, localFile);

                } catch (IOException e) {
                    logger.error(" syncFolder  error cpath=" + cpath, e);
                }


            }
        } catch (JDSException e) {
            logger.error("Failed to sync folder: " + vfsPath, e);
        }


    }

    private void copyStreamToFile(InputStream input, File file) throws IOException {
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (file.exists() && !file.canWrite()) {
            final String message = "Unable to open file " + file + " for writing.";
            throw new IOException(message);
        }
        final FileOutputStream output = new FileOutputStream(file);
        IOUtility.copy(input, output);
        IOUtility.shutdownStream(input);
        IOUtility.shutdownStream(output);
    }


    class DowloadTaskList<T extends List<TaskResult<FileInfo>>> implements Callable<T> {

        private final FileInfo[] vfsfiles;

        private int maxTaskSize = 50;

        DowloadTaskList(FileInfo[] vfsfiles, int maxTaskSize) {
            this.vfsfiles = vfsfiles;
            this.maxTaskSize = maxTaskSize;
        }

        @Override
        public T call() {
            List<DowloadTask<TaskResult<FileInfo>>> tasks = new ArrayList<DowloadTask<TaskResult<FileInfo>>>();
            for (FileInfo vfsfile : vfsfiles) {
                DowloadTask<TaskResult<FileInfo>> task = new DowloadTask<TaskResult<FileInfo>>(vfsfile);
                tasks.add(task);
            }
            RemoteConnectionManager.initConnection("localSync", this.maxTaskSize);
            List<TaskResult<FileInfo>> resultList = new ArrayList<>();
            List<Future<TaskResult<FileInfo>>> results = null;
            try {
                results = RemoteConnectionManager.getConntctionService("localSync").invokeAll(tasks);
                for (Future<TaskResult<FileInfo>> resultFuture : results) {
                    TaskResult<FileInfo> result = null;
                    result = resultFuture.get();
                    resultList.add(result);
                }

            } catch (Exception e) {
                logger.error("Failed to execute download tasks", e);
            }
            return (T) resultList;
        }

    }


    class DowloadTask<T extends TaskResult> implements Callable<T> {

        private final FileInfo vfsfile;

        DowloadTask(FileInfo vfsfile) {
            this.vfsfile = vfsfile;
        }

        @Override
        public T call() {
            TaskResult<FileInfo> resultModel = new TaskResult<FileInfo>();

            resultModel.setData(vfsfile);

            if (vfsfile == null) {
                resultModel.setResult(-1);
                return (T) resultModel;
            }

            boolean isSuccess = true;
            try {

                String localPath = formartPath(localDiskPath.toAbsolutePath().toString(), false);

                String fpath = localPath + formartPath(vfsfile.getPath(), false);
                if (localPath.indexOf("/root/") > -1) {
                    fpath = localPath.substring(0, localPath.indexOf("root/")) + formartPath(vfsfile.getPath(), false);
                } else if (localPath.indexOf("/form/") > -1) {
                    fpath = localPath.substring(0, localPath.indexOf("form/")) + formartPath(vfsfile.getPath(), false);
                }
                if (Paths.get(fpath) == null) {
                    File file = new File(fpath);
                    if (file.getParentFile() != null && !file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    file.mkdir();
                }

                if (vfsfile.getCurrentVersion() != null && vfsfile.getCurrentVersion().getLength() > 0) {
                    try {
                        File localFile = Paths.get(fpath).toFile();
                        Long currTime = System.currentTimeMillis();
                        if (!localFile.exists()) {
                            InputStream stream = getVfsService().downLoad(vfsfile.getPath());
                            logger.info("开始下载 文件地址地址：" + vfsfile.getPath() + " 文件大小：" + vfsfile.getCurrentVersion().getLength() + " 用时:" + (System.currentTimeMillis() - currTime));
                            if (stream != null) {
                                copyStreamToFile(stream, localFile);
                                resultModel.setResult(1);
                            } else {
                                resultModel.setDes("发现文件错误，path " + vfsfile.getPath());
                                resultModel.setResult(-1);
                                logger.error("发现文件错误，path=" + vfsfile.getPath());
                            }
                        } else {
                            String md5 = MD5.getHashString(localFile);
                            if (!md5.equals(vfsfile.getCurrentVersonFileHash())) {
                                copyStreamToFile(getVfsService().downLoad(vfsfile.getPath()), localFile);
                                logger.info("服务器文件更新 正在更新本地文件\"【" + vfsfile.getPath() + "】 文件大小：" + vfsfile.getCurrentVersion().getLength() + " 用时:" + (System.currentTimeMillis() - currTime));
                                resultModel.setResult(1);
                            }
                        }

                    } catch (ExecutionException | IOException e) {
                        logger.error("下载文件失败: " + vfsfile.getPath(), e);
                        resultModel.setDes("下载错误：" + vfsfile.getPath());
                        resultModel.setResult(-1);
                        logger.error("下载错误：" + vfsfile.getPath());
                    }
                } else {
                    resultModel.setDes("服务器版本错误：" + vfsfile.getPath());
                    resultModel.setResult(2);
                    //logger.error("服务器版本错误：" + vfsfile.getPath());
                }

            } catch (Throwable e) {
                logger.error("Download task error", e);
                resultModel.setResult(-1);
            }

            return (T) resultModel;
        }
    }


    void syncFile() throws IOException, InterruptedException, ExecutionException {
        CtVfsService service = this.getVfsService();
        Folder rootFolder = service.getFolderByPath(vfsPath);
        List<FileInfo> vfsfiles = getVfsService().getFolderById(rootFolder.getID()).getFileListRecursively();

        long time = System.currentTimeMillis();
        int k = 0;

        List<String> errorPaths = new ArrayList<String>();
        List<String> updatePaths = new ArrayList<String>();

        logger.info("开始 同步文件 共：" + vfsfiles.size());


        int page = 0;
        int start = 0;
        int size = vfsfiles.size();
        while (page * maxTaskSize < size) {
            page++;
        }
        List<DowloadTaskList<List<TaskResult<FileInfo>>>> tasks = new ArrayList<DowloadTaskList<List<TaskResult<FileInfo>>>>();
        for (int kk = 0; k < page; k++) {
            int end = start + maxTaskSize;
            if (end >= size) {
                end = size;
            }
            FileInfo[] loadFiles = Arrays.copyOfRange(vfsfiles.toArray(new FileInfo[]{}), start, start + maxTaskSize);
            tasks.add(new DowloadTaskList<List<TaskResult<FileInfo>>>(loadFiles, maxTaskSize));
            start = end;
        }

        for (DowloadTaskList<List<TaskResult<FileInfo>>> task : tasks) {
            List<TaskResult<FileInfo>> results = task.call();
            for (TaskResult<FileInfo> taskResult : results) {
                if (taskResult.getResult() == 1) {

                    updatePaths.add(taskResult.getData().getPath());
                } else {
                    if (taskResult.getData()!=null){
                        errorPaths.add(taskResult.getData().getPath());
                    }


                }
            }
        }

        int ff = 0;
        while (ff < 5) {
            ff = ff + 1;
            logger.info("");
        }

        logger.info("耗时：" + (System.currentTimeMillis() - time) + "ms");

        logger.info("下载完毕正在统计结果");

        int kk = 0;
        while (kk < 20) {
            Thread.sleep(500);
            kk = kk + 1;
            logger.info("..");
        }


        logger.info("下载结束：共计" + vfsfiles.size() + "个  成功更新：" + updatePaths.size() + "个 失败：" + errorPaths.size() + "个");

        for (String path : errorPaths) {
            logger.info("错误文件： " + path);
        }

    }


    public void downLoad() {
        try {
            syncFolder();
            syncFile();

        } catch (Exception e) {
            logger.error("Download failed", e);
        }

    }

    static List<UPLoadTask<TaskResult<String>>> tasks = new ArrayList<UPLoadTask<TaskResult<String>>>();


    public static int tasksSize = 0;

    public static int updatetasksSize = 0;


    public void importAll() throws IOException {
        tasksSize = 0;
        updatetasksSize = 0;
        List<ImportTask<Path>> imptasks = new ArrayList<ImportTask<Path>>();
        //
        if (maxTaskSize > 0) {
            RemoteConnectionManager.initConnection("localSync", maxTaskSize);
            logger.info("maxTaskSize=" + maxTaskSize);
        }
        logger.info("localDiskPath=" + localDiskPath);

        long time = System.currentTimeMillis();
        int k = 0;

        logger.info("开始运算文件.....  ");
        if (!localDiskPath.toFile().getName().equals("root") && !localDiskPath.toFile().getName().equals("from")) {
            throw new IOException("path mast be root start");
        } else {
            File[] files = localDiskPath.toFile().listFiles();

            for (File file : files) {
                if (!file.isFile()) {
                    ScheduledExecutorService service = SyncFactory.getLocalService(file.toPath());
                    imptasks.add(new ImportTask(file, maxTaskSize, tasks));

                }
            }

            try {

                List<Future<Path>> futures = RemoteConnectionManager.getConntctionService("importAll").invokeAll(imptasks);
                for (Future<Path> resultFuture : futures) {
                    try {
                        Path result = resultFuture.get();
                    } catch (InterruptedException e) {
                        logger.error("Import task interrupted", e);
                    } catch (ExecutionException e) {
                        logger.error("Import task execution failed", e);
                    }

                }
            } catch (InterruptedException e) {
                logger.error("Import all interrupted", e);
            }
            tasksSize = tasks.size();
            logger.info("文件运算完毕   ");
            logger.info("耗时：" + (System.currentTimeMillis() - time) + "ms");
            logger.info("共计" + tasksSize + "个  需要对比！");
            logger.info("准备开始更新...");

            List<Future<TaskResult<String>>> results = null;
            try {
                results = RemoteConnectionManager.getConntctionService("localSync").invokeAll(tasks);
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

            } catch (Exception e) {
                logger.error("Upload failed", e);
            }

            int ff = 0;
            while (ff < 5) {
                ff = ff + 1;
                logger.info("");
            }


            logger.info("耗时：" + (System.currentTimeMillis() - time) + "ms");
            logger.info("上传完毕正在统计结果");
            int kk = 0;
            while (kk < 10) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    logger.error("Thread interrupted", e);
                }
                kk = kk + 1;
                logger.info("..");
            }


            logger.info("上传结束：共计" + tasks.size() + "个  成功更新：" + updatePaths.size() + "个 失败：" + errorPaths.size() + "个");

            for (String path : errorPaths) {
                logger.info(" 错误文件地址：" + path);
            }
        }


    }


    public ScheduledFuture<Path> push() throws IOException {
        ScheduledExecutorService service = SyncFactory.getLocalService(this.localDiskPath);
        final LocalFileVisitor visitor = new LocalFileVisitor(vfsPath, localDiskPath, syncDelayTime, maxTaskSize, tasks);
        if (maxTaskSize > 0) {
            RemoteConnectionManager.initConnection("localSync", maxTaskSize);
            logger.info("maxTaskSize=" + maxTaskSize);
        }
        logger.info("localDiskPath=" + localDiskPath);


        ScheduledFuture<Path> scheduledService = service.schedule(new Callable<Path>() {
            @Override
            public Path call() {

                int k = 0;
                long time = System.currentTimeMillis();
                Path vpath = null;
                try {
                    vpath = Files.walkFileTree(localDiskPath, visitor);
                } catch (IOException e) {
                    logger.error("Failed to walk file tree: " + localDiskPath, e);
                }

                tasksSize = tasks.size();
                List<Future<TaskResult<String>>> results = null;
                try {
                    results = RemoteConnectionManager.getConntctionService("localSync").invokeAll(tasks);
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


                    int ff = 0;
                    while (ff < 5) {
                        ff = ff + 1;
                        logger.info("");
                    }


                    logger.info("耗时：" + (System.currentTimeMillis() - time) + "ms");
                    logger.info("上传完毕正在统计结果");
                    int kk = 0;
                    while (kk < 20) {
                        Thread.sleep(500);
                        kk = kk + 1;
                        logger.info("..");
                    }


                    logger.info("上传结束：共计" + tasks.size() + "个  成功更新：" + updatePaths.size() + "个 失败：" + errorPaths.size() + "个");

                    for (String path : errorPaths) {
                        logger.info(" 错误文件地址：" + path);
                    }
                } catch (Exception e) {
                    logger.error("Upload task error", e);
                }


                return vpath;
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
    public static String formartPath(String path, boolean idfile) {

        path = path.replaceAll("\\\\", "/");
        path = path.replaceAll("//", "/");

        if (!path.endsWith("/") && path.indexOf(".") == -1 && !idfile) {
            path = path + "/";
        }

        return path;
    }


//    class ImportTask<T extends Path>  implements Callable<T>{
//
//        private final File file;
//
//        ImportTask(File file) {
//
//            this.file = file;
//        }
//
//        @Override
//        public T call() {
//            Path vpath = null;
//            try {
//                LocalFileVisitor visitor = new LocalFileVisitor("root/" + file.getName(), file.toPath(), syncDelayTime, maxTaskSize, tasks);
//                vpath = Files.walkFileTree(file.toPath(), visitor);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return vpath;
//        }
//    }

    public static void main(String[] args) throws IOException {


    }

}
