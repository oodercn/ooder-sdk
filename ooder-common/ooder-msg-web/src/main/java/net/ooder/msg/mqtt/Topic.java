/**
 * $RCSfile: Topic.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.msg.mqtt;

import java.util.Set;

public interface Topic {

    public Set<String> getPersonIds();

    public void setPersonIds(Set<String> personIds);

    public  String  getTopic();

    public  void setTopic(String times);

    public boolean isRetained();

    public void setRetained(boolean event);

    public int getQos();

    public void setQos(int qos);

}


