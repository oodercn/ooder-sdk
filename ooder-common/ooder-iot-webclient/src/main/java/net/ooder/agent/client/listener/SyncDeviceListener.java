package net.ooder.agent.client.listener;

import net.ooder.agent.client.iot.Area;
import net.ooder.agent.client.iot.Device;
import net.ooder.agent.client.iot.HomeException;
import net.ooder.agent.client.iot.ZNode;
import net.ooder.agent.client.iot.ct.CtIotFactory;
import net.ooder.agent.client.iot.enums.DeviceStatus;
import  net.ooder.common.ConfigCode;
import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.common.EsbFlowType;
import  net.ooder.common.JDSException;
import  net.ooder.agent.client.home.event.DeviceEvent;
import  net.ooder.agent.client.home.event.DeviceListener;
import  net.ooder.server.JDSServer;

@EsbBeanAnnotation(id = "SyncDeviceListener", name = "同步设备信息", flowType = EsbFlowType.listener, expressionArr = "SyncDeviceListener()", desc = "同步设备信息")
public class SyncDeviceListener implements DeviceListener {
    @Override
    public void register(DeviceEvent event) throws HomeException {
        try {
            if (JDSServer.getInstance().getCurrServerBean().getConfigCode().equals(ConfigCode.app)) {
                Device device = event.getSource();
                if (device != null && device.getRootDevice() != null) {
                    CtIotFactory.getCtIotService().clearDeviceCache(device.getRootDevice().getDeviceid());
                }
            }

        } catch (JDSException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void deviceActivt(DeviceEvent event) {
        Device device = event.getSource();
        try {
            CtIotFactory.getCtIotService().clearDeviceCache(device.getDeviceid());
        } catch (JDSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteing(DeviceEvent event) throws HomeException {

        Device device = event.getSource();
        if (device != null && device.getRootDevice() != null) {
            try {
                CtIotFactory.getCtIotService().clearDeviceCache(device.getRootDevice().getDeviceid());
            } catch (JDSException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void deleteFail(DeviceEvent event) throws HomeException {

    }

    @Override
    public void areaBind(DeviceEvent event) throws HomeException {
        Device device = event.getSource();
        device.setAreaid(device.getAreaid());

        String areaId = device.getAreaid();
        if (areaId != null) {
            Area area = null;
            try {
                area = CtIotFactory.getCtIotService().getAreaById(areaId);
                for (ZNode znode : device.getAllZNodes()) {
                    area.getSensorIds().add(znode.getZnodeid());
                }
            } catch (JDSException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void areaUnBind(DeviceEvent event) throws HomeException {
        Device device = event.getSource();
        String areaId = device.getAreaid();
        if (areaId != null) {
            Area area = null;
            try {
                area = CtIotFactory.getCtIotService().getAreaById(areaId);
                for (ZNode znode : device.getAllZNodes()) {
                    area.getSensorIds().remove(znode.getZnodeid());
                }
            } catch (JDSException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onLine(DeviceEvent event) throws HomeException {
        Device device = event.getSource();
        device.setStates(DeviceStatus.ONLINE);
//        try {
////            if (JDSServer.getInstance().getCurrServerBean().getConfigCode().equals(ConfigCode.gw)) {
////                String epIeee = device.getSerialno();
////                if (device.getDeviceEndPoints().size() > 0) {
////                    epIeee = device.getDeviceEndPoints().get(0).getIeeeaddress();
////                }
////                CtIotFactory.getCommandClient(device.getRootDevice().getSerialno()).sendDataReportCommand(epIeee);
////            }
////
////        } catch (JDSException e) {
////            e.printStackTrace();
////        }

    }

    @Override
    public void offLine(DeviceEvent event) throws HomeException {
        Device device = event.getSource();
        device.setStates(DeviceStatus.OFFLINE);


    }
}
