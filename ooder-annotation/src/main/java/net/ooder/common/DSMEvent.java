package net.ooder.common;

import net.ooder.annotation.CustomBean;

import java.util.List;

public interface DSMEvent<T extends CustomBean, K extends EventKey> {

    public List<T> getActions();

    public String getEventReturn();

    public String getScript();

    public K getEventKey();


}
