package net.ooder.agent.client.listener;

import net.ooder.agent.client.iot.Device;
import net.ooder.agent.client.iot.HomeException;
import net.ooder.agent.client.iot.ZNode;
import net.ooder.agent.client.iot.ct.CtIotFactory;
import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.common.EsbFlowType;
import  net.ooder.common.JDSException;
import  net.ooder.agent.client.home.event.ZNodeEvent;
import  net.ooder.agent.client.home.event.ZNodeListener;

import java.util.Set;

@EsbBeanAnnotation(id = "SyncZNodeListener", name = "同步设备ZNODE信息", flowType = EsbFlowType.listener, expressionArr = "SyncZNodeListener()", desc = "同步设备ZNODE信息")
public class SyncZNodeListener implements ZNodeListener {


    @Override
    public void sensorCreated(ZNodeEvent event) throws HomeException {
        ZNode znode = event.getSource();
        Set<String> zondeIds = znode.getEndPoint().getAllZNodeIds();
        if (!zondeIds.contains(znode.getZnodeid())) {
            zondeIds.add(znode.getZnodeid());
        }

        Set<String> childIds = znode.getParentNode().getChildNodeIdList();
        if (!childIds.contains(znode.getZnodeid())) {
            childIds.add(znode.getZnodeid());
        }

        try {
            Device device = CtIotFactory.getCtIotService().getDeviceById(znode.getDeviceid());
            if (device != null && device.getRootDevice() != null) {
                CtIotFactory.getCtIotService().clearDeviceCache(device.getRootDevice().getDeviceid());
            }

        } catch (JDSException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void znodeMoved(ZNodeEvent event) throws HomeException {
        ZNode znode = event.getSource();
        znode.getEndPoint().getAllZNodeIds().remove(znode.getZnodeid());
    }

    @Override
    public void sensorLocked(ZNodeEvent event) throws HomeException {

    }

    @Override
    public void sensorUnLocked(ZNodeEvent event) throws HomeException {

    }

    @Override
    public void sceneAdded(ZNodeEvent event) throws HomeException {

    }

    @Override
    public void sceneRemoved(ZNodeEvent event) throws HomeException {

    }
}
