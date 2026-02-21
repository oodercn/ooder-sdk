package net.ooder.vfs.engine.event.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface Listener
{
    String name();
    String caption();
    String type();
    Class tagClass();
    String ftlPath();
}
