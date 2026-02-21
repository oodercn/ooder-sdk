package net.ooder.agent.client.home.listener;

import  net.ooder.agent.client.home.event.*;
import net.ooder.agent.client.iot.DeviceEndPoint;
import net.ooder.agent.client.iot.HomeException;

//@EsbBeanAnnotation(id = "ListenerLog", name = "测试服务监听器", expressionArr = "ListenerLog()",flowType = EsbFlowType.listener, desc = "测试服务监听器")
public class ListenerLog implements CommandListener, DeviceListener, GatewayListener, PlaceListener, SensorListener {


    @Override
    public void commandSendIng(CommandEvent event) throws HomeException {

    }

    @Override
    public void commandSended(CommandEvent event) throws HomeException {

    }

    @Override
    public void commandSendFail(CommandEvent event) throws HomeException {

    }

    @Override
    public void commandExecuteSuccess(CommandEvent event) throws HomeException {

    }

    @Override
    public void commandExecuteFail(CommandEvent event) throws HomeException {

    }

    @Override
    public void commandSendTimeOut(CommandEvent event) throws HomeException {

    }

    @Override
    public void commandRouteing(CommandEvent event) throws HomeException {

    }

    @Override
    public void commandRouted(CommandEvent event) throws HomeException {

    }

    @Override
    public void gatewayOnLine(GatewayEvent event) throws HomeException {

    }

    @Override
    public void gatewayOffLine(GatewayEvent event) throws HomeException {

    }

    @Override
    public void sensorAdding(GatewayEvent event) throws HomeException {

    }

    @Override
    public void sensorAdded(GatewayEvent event) throws HomeException {

    }

    @Override
    public void gatewaySharing(GatewayEvent event) throws HomeException {

    }

    @Override
    public void gatewayShared(GatewayEvent event) throws HomeException {

    }

    @Override
    public void stopGatewayShared(GatewayEvent event) throws HomeException {

    }

    @Override
    public void sensorRemoving(GatewayEvent event) throws HomeException {

    }

    @Override
    public void sensorRemoved(GatewayEvent event) throws HomeException {

    }

    @Override
    public void gatewayLocked(GatewayEvent event) throws HomeException {

    }

    @Override
    public void gatewayUnLocked(GatewayEvent event) throws HomeException {

    }

    @Override
    public void accountBind(GatewayEvent event) throws HomeException {

    }

    @Override
    public void accountUNBind(GatewayEvent event) throws HomeException {

    }

    @Override
    public String getSystemCode() {
        return null;
    }

    @Override
    public void register(DeviceEvent event) throws HomeException {

    }

    @Override
    public void deviceActivt(DeviceEvent event) {

    }

    @Override
    public void deleteing(DeviceEvent event) throws HomeException {

    }

    @Override
    public void deleteFail(DeviceEvent event) throws HomeException {

    }

    @Override
    public void areaBind(DeviceEvent event) throws HomeException {

    }

    @Override
    public void areaUnBind(DeviceEvent event) throws HomeException {

    }

    @Override
    public void onLine(DeviceEvent event) throws HomeException {

    }

    @Override
    public void offLine(DeviceEvent event) throws HomeException {

    }

    @Override
    public void placeCreate(PlaceEvent event) throws HomeException {

    }

    @Override
    public void placeRemove(PlaceEvent event) throws HomeException {

    }

    @Override
    public void areaAdd(PlaceEvent event) throws HomeException {

    }

    @Override
    public void areaRemove(PlaceEvent event) throws HomeException {

    }

    @Override
    public void gatewayAdd(PlaceEvent event) throws HomeException {

    }

    @Override
    public void gatewayRemove(PlaceEvent event) throws HomeException {

    }

    @Override
    public void addDesktop(SensorEvent event) throws HomeException {

    }

    @Override
    public void removeDesktop(SensorEvent event) throws HomeException {

    }

    @Override
    public void addAlarm(SensorEvent event) throws HomeException {

    }

    @Override
    public void removeAlarm(SensorEvent event) throws HomeException {

    }

    @Override
    public void start(SensorEvent event) throws HomeException {

    }

    @Override
    public void close(SensorEvent event) throws HomeException {

    }

    @Override
    public void sceneAdded(DeviceEndPoint event) throws HomeException {

    }

    @Override
    public void sceneRemoved(DeviceEndPoint event) throws HomeException {

    }
}
