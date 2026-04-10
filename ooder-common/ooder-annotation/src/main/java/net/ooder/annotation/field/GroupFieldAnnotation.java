package net.ooder.annotation.field;


import net.ooder.annotation.*;
import net.ooder.annotation.event.CustomFormEvent;
import net.ooder.annotation.ui.*;
import net.ooder.annotation.NotNull;
import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.*;

import java.lang.annotation.*;

@Inherited
@CustomClass(viewType = CustomViewType.COMPONENT, moduleType = ModuleViewType.GROUPCONFIG, componentType = ComponentType.GROUP)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface GroupFieldAnnotation {

    @NotNull
    Dock dock() default Dock.fill;

    String image() default "";

    ImagePos imagePos() default ImagePos.center;

    String imageBgSize() default "";

    String iconFontCode() default "";

    String height() default "150";

    @NotNull
    BorderType borderType() default BorderType.none;

    boolean noFrame() default false;

    HAlignType hAlign() default HAlignType.left;

    ToggleIconType toggleIcon() default ToggleIconType.taggle;

    @NotNull
    boolean toggleBtn() default true;

    String imageClass() default "";

    String caption() default "";

    String groupName() default "";

    boolean displayBar() default true;

    boolean optBtn() default true;

    boolean refreshBtn() default true;

    boolean infoBtn() default false;

    boolean closeBtn() default true;

    @NotNull
    boolean lazyLoad() default false;

    @NotNull
    boolean initFold() default false;

    boolean lazyAppend() default true;

    boolean autoReload() default true;

    boolean dynDestory() default false;

    Class<? extends TabItem> customItems() default TabItem.class;

    String src() default "";

    boolean dynLoad() default false;

    EmbedType embed() default EmbedType.module;

    @NotNull
    AppendType append() default AppendType.ref;

    @NotNull
    Class bindClass() default Void.class;

    CustomFormEvent[] event() default {};

}
