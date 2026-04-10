package net.ooder.annotation;

import net.ooder.common.EventKey;

public enum DataBinderEventEnum implements EventKey {
    beforeInputAlert("beforeInputAlert"),
    beforeUpdateDataToUI("beforeUpdateDataToUI"),
    afterUpdateDataFromUI("afterUpdateDataFromUI");

    private String event;

    private String[] params;

    DataBinderEventEnum(String event,String... args) {

        this.event = event;
        this.params=args;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    @Override
    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }

    @Override
    public String getEvent() {
        return event;
    }


    @Override
    public String toString() {
        return event;
    }


}
