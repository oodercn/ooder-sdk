/**
 * $RCSfile: CtTopicMsg.java,v $
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
package net.ooder.msg.ct;

import net.ooder.msg.TopicMsg;
import net.ooder.annotation.MethodChinaName;

public class CtTopicMsg extends CtMsg implements TopicMsg {


    private String topic;
    private Boolean retained=false;
    private Integer qos=0;

    public CtTopicMsg() {

    }

    @MethodChinaName("获取主题")
    @Override
    public String getTopic() {
        return topic;
    }

    @MethodChinaName("设置主题")
    @Override
    public void setTopic(String topic) {
        this.topic = topic;
    }

    @MethodChinaName("获取保留标志")
    @Override
    public Boolean getRetained() {
        return retained;
    }

    @MethodChinaName("设置保留标志")
    @Override
    public void setRetained(Boolean retained) {
        this.retained = retained;
    }

    @MethodChinaName("获取服务质量")
    @Override
    public Integer getQos() {
        return qos;
    }

    @MethodChinaName("设置服务质量")
    @Override
    public void setQos(Integer qos) {
        this.qos = qos;
    }
}


