package net.ooder.annotation.ui.css.preset;

import net.ooder.annotation.ui.css.CSBorder;
import net.ooder.annotation.ui.css.CSFont;
import net.ooder.annotation.ui.css.CSLayout;

public enum PanelPreset {
    UNSET,
    BASIC,
    CARD,
    COLLAPSIBLE,
    ANT_CARD,
    MATERIAL_CARD;

    public CSFont getFont() { return null; }
    public CSLayout getLayout() { return null; }
    public CSBorder getBorder() { return null; }
}
