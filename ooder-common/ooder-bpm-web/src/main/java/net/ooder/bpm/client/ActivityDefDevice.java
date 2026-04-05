package net.ooder.bpm.client;

import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.enums.activitydef.deivce.ActivityDefDevicePerformSequence;
import net.ooder.bpm.enums.activitydef.deivce.ActivityDefDevicePerformtype;
import net.ooder.bpm.enums.activitydef.deivce.ActivityDefDeviceSpecial;
import net.ooder.bpm.enums.command.CommandExecType;
import net.ooder.bpm.enums.command.CommandRetry;
import net.ooder.command.Command;
import net.ooder.agent.client.iot.DeviceEndPoint;

import java.util.List;

public interface ActivityDefDevice {


    /**
     * 命令执行方式
     * @return
     */
    public CommandExecType getCommandExecType();

    /**
     *  命令重试方式
     * @return
     */
    public CommandRetry getCommandRetry ();

    /**
     * 命令重试次数
     * @return
     */
    public Integer getCommandExecRetryTimes();

    /**
     * 命令等待时间
     * @return
     */
    public Integer getCommandDelayTime();

    /**
     * 命令超时等待时间
     * @return
     */
    public Integer commandSendTimeout ();

    /**
     * 是否可以离线发送
     * @return
     */
    public Boolean isCanOffLineSend ();

    /**
     * 设备执行顺序
     * @return
     */
    public ActivityDefDevicePerformSequence getPerformSequence();

    /**
     * 设备执行方式
     * @return
     */
    public ActivityDefDevicePerformtype getPerformType();

    /**
     * 是否收回命令
     * @return
     */
    public Boolean isCanTakeBack();

    /**
     * 是否能重新发送
     * @return
     */
    public Boolean isCanReSend ();

    /**
     * 获取设备应用列表
     * @return
     */
    public String getEndpointSelectedId();

    /**
     * 获取设备名列表
     * @return
     */
    public String getCommandSelectedId();

    /**
     *
     * @return
     */
    public ActivityDefDeviceSpecial getSpecialSendScope();

    /**
     *
     * @return
     * @throws BPMException
     */
    public List<DeviceEndPoint> getEndpoints( )throws BPMException ;

    /**
     *
     * @return
     * @throws BPMException
     */
    public List<Command> getCommand() throws BPMException ;



}
