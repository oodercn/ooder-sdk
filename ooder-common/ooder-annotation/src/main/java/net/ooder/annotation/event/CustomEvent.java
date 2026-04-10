package net.ooder.annotation.event;

import net.ooder.annotation.CustomAction;

public interface CustomEvent<T> {
    public CustomAction[] getActions(boolean expar);

    T getEventEnum();


}
