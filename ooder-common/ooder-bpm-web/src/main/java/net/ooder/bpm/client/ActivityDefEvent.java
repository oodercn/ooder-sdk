package net.ooder.bpm.client;

import net.ooder.bpm.enums.activitydef.ActivityDefDeadLineOperation;
import net.ooder.bpm.enums.event.DeviceAPIEventEnums;
import net.ooder.annotation.DurationUnit;
import net.ooder.agent.client.iot.DeviceEndPoint;
import net.ooder.agent.client.iot.enums.DeviceDataTypeKey;

import java.util.List;

public interface ActivityDefEvent {

    /**
     *
     * @return
     */
    public DeviceAPIEventEnums getDeviceEvent();

    /**
     *
     * @return
     */
    public String getEndpointSelectedId ();

    /**
     *
     * @return
     */
    public List<DeviceEndPoint> getEndpoints();


    /**
     *
     * @return
     */
    public DurationUnit getDurationUnit();

    /**
     *
     * @return
     */
    public String getAlertTime();

    /**
     *
     * @return
     */
    public ActivityDefDeadLineOperation getDeadLineOperation();

    public DeviceDataTypeKey getAttributeName() ;

    public void setAttributeName(DeviceDataTypeKey attributeName) ;

}
