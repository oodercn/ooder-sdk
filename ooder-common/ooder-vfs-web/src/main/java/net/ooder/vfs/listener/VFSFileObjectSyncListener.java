package net.ooder.vfs.listener;

import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.common.EsbFlowType;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.org.conf.OrgConstants;
import  net.ooder.vfs.VFSConstants;
import  net.ooder.vfs.VFSException;
import  net.ooder.vfs.engine.event.FileObjectEvent;
import  net.ooder.vfs.engine.event.FileObjectListener;


@EsbBeanAnnotation(id = "VFSFileObjectSyncListener", name = "文件实体监听", expressionArr = "VFSFileObjectSyncListener()",flowType = EsbFlowType.listener,desc = "文件实体监听")
public class VFSFileObjectSyncListener implements FileObjectListener {

    protected static Log log = LogFactory.getLog(OrgConstants.VFSCONFIG_KEY.getType(), VFSFileObjectSyncListener.class);



    @Override
    public void befaultUpLoad(FileObjectEvent event) throws VFSException {

    }

    @Override
    public void upLoading(FileObjectEvent event) throws VFSException {

    }

    @Override
    public void upLoadEnd(FileObjectEvent event) throws VFSException {

    }

    @Override
    public void upLoadError(FileObjectEvent event) throws VFSException {

    }

    @Override
    public void share(FileObjectEvent event) throws VFSException {

    }

    @Override
    public void append(FileObjectEvent event) throws VFSException {

    }

    @Override
    public void beforDownLoad(FileObjectEvent event) throws VFSException {

    }

    @Override
    public void downLoading(FileObjectEvent event) throws VFSException {

    }

    @Override
    public void downLoadEnd(FileObjectEvent event) throws VFSException {

    }

    @Override
    public String getSystemCode() {
        return VFSConstants.CONFIG_KEY;
    }
}
