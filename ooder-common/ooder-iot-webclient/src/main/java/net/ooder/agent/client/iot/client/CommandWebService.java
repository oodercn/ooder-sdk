package net.ooder.agent.client.iot.client;


import net.ooder.agent.client.command.CommandReportStatus;
import net.ooder.agent.client.iot.json.device.BindInfo;
import net.ooder.agent.client.iot.json.device.SenceMode;
import net.ooder.config.ResultModel;

public interface CommandWebService {

    /**
     * 
     * @param bindInfo
     * @return
     */
    public ResultModel<Boolean> bindListReport(BindInfo bindInfo);

    /**
     * 
     * @param mode
     * @return
     */
    public ResultModel<Boolean> modeListReport(SenceMode mode);

    /**
     * 2.6.5.3 智能灯传感器开关设置
     * 
     * @param sensorId
     * @param value
     * @return
     */
    ResultModel<Boolean> setLightSensorStatusInfo(String sensorId, Integer value);

    /**
     * 2.6.5.3 插座传感器开关设置
     * 
     * @param sensorId
     * @param value
     * @return
     */
    ResultModel<Boolean> setOutLetSensorInfo(String sensorId, Integer value);

    /**
     * 命令状态报告
     * 
     * @param commandReportStatus
     * @throws HomeException
     */
    ResultModel<Boolean> commandReport(CommandReportStatus commandReportStatus);

    /**
     * 绑定状态报告
     * 
     * @param report
     * @throws HomeException
     */
    ResultModel<Boolean> bindingStatusReport(CommandReportStatus report);

}
