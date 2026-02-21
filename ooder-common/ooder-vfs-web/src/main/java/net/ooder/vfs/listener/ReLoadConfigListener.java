package net.ooder.vfs.listener;

import  net.ooder.common.JDSConstants;
import  net.ooder.common.JDSException;
import  net.ooder.common.SystemNodeType;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.common.property.ConfigFactory;
import  net.ooder.config.JDSConfig.Config;
import  net.ooder.engine.event.EIServerAdapter;
import  net.ooder.engine.event.EIServerEvent;
import  net.ooder.esb.config.manager.EsbBeanFactory;
import  net.ooder.esb.config.manager.ExpressionTempBean;
import  net.ooder.esb.config.manager.ServiceBean;
import  net.ooder.esb.util.EsbFactory;
import  net.ooder.server.JDSServer;
import  net.ooder.vfs.sync.SyncFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ReLoadConfigListener extends EIServerAdapter {

    private static final long serialVersionUID = 1L;
    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, ReLoadConfigListener.class);

    // 关闭前更新配置
    @Override
    public void serverStopping(EIServerEvent event) throws JDSException, InterruptedException {
        logger.info("stop server .... start update Config");
        try {
            if (JDSServer.getInstance().getCurrServerBean().getType().equals(SystemNodeType.MAIN)) {

                SyncFactory.getInstance().push(Paths.get(Config.rootServerHome().getAbsolutePath()), "root/JDSHome/");

            } else {
                SyncFactory.getInstance().pull(Paths.get(Config.configPath().getAbsolutePath()), "root/JDSHome/");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ConfigFactory.reLoad();
    }

    @Override
    public void serverStarted(EIServerEvent event) throws JDSException {

        logger.info("serverStarted .... loadStaticAllData");

        EsbFactory.initBus();

        List<? extends ServiceBean> list = EsbBeanFactory.getInstance().loadAllServiceBean();
        for (int k = 0; k < list.size(); k++) {
            if (!(list.get(k) instanceof ExpressionTempBean)) {
                continue;
            }
            ExpressionTempBean bean = (ExpressionTempBean) list.get(k);
            logger.info(bean.getId() + bean.getExpressionArr());
        }
        logger.info("end .... loadStaticAllData");


    }

}
