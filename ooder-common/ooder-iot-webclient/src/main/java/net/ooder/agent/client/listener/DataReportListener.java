package net.ooder.agent.client.listener;

import com.alibaba.fastjson.JSONObject;
import net.ooder.agent.client.command.CommandFactory;
import net.ooder.agent.client.command.task.IRCommandTask;
import net.ooder.agent.client.iot.Device;
import net.ooder.agent.client.iot.DeviceEndPoint;
import net.ooder.agent.client.iot.HomeException;
import net.ooder.agent.client.iot.ct.CtIotFactory;
import net.ooder.agent.client.iot.enums.DeviceDataTypeKey;
import  net.ooder.common.JDSException;
import  net.ooder.common.cache.Cache;
import  net.ooder.common.cache.CacheManagerFactory;
import  net.ooder.config.UserBean;
import  net.ooder.agent.client.home.event.DataEvent;
import  net.ooder.agent.client.home.event.DataListener;
import  net.ooder.msg.index.DataIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Component
//@EsbBeanAnnotation(id = "DataReportListener", name = "红外监听上报监听器", expressionArr = "DataReportListener()", flowType = EsbFlowType.listener,desc = "红外监听上报监听器")
public class DataReportListener implements DataListener {

    private final Logger logger = LoggerFactory.getLogger(DataReportListener.class);
    ;


    public DataReportListener() {

    }

    @Override
    public String getSystemCode() {
        return null;
    }


    private void sendIRCommand(Device gwdevice, String value) {


        List<Device> devices = gwdevice.getChildDevices();

        for (Device cdevice : devices) {

            List<DeviceEndPoint> deviceEndPoints = cdevice.getDeviceEndPoints();
            for (DeviceEndPoint endPoint : deviceEndPoints) {
                if (endPoint.getSensortype().getType().equals(17)) {
                    IRCommandTask task = new IRCommandTask(gwdevice.getSerialno(), endPoint.getIeeeaddress(), value, gwdevice.getSubsyscode());
                    CommandFactory.getCommandExecutors(endPoint.getIeeeaddress()).schedule(task, 0, TimeUnit.SECONDS);
                }
            }
        }
    }

    static Map<String, Long> pirMap = new HashMap<String, Long>();

    static Map<String, Long> mcMap = new HashMap<String, Long>();

