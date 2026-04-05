package net.ooder.home.event;

import java.io.Serializable;
import net.ooder.common.JDSEvent;
import net.ooder.annotation.EventEnums;

public class HomeEvent extends JDSEvent<Object> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Object data;

    public HomeEvent(Object source) {
        super(source);
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public EventEnums getID() {
        return null;
    }
}
