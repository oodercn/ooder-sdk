package net.ooder.annotation.field;

import net.ooder.annotation.NotNull;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.CustomClass;
import net.ooder.annotation.event.CustomModuleEventEnum;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.CustomViewType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CustomClass(viewType = CustomViewType.COMPONENT, componentType = ComponentType.MQTT)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface MQTTAnnotation {

    String dataBinder() default "";

    String dataField() default "";

    String libCDN() default "";

    @NotNull
    boolean autoConn() default false;

    boolean autoSub() default false;

    String[] subscribers() default {};

    @NotNull
    String server() default "127.0.0.1";

    @NotNull
    int port() default 7019;

    @NotNull
    String path() default "/";

    String clientId() default "";

    int timeout() default 30;

    String userName() default "admin";

    String password() default "admin";

    int keepAliveInterval() default 60;

    boolean cleanSession() default false;

    boolean useSSL() default false;

    boolean reconnect() default true;

    String willTopic() default "";

    String willMessage() default "";

    int willQos() default 0;

    boolean willRetained() default false;

    CustomModuleEventEnum[] onMsgArrived() default {};

    CustomAction[] onMsgArrivedAction() default {};


}
