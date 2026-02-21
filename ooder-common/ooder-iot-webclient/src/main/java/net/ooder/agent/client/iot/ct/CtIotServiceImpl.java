package net.ooder.agent.client.iot.ct;

import net.ooder.agent.client.command.Command;
import net.ooder.agent.client.iot.*;
import net.ooder.agent.client.iot.api.inner.HomeService;
import net.ooder.agent.client.iot.api.inner.IotService;
import net.ooder.common.JDSException;
import  net.ooder.jds.core.esb.EsbUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

public class CtIotServiceImpl implements CtIotService {

    @Override
    public void clearDeviceCache(String deviceId) throws JDSException {
         CtIotCacheManager.getInstance().clearCache(deviceId);
    }

    @Override
    public Device getDeviceById(String deviceId) throws JDSException {
        return CtIotCacheManager.getInstance().getDeviceById(deviceId);
    }

    @Override
    public List<Place> getAllPlace() throws JDSException {
        List<Place> ctplaces = new ArrayList<Place>();
        Set<String> placeIds = getHomeService().getAllPlaceIds().getData();
        for (String placeId : placeIds) {
            ctplaces.add(this.getPlaceById(placeId));
        }
        return ctplaces;
    }

    @Override
    public Place getPlaceById(String placeId) throws JDSException {
        return CtIotCacheManager.getInstance().getPlaceById(placeId);
    }

    @Override
    public Area getAreaById(String areaId) throws JDSException {
        return CtIotCacheManager.getInstance().getAreaById(areaId);
    }

    @Override
    public DeviceEndPoint getEndPointById(String endPointId) throws JDSException {
        return CtIotCacheManager.getInstance().getEndPointById(endPointId);
    }

    @Override
    public ZNode getZNodeById(String znodeId) throws JDSException {
        return CtIotCacheManager.getInstance().getZNodeById(znodeId);
    }

    @Override
    public <T extends Command> T getCommandById(String commandId) throws JDSException {
        return CtIotCacheManager.getInstance().getCommand(commandId);
    }

    @Override
    public List<DeviceEndPoint> getSensorByBindAccount(String account) throws JDSException {

        List<DeviceEndPoint> ctEndPoints = new ArrayList<DeviceEndPoint>();
        List<String> endpointids = getService().getSensorIdsByBindAccount(account).getData();
        for (String endPointId : endpointids) {
            ctEndPoints.add(this.getEndPointById(endPointId));
        }
        return ctEndPoints;
    }

    @Override
    public List<Sensortype> getAllSensorTypes() throws JDSException {

        return CtIotCacheManager.getInstance().getAllSensorType();
    }


    IotService getService() {
        IotService ctService = (IotService) EsbUtil.parExpression(IotService.class);
        return ctService;
    }

    HomeService getHomeService() {
        HomeService ctService = (HomeService) EsbUtil.parExpression(HomeService.class);
        return ctService;
    }

    @Override
    public Sensortype getSensorTypesByNo(String devicetype) throws JDSException {

        return CtIotCacheManager.getInstance().getSensorTypesByNo(Integer.valueOf(devicetype));
    }

    @Override
    public DeviceEndPoint getEndPointByIeee(String sensorieee) throws JDSException {
        return CtIotCacheManager.getInstance().getEndPointByIeee(sensorieee);

    }

    @Override
    public Future<Command> sendCommand(Command command, Integer delayTime) throws JDSException {
        return CtIotCacheManager.getInstance().sendCommand(command, delayTime);
    }

    @Override
    public Device getDeviceByIeee(String gatewayieee) throws JDSException {
        return CtIotCacheManager.getInstance().getDeviceByIeee(gatewayieee);
    }



}
