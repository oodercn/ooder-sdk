package net.ooder.annotation;

import net.ooder.annotation.NotNull;
import net.ooder.annotation.event.*;
import net.ooder.annotation.event.*;
import net.ooder.annotation.ui.ModuleViewType;
import net.ooder.annotation.ui.PanelType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ModuleAnnotation {

    String name() default "";

    String imageClass() default "";

    String caption() default "";

    String childname() default "";

    int rotate() default -1;

    ModuleViewType moduleViewType() default ModuleViewType.NONE;

    String target() default "";

    Class bindService() default Void.class;

    @NotNull
    boolean dynLoad() default false;

    @NotNull
    PanelType panelType() default PanelType.block;

    @NotNull
    boolean cache() default true;


    @NotNull
    boolean initMethod() default false;

//    @NotNull
//    Dock dock() default Dock.fill;

    //准备调

    public CustomModuleEventEnum[] onRender() default {};

    public CustomAction[] onRenderAction() default {};

    public ModuleOnReadyEventEnum[] onReady() default {};

    public CustomAction[] onReadyAction() default {};

    public CustomModuleEventEnum[] onFragmentChanged() default {};

    public CustomAction[] onFragmentChangedAction() default {};

    public ModuleOnMessageEventEnum[] onMessage() default {};

    public CustomAction[] onMessageAction() default {};

    public CustomModuleEventEnum[] beforeCreated() default {};

    public CustomAction[] beforeCreatedAction() default {};

    public CustomOnDestroyEventEnum[] beforeDestroy() default {};

    public CustomAction[] beforeDestroyAction() default {};

    public CustomOnDestroyEventEnum[] onDestroy() default {};

    public CustomAction[] onDestroyAction() default {};

    public CustomModuleEventEnum[] afterShow() default {};

    public CustomAction[] afterShowAction() default {};

    public CustomModuleEventEnum[] onLoadBaseClass() default {};

    public CustomAction[] onLoadBaseClassAction() default {};

    public CustomModuleEventEnum[] onLoadRequiredClass() default {};


    public CustomAction[] onLoadRequiredClassAction() default {};

    public CustomModuleEventEnum[] onLoadRequiredClassErr() default {};

    public CustomAction[] onLoadRequiredClassErrAction() default {};


    public CustomModuleEventEnum[] onIniResource() default {};

    public CustomAction[] onIniResourceAction() default {};


    public CustomModuleEventEnum[] beforeIniComponents() default {};

    public CustomAction[] beforeIniComponentsAction() default {};

    public CustomModuleEventEnum[] afterIniComponents() default {};

    public CustomAction[] afterIniComponentsAction() default {};


    public ModuleOnPropChangeEventEnum[] onModulePropChange() default {};

    public CustomAction[] onModulePropChangeAction() default {};
}
