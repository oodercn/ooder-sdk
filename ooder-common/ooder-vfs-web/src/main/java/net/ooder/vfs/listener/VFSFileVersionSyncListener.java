package net.ooder.vfs.listener;

import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.common.EsbFlowType;
import  net.ooder.common.JDSException;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.org.conf.OrgConstants;
import  net.ooder.vfs.FileInfo;
import  net.ooder.vfs.FileVersion;
import  net.ooder.vfs.VFSConstants;
import  net.ooder.vfs.VFSException;
import  net.ooder.vfs.ct.CtFile;
import  net.ooder.vfs.ct.CtVfsFactory;
import  net.ooder.vfs.engine.event.FileVersionEvent;
import  net.ooder.vfs.engine.event.FileVersionListener;


@EsbBeanAnnotation(id = "VFSFileVersionSyncListener", name = "版本同步监听", expressionArr = "VFSFileVersionSyncListener()", desc = "版本同步监听", flowType = EsbFlowType.listener)
public class VFSFileVersionSyncListener implements FileVersionListener {

    protected static Log log = LogFactory.getLog(OrgConstants.VFSCONFIG_KEY.getType(), VFSFileVersionSyncListener.class);


    @Override
    public void lockVersion(FileVersionEvent event) throws VFSException {


    }

    @Override
    public void addFileVersion(FileVersionEvent event) throws VFSException {
        try {

            if (event.getPath() != null && !event.getPath().equals("")) {
                CtFile fileInfo = (CtFile) CtVfsFactory.getCtVfsService().getFileByPath(event.getPath());
                if (fileInfo != null) {
                    CtVfsFactory.getCtVfsService().clearCache(fileInfo.getFolder().getPath());
                    //  CtVfsFactory.getCtVfsService().clearFileCache(fileInfo.getPath());
                }
            }
        } catch (JDSException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void updateFileVersion(FileVersionEvent event) throws VFSException {
        try {
            if (event.getPath() != null && !event.getPath().equals("")) {
                FileVersion version = CtVfsFactory.getCtVfsService().getFileVersionByPath(event.getPath());
                if (version != null && version.getIndex() == 1) {
                    FileInfo fileInfo = CtVfsFactory.getCtVfsService().getFileById(version.getFileId());
                    if (fileInfo != null) {
                        CtVfsFactory.getCtVfsService().clearCache(fileInfo.getFolder().getPath());
                    }
                } else {
                    CtVfsFactory.getCtVfsService().clearFileVersionCache(event.getPath());
                }
            }

        } catch (JDSException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void deleteFileVersion(FileVersionEvent event) throws VFSException {
        try {
            CtVfsFactory.getCtVfsService().clearFileVersionCache(event.getPath());
        } catch (JDSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getSystemCode() {
        return VFSConstants.CONFIG_KEY;
    }
}
