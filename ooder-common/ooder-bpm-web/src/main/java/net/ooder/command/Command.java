package net.ooder.command;

import java.io.Serializable;

public interface Command extends Serializable {
    String getCommandId();
    void setCommandId(String commandId);
    String getName();
    void setName(String name);
}