    /**
     * 数据上报
     *
     * @param event
     * @throws HomeException
     */
    @Override
    public void dataReport(final DataEvent event) throws HomeException {
        DataIndex dataIndex = (DataIndex) event.getSource();

        if (UserBean.getInstance() != null && UserBean.getInstance().getUsername() != null && UserBean.getInstance().getUsername().equals("consoleconsole")) {

            String sn = dataIndex.getSn();
            String gwDevcieSn = dataIndex.getGwSN();
            if (dataIndex != null) {
                logger.info("-----数据上报dataReport-----sn=" + sn + "gwDevcieSn=" + gwDevcieSn + " DeviceDataTypeKey=" + dataIndex.getValuetype() + " value=" + dataIndex.getValue());

                try {
                    DeviceEndPoint endpoint = CtIotFactory.getCtIotService().getEndPointByIeee(sn);
                    Map<DeviceDataTypeKey, String> valueMC = endpoint.getCurrvalue();

                    Device gwdevice = CtIotFactory.getCtIotService().getDeviceByIeee(gwDevcieSn);

                    if (gwdevice == null) {
                        gwdevice = endpoint.getDevice().getRootDevice();
                    }

                    Cache<String, String> irceneCache = CacheManagerFactory.createCache("JDS", "irScene",1 * 1024 * 1024, 60 * 60 * 1000);
                    String scenejson = irceneCache.get(gwdevice.getSerialno());


                    logger.info("-----数据上报dataReport-----scenejson" + scenejson);

                    String znodeStatus = dataIndex.getValue();


                    if (dataIndex.getValuetype().equals(DeviceDataTypeKey.Status.getType()) && (endpoint.getSensortype().getType().equals(1) || endpoint.getSensortype().getType().equals(100))) {
                        //门打开时发送开灯指令
                        if (znodeStatus == null || znodeStatus.equals("1")) {
                            //sendIRCommand(gwdevice, "2");
                            mcMap.put(endpoint.getIeeeaddress(), System.currentTimeMillis());
                        } else {

                            List<Device> devices = gwdevice.getChildDevices();
                            for (Device cdevice : devices) {
                                List<DeviceEndPoint> deviceEndPoints = cdevice.getDeviceEndPoints();
                                for (DeviceEndPoint endPoint : deviceEndPoints) {
                                    if (endPoint.getSensortype().getType().equals(4)) {
                                        Map<DeviceDataTypeKey, String> values = cdevice.getDeviceEndPoints().get(0).getCurrvalue();
                                        Long pirtime = pirMap.get(endPoint.getIeeeaddress());
                                        Long mctime = mcMap.get(endpoint.getIeeeaddress());
                                        Cache irCache = CacheManagerFactory.createCache("JDS", "irCache",1 * 1024 * 1024, 60 * 60 * 1000);
                                        boolean open = true;

                                        if (pirtime != null && mctime > pirtime) {
                                            //    if (pirtime != null && mctime>pirtime && mctime-pirtime<10000)  {
                                            if (System.currentTimeMillis() - pirtime < 30000) {
                                                this.logger.info(endPoint.getIeeeaddress());
                                                if (scenejson != null && !scenejson.equals("")) {
                                                    Map<String, String> map = JSONObject.parseObject(scenejson, Map.class);
                                                    if (map.get("closescene").equals("0")) {
                                                        open = false;
                                                    }
                                                }

                                                if (open) {
                                                    String modeId = (String) irCache.get(gwdevice.getSerialno() + "||0");
                                                    if (modeId != null) {
                                                        sendIRCommand(gwdevice, modeId);

                                                    } else if (endPoint.getIeeeaddress().equals("086BD7FFFE8ACB8001")) {

                                                        sendIRCommand(gwdevice, "10");
                                                    } else {
                                                        sendIRCommand(gwdevice, "3");
                                                    }

                                                }
                                                pirMap.remove(endPoint.getIeeeaddress());

                                            }
                                        } else {


                                            boolean close = true;
                                            if (scenejson != null && !scenejson.equals("")) {
                                                Map<String, String> map = JSONObject.parseObject(scenejson, Map.class);
                                                if (map.get("openscene").equals("0")) {
                                                    close = false;
                                                }

                                            }

                                            if (close) {
                                                String modeId = (String) irCache.get(gwdevice.getSerialno() + "||1");
                                                if (modeId != null) {
                                                    sendIRCommand(gwdevice, modeId);

                                                } else if (endPoint.getIeeeaddress().equals("086BD7FFFE8ACB8001")) {
                                                    sendIRCommand(gwdevice, "11");
                                                } else {
                                                    sendIRCommand(gwdevice, "2");
                                                }
                                            }
                                        }

                                    }
                                }
                            }

                        }
                    } else if (dataIndex.getValuetype().equals(DeviceDataTypeKey.Status.getType()) && endpoint.getSensortype().getType().equals(4)) {
                        if (znodeStatus == null || znodeStatus.equals("1")) {
                            //sendIRCommand(gwdevice, "2");
                            pirMap.put(dataIndex.getSn(), System.currentTimeMillis());
                        }
                    }


                } catch (JDSException e) {
                    e.printStackTrace();
                }
            }

        }


    }

    /**
     * 报警
     *
     * @param event
     * @throws HomeException
     */
    @Override
    public void alarmReport(final DataEvent event) throws HomeException {

    }

    /**
     * 属性上报
     *
     * @param event
     * @throws HomeException
     */
    @Override
    public void attributeReport(final DataEvent event) throws HomeException {


    }


}