package net.ooder.vfs.sync;

import  net.ooder.common.JDSConstants;
import  net.ooder.common.JDSException;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.common.md5.MD5InputStream;
import  net.ooder.context.JDSActionContext;
import  net.ooder.context.JDSContext;
import  net.ooder.server.context.MinServerActionContextImpl;
import  net.ooder.vfs.ct.CtVfsFactory;

import java.util.concurrent.Callable;

public class SyncUPLoadTask<T extends TaskResult> implements Callable<T> {


    public static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, SyncUPLoadTask.class);
    private String vfsPath;

    private String sUrl;
    private String tUrl;
    private MinServerActionContextImpl autoruncontext;

    public SyncUPLoadTask(String sUrl, String tUrl, String vfsPath) {

        this.sUrl = sUrl;
        this.tUrl = tUrl;
        this.vfsPath = vfsPath;
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

    @Override
    public T call() {
        JDSActionContext.setContext(autoruncontext);
        TaskResult<String> resultModel = new TaskResult<String>();
        try {
            JDSActionContext.getActionContext().getContext().put("ServerUrl", sUrl);
            MD5InputStream md5InputStream = CtVfsFactory.getCtVfsService().downLoad(vfsPath);

            JDSActionContext.getActionContext().getContext().put("ServerUrl", tUrl);
            CtVfsFactory.getCtVfsService().upload(vfsPath, md5InputStream, null);
            JDSActionContext.getActionContext().getContext().remove("ServerUrl");
        } catch (JDSException e) {
            e.printStackTrace();
        }

        resultModel.setResult(1);

        return (T) resultModel;
    }


}
