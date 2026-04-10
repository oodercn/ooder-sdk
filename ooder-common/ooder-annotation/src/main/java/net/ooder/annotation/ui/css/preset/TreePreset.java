package net.ooder.annotation.ui.css.preset;

import net.ooder.annotation.ui.css.CSBorder;
import net.ooder.annotation.ui.css.CSFont;
import net.ooder.annotation.ui.css.CSLayout;

public enum TreePreset {
    UNSET,
    DEFAULT,
    DIRECTORY,
    SIMPLE;

    public CSFont getFont() { return null; }
    public CSLayout getLayout() { return null; }
    public CSBorder getBorder() { return null; }
}
