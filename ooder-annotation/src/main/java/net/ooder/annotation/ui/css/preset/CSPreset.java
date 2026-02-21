package net.ooder.annotation.ui.css.preset;

import net.ooder.annotation.ui.css.preset.ButtonPreset;
import net.ooder.annotation.ui.css.preset.InputPreset;
import net.ooder.annotation.ui.css.preset.AlertPreset;
import net.ooder.annotation.ui.css.preset.CardPreset;
import net.ooder.annotation.ui.css.preset.TagPreset;

import java.lang.annotation.*;

/**
 * CSS预设样式组合注解
 * 用于快速应用预设样式
 * 
 * @author OODER Team
 * @version 2.0.0
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CSPreset {
    
    ButtonPreset button() default ButtonPreset.UNSET;
    
    InputPreset input() default InputPreset.UNSET;
    
    AlertPreset alert() default AlertPreset.UNSET;
    
    CardPreset card() default CardPreset.UNSET;
    
    TagPreset tag() default TagPreset.UNSET;
}
