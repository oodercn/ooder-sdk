package net.ooder.vfs.sync;

import  net.ooder.common.JDSConstants;
import  net.ooder.common.JDSException;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.context.JDSActionContext;
import  net.ooder.context.JDSContext;
import  net.ooder.server.context.MinServerActionContextImpl;
import  net.ooder.vfs.FileInfo;
import  net.ooder.vfs.Folder;
import  net.ooder.vfs.ct.CtVfsFactory;

import java.io.IOException;
import java.util.List;

public class ServerFileVisitor {

    private String sUrl;
    private String tUrl;
    private String vfsPath;
    private String loaclVfsPath;

    private long syncDelayTime;
    private long defaultDelayTime = 180 * 1000;
    private int maxTaskSize;
    private MinServerActionContextImpl autoruncontext;

    List<SyncUPLoadTask<TaskResult<String>>> tasks;
    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, ServerFileVisitor.class);

    public ServerFileVisitor(String sUrl, String tUrl, String vfsPath, long syncDelayTime, int maxTaskSize, List<SyncUPLoadTask<TaskResult<String>>> tasks) {
        // 延时执行时间
        this.syncDelayTime = syncDelayTime;
        this.maxTaskSize = maxTaskSize;
        this.tasks = tasks;
        this.sUrl = sUrl;
        this.tUrl = tUrl;
        this.vfsPath = vfsPath;
        this.loaclVfsPath = loaclVfsPath;

        JDSContext context = JDSActionContext.getActionContext();
        this.autoruncontext = new MinServerActionContextImpl(context.getHttpRequest(), context.getOgnlContext());
        autoruncontext.setParamMap(context.getContext());
        String sessionId = context.getSessionId();
        if (sessionId != null) {
            autoruncontext.setSessionId(context.getSessionId());
            autoruncontext.getSession().put("sessionHandle", context.getSession().get("sessionHandle"));
            autoruncontext.getContext().put(JDSContext.SYSCODE, autoruncontext.getSystemCode());
            autoruncontext.setSessionMap(context.getSession());

        }
        logger.info(autoruncontext);

    }

    /**
     * 同步对比文件
     *
     * @return
     * @throws IOException
     */

    public void visitFile(List<FileInfo> sfileInfos) throws IOException {
        JDSActionContext.setContext(autoruncontext);

        for (FileInfo sfileInfo : sfileInfos) {

            JDSActionContext.getActionContext().getContext().put("ServerUrl", tUrl);
            try {

                if (sfileInfo != null) {
                    FileInfo tfileInfo = CtVfsFactory.getCtVfsService().createFile(sfileInfo.getFolder().getPath(), sfileInfo.getName());
                    String vfsHash = sfileInfo.getCurrentVersonFileHash();
                    String md5 = tfileInfo.getCurrentVersonFileHash();
                    if (vfsHash == null || !vfsHash.equals(md5)) {
                        SyncUPLoadTask task = new SyncUPLoadTask(sUrl, tUrl, tfileInfo.getPath());
                        tasks.add(task);
                    }
                }


            } catch (JDSException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * 同步文件夹
     *
     * @return
     * @throws IOException
     */

    public void SynceFolder(List<Folder> folders) {
        JDSActionContext.setContext(autoruncontext);
        for (Folder folder : folders) {
            JDSActionContext.getActionContext().getContext().put("ServerUrl", tUrl);
            try {
                CtVfsFactory.getCtVfsService().mkDir(folder.getPath());
            } catch (JDSException e) {
                e.printStackTrace();
            }
        }


    }


};
