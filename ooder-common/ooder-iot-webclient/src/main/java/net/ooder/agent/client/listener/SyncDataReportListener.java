package net.ooder.agent.client.listener;

import net.ooder.agent.client.iot.DeviceEndPoint;
import net.ooder.agent.client.iot.HomeException;
import net.ooder.agent.client.iot.ct.CtIotFactory;
import net.ooder.agent.client.iot.enums.DeviceDataTypeKey;
import net.ooder.agent.client.iot.enums.DeviceZoneStatus;
import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.common.EsbFlowType;
import  net.ooder.common.JDSException;
import  net.ooder.agent.client.home.event.DataEvent;
import  net.ooder.agent.client.home.event.DataListener;
import  net.ooder.msg.index.DataIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
@EsbBeanAnnotation(id = "SyncDataReportListener", name = "同步数据属性", flowType = EsbFlowType.listener, expressionArr = "SyncDataReportListener()", desc = "同步数据属性")
public class SyncDataReportListener implements DataListener {

    private final Logger logger = LoggerFactory.getLogger(SyncDataReportListener.class);
    ;


    public SyncDataReportListener() {

    }

    @Override
    public String getSystemCode() {
        return null;
    }


    /**
     * 数据上报
     *
     * @param event
     * @throws HomeException
     */
    @Override
    public void dataReport(final DataEvent event) throws HomeException {
        DataIndex dataIndex = event.getSource();
        DeviceEndPoint endPoint = null;
        try {
            endPoint = CtIotFactory.getCtIotService().getEndPointByIeee(dataIndex.getSn());
            endPoint.getCurrvalue().put(DeviceDataTypeKey.fromType(dataIndex.getValuetype()), dataIndex.getValue());
            endPoint.getCurrvalue().put(DeviceDataTypeKey.Zone_Status, DeviceZoneStatus.OFF.getCode().toString());
            // 解除紧急报警

        } catch (JDSException e) {
            e.printStackTrace();
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
        DataIndex dataIndex = event.getSource();
        DeviceEndPoint endPoint = null;
        try {
            endPoint = CtIotFactory.getCtIotService().getEndPointByIeee(dataIndex.getSn());
            endPoint.getCurrvalue().put(DeviceDataTypeKey.fromType(dataIndex.getValuetype()), dataIndex.getValue());
            endPoint.getCurrvalue().put(DeviceDataTypeKey.Zone_Status, DeviceZoneStatus.ON.getCode().toString());
            //            // 解除紧急报警
        } catch (JDSException e) {
            e.printStackTrace();
        }
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