package net.ooder.agent.client.listener;

import net.ooder.agent.client.iot.DeviceEndPoint;
import net.ooder.agent.client.iot.HomeException;
import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.common.EsbFlowType;
import  net.ooder.agent.client.home.event.DeviceEndPointEvent;
import  net.ooder.agent.client.home.event.DeviceEndPointListener;

import java.util.Set;

@EsbBeanAnnotation(id = "SyncDeviceEndpointListener", name = "同步设备信息",flowType = EsbFlowType.listener, expressionArr = "SyncDeviceEndpointListener()", desc = "同步设备信息")
public class SyncDeviceEndpointListener implements DeviceEndPointListener {


    @Override
    public void updateInfo(DeviceEndPointEvent event) throws HomeException {

    }

    @Override
    public void bind(DeviceEndPointEvent event) throws HomeException {

    }

    @Override
    public void bindSuccess(DeviceEndPointEvent event) throws HomeException {

    }

    @Override
    public void bindFail(DeviceEndPointEvent event) throws HomeException {

    }

    @Override
    public void unbind(DeviceEndPointEvent event) throws HomeException {

    }

    @Override
    public void unbindSuccess(DeviceEndPointEvent event) throws HomeException {

    }

    @Override
    public void unbindFail(DeviceEndPointEvent event) throws HomeException {

    }

    @Override
    public void locked(DeviceEndPointEvent event) throws HomeException {

    }

    @Override
    public void unLocked(DeviceEndPointEvent event) throws HomeException {

    }

    @Override
    public void createEndPoint(DeviceEndPointEvent event) throws HomeException {
        DeviceEndPoint endPoint=event.getSource();
        Set<String> epIds= endPoint.getDevice().getDeviceEndPointIds();
        if (!epIds.contains(endPoint.getEndPointId())){
            epIds.add(endPoint.getEndPointId());
        }

    }

    @Override
    public void removeEndPoint(DeviceEndPointEvent event) throws HomeException {

    }
}
