package net.ooder.agent.client.listener;

import net.ooder.agent.client.command.AddSensorCommand;
import net.ooder.agent.client.command.RemoveSensorCommand;
import net.ooder.agent.client.iot.Device;
import net.ooder.agent.client.iot.HomeException;
import net.ooder.agent.client.iot.ct.CtIotCacheManager;
import net.ooder.agent.client.iot.ct.CtIotFactory;
import net.ooder.agent.client.iot.enums.DeviceStatus;
import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.common.EsbFlowType;
import  net.ooder.common.JDSException;
import  net.ooder.agent.client.home.event.CommandEvent;
import  net.ooder.agent.client.home.event.CommandListener;
import  net.ooder.server.JDSServer;
import  net.ooder.common.ConfigCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@EsbBeanAnnotation(id = "SensorCommandListener", name = "添加移除设备监听", expressionArr = "SensorCommandListener()", flowType = EsbFlowType.listener, desc = "同步设备ZNODE信息")
public class SensorCommandListener implements CommandListener {

    private final Logger logger = LoggerFactory.getLogger(SyncCommandListener.class);
    ;


    public SensorCommandListener() {

    }

    @Override
    public void commandSendIng(CommandEvent event) throws HomeException {

    }

    @Override
    public void commandSended(CommandEvent event) throws HomeException {

    }

    @Override
    public void commandSendFail(CommandEvent event) throws HomeException {
        try {
            if (JDSServer.getInstance().getCurrServerBean().getConfigCode().equals(ConfigCode.gw)) {
                if (event.getSource() instanceof RemoveSensorCommand) {
                    RemoveSensorCommand removeSensorCommand = (RemoveSensorCommand) event.getSource();
                    List<String> sensorIeees = new ArrayList<>();
                    if (removeSensorCommand.getSensorieee() != null && !removeSensorCommand.getSensorieee().equals("")) {
                        sensorIeees.add(removeSensorCommand.getSensorieee());
                    }
                    if (removeSensorCommand.getSensorieees() != null) {
                        sensorIeees.addAll(removeSensorCommand.getSensorieees());
                    }
                    for (String sensorieee : sensorIeees) {
                        try {
                            Device device = CtIotFactory.getCtIotService().getDeviceByIeee(sensorieee);
                            CtIotFactory.getCtIotService().getDeviceByIeee(sensorieee).setStates(DeviceStatus.FAULT);
                            CtIotCacheManager.getInstance().updateDevice(device);
                        } catch (JDSException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

        } catch (JDSException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void commandExecuteSuccess(CommandEvent event) throws HomeException {
        if (event.getSource() instanceof AddSensorCommand) {
            CtIotFactory.getCommandClient(((AddSensorCommand) event.getSource()).getGatewayieee()).sendSensorReportCommand();
        } else if (event.getSource() instanceof RemoveSensorCommand) {
            CtIotFactory.getCommandClient(((RemoveSensorCommand) event.getSource()).getGatewayieee()).sendSensorReportCommand();
        }


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
    public String getSystemCode() {
        return null;
    }
}
