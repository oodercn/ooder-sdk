package net.ooder.agent.client.home.listener;

import  net.ooder.agent.client.home.event.DeviceEvent;
import  net.ooder.agent.client.home.event.DeviceListener;
import net.ooder.agent.client.iot.HomeException;

public class DefaultDeviceListener implements DeviceListener {


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
}
