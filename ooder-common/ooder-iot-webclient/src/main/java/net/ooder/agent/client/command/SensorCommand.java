package net.ooder.agent.client.command;

import  net.ooder.agent.client.enums.CommandEnums;

import java.util.List;

public class SensorCommand extends Command {

    private static final long serialVersionUID = -1371652048371144609L;

    String sensorieee;

    Integer sensorType;

    List<String> sensorieees;

    public SensorCommand() {

    }

    public SensorCommand(final CommandEnums command) {
        super(command);
    }

    public Integer getSensorType() {
        return sensorType;
    }

    public void setSensorType(final Integer sensorType) {
        this.sensorType = sensorType;
    }

    public List<String> getSensorieees() {
        return sensorieees;
    }

    public void setSensorieees(final List<String> sensorieees) {
        this.sensorieees = sensorieees;
    }

    public String getSensorieee() {
        return sensorieee;
    }

    public void setSensorieee(final String sensorieee) {
        this.sensorieee = sensorieee;
    }

}
