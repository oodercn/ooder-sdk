package net.ooder.annotation.event;


import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.action.*;
import net.ooder.annotation.action.*;

import java.lang.annotation.Annotation;


public enum CustomCallBack implements APIEvent {


    JMQCONFIG(APIEventEnum.afterInvoke, "配置JMQ", new CustomAction[]{CustomMQTTAction.JMQCONFIG, CustomMQTTAction.JMQCONNECT}),

    JMQCONNECT(APIEventEnum.afterInvoke, "链接JMQ", new CustomAction[]{CustomMQTTAction.JMQCONNECT}),

    GOPAGE(APIEventEnum.afterInvoke, "页面跳转", new CustomAction[]{CustomGlobalAction.Open}),

    MESSAGE(APIEventEnum.afterInvoke, "执行完毕", new CustomAction[]{CustomMsgAction.MESSAGE}),

    ECHO(APIEventEnum.afterInvoke, "调试日志", new CustomAction[]{CustomMsgAction.ECHO}),

    TREEOPENTONODE(APIEventEnum.afterInvoke, LocalTreeAction.OPENTONODE.getDesc(), new CustomAction[]{LocalTreeAction.OPENTONODE}),

    TREETOGGLENODE(APIEventEnum.afterInvoke, LocalTreeAction.TOGGLENODE.getDesc(), new CustomAction[]{LocalTreeAction.TOGGLENODE}),

    TREEINSERTITEMS(APIEventEnum.afterInvoke, LocalTreeAction.INSERTITEMS.getDesc(), new CustomAction[]{LocalTreeAction.INSERTITEMS}),

    TREEREMOVEITEMS(APIEventEnum.afterInvoke, LocalTreeAction.REMOVEITEMS.getDesc(), new CustomAction[]{LocalTreeAction.REMOVEITEMS}),

    TREECLEARITEMS(APIEventEnum.afterInvoke, LocalTreeAction.CLEARITEMS.getDesc(), new CustomAction[]{LocalTreeAction.CLEARITEMS}),

    TREERELOADNODE(APIEventEnum.afterInvoke, LocalTreeAction.RELOADNODE.getDesc(), new CustomAction[]{LocalTreeAction.RELOADNODE}),

    TREEFIREITEMCLICKEVENT(APIEventEnum.afterInvoke, LocalTreeAction.FIREITEMCLICKEVENT.getDesc(), new CustomAction[]{LocalTreeAction.FIREITEMCLICKEVENT}),

    TREESAVE(APIEventEnum.afterInvoke, CustomTreeAction.SAVE.getDesc(), new CustomAction[]{CustomTreeAction.SAVE}),

    FREE(APIEventEnum.afterInvoke, CustomMsgAction.FREE.getDesc(), new CustomAction[]{CustomMsgAction.FREE}),

    RELOAD(APIEventEnum.afterInvoke, CustomTreeGridAction.RELOAD.getDesc(), new CustomAction[]{CustomTreeGridAction.RELOAD}),

    PAGERELOAD(APIEventEnum.afterInvoke, CustomPageAction.RELOAD.getDesc(), new CustomAction[]{CustomPageAction.RELOAD}),

    RELOADMENU(APIEventEnum.afterInvoke, CustomPageAction.RELOADMENU.getDesc(), new CustomAction[]{CustomPageAction.RELOADMENU}),

    DYNRELOAD(APIEventEnum.afterInvoke, CustomDynModuleAction.DYNRELOAD.getDesc(), new CustomAction[]{CustomDynModuleAction.DYNRELOAD}),


    RELOADPARENT(APIEventEnum.afterInvoke, CustomPageAction.RELOADPARENT.desc(), new CustomAction[]{CustomPageAction.RELOADPARENT}),

    RELOADTOP(APIEventEnum.afterInvoke, CustomPageAction.RELOADTOP.getDesc(), new CustomAction[]{CustomPageAction.RELOADTOP}),

    CLOSEPARENT(APIEventEnum.afterInvoke, CustomPageAction.CLOSEPARENT.getDesc(), new CustomAction[]{CustomPageAction.CLOSEPARENT}),

    CLOSETOP(APIEventEnum.afterInvoke, CustomPageAction.CLOSETOP.getDesc(), new CustomAction[]{CustomPageAction.CLOSETOP}),

    RELOADTOPPARENT(APIEventEnum.afterInvoke, CustomPageAction.RELOADTOPPARENT.getDesc(), new CustomAction[]{CustomPageAction.RELOADTOPPARENT}),

    CLOSE(APIEventEnum.afterInvoke, CustomPageAction.CLOSE.getDesc(), new CustomAction[]{CustomPageAction.CLOSE});

    APIEventEnum event;
    CustomAction[] actions;
    String desc;
    @JSONField(name = "return")
    private Boolean _return = true;
    private String eventReturn;

    CustomCallBack(APIEventEnum event, String desc, CustomAction[] actions) {
        this.event = event;
        this.actions = actions;
        this.desc = desc;
    }

    @Override
    public boolean _return() {
        return true;
    }

    public String getEventReturn() {
        return eventReturn;
    }

    public void setEventReturn(String eventReturn) {
        this.eventReturn = eventReturn;
    }

    @Override
    public String eventReturn() {
        return eventReturn;
    }

    ;

    CustomCallBack(APIEvent event) {
        this.event = event.event();
        this.actions = event.actions();
        this.desc = event.desc();
    }

    @Override
    public String eventName() {
        return name();
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    public APIEventEnum getEvent() {
        return event;
    }

    public void setEvent(APIEventEnum event) {
        this.event = event;
    }

    public CustomAction[] getActions() {
        return actions;
    }

    public void setActions(CustomAction[] actions) {
        this.actions = actions;
    }


    @Override
    public APIEventEnum event() {
        return event;
    }

    @Override
    public CustomAction[] actions() {
        return actions;
    }

    @Override
    public String desc() {
        return desc;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return APIEvent.class;
    }
}
