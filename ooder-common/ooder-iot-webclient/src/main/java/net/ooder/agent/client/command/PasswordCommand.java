package net.ooder.agent.client.command;

import  net.ooder.agent.client.enums.CommandEnums;

public class PasswordCommand extends SensorCommand {

    Integer passId = 100;
    Integer passType;
    String passVal1;
    String phone;
    Integer passVal2;
    Long startTime = System.currentTimeMillis();
    Long endTime = System.currentTimeMillis() + 5 * 60 * 1000;
    String modeId;
    String seed;
    String interval;



    public PasswordCommand() {

    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }


    public PasswordCommand(CommandEnums command) {
        super(command);
    }

    public String getModeId() {
        return modeId;
    }


    public void setModeId(String modeId) {
        this.modeId = modeId;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Integer getPassId() {
        return passId;
    }

    public void setPassId(Integer passId) {
        this.passId = passId;
    }

    public Integer getPassType() {
        return passType;
    }

    public void setPassType(Integer passType) {
        this.passType = passType;
    }

    public String getPassVal1() {
        return passVal1;
    }

    public void setPassVal1(String passVal1) {
        this.passVal1 = passVal1;
    }

    public Integer getPassVal2() {
        return passVal2;
    }

    public void setPassVal2(Integer passVal2) {
        this.passVal2 = passVal2;
    }

}
