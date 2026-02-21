package net.ooder.annotation.ui.css.preset;

import net.ooder.annotation.ui.css.CSBorder;
import net.ooder.annotation.ui.css.CSFont;
import net.ooder.annotation.ui.css.CSLayout;

public enum TagPreset {
    UNSET,
    DEFAULT,
    PRIMARY,
    SUCCESS,
    WARNING,
    DANGER;

    public CSFont getFont() {
        return new CSFont() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() { return CSFont.class; }

            @Override
            public String color() {
                switch (TagPreset.this) {
                    case UNSET: return "";
                    case DEFAULT: return "#666666";
                    default: return "#ffffff";
                }
            }

            @Override
            public String fontSize() { return "12px"; }

            @Override
            public net.ooder.annotation.ui.css.enums.CSFontWeight fontWeight() { return net.ooder.annotation.ui.css.enums.CSFontWeight.NORMAL; }

            @Override
            public String fontFamily() { return ""; }

            @Override
            public net.ooder.annotation.ui.css.enums.CSFontStyle fontStyle() { return net.ooder.annotation.ui.css.enums.CSFontStyle.UNSET; }

            @Override
            public String lineHeight() { return ""; }

            @Override
            public String letterSpacing() { return ""; }

            @Override
            public net.ooder.annotation.ui.css.enums.CSTextAlign textAlign() { return net.ooder.annotation.ui.css.enums.CSTextAlign.UNSET; }

            @Override
            public net.ooder.annotation.ui.css.enums.CSTextDecoration textDecoration() { return net.ooder.annotation.ui.css.enums.CSTextDecoration.UNSET; }

            @Override
            public net.ooder.annotation.ui.css.enums.CSTextTransform textTransform() { return net.ooder.annotation.ui.css.enums.CSTextTransform.UNSET; }

            @Override
            public net.ooder.annotation.ui.css.enums.CSWhiteSpace whiteSpace() { return net.ooder.annotation.ui.css.enums.CSWhiteSpace.UNSET; }

            @Override
            public String wordWrap() { return ""; }

            @Override
            public String textOverflow() { return ""; }

            @Override
            public String textShadow() { return ""; }

            @Override
            public net.ooder.annotation.ui.css.enums.CSVerticalAlign verticalAlign() { return net.ooder.annotation.ui.css.enums.CSVerticalAlign.UNSET; }

            @Override
            public ButtonPreset buttonPreset() { return ButtonPreset.UNSET; }

            @Override
            public InputPreset inputPreset() { return InputPreset.UNSET; }
        };
    }

    public CSLayout getLayout() {
        return new CSLayout() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() { return CSLayout.class; }

            @Override
            public net.ooder.annotation.ui.css.enums.CSDisplay display() { return net.ooder.annotation.ui.css.enums.CSDisplay.UNSET; }

            @Override
            public net.ooder.annotation.ui.css.enums.CSPosition position() { return net.ooder.annotation.ui.css.enums.CSPosition.UNSET; }

            @Override
            public String top() { return ""; }

            @Override
            public String left() { return ""; }

            @Override
            public String right() { return ""; }

            @Override
            public String bottom() { return ""; }

            @Override
            public String width() { return ""; }

            @Override
            public String height() { return ""; }

            @Override
            public String minWidth() { return ""; }

            @Override
            public String maxWidth() { return ""; }

            @Override
            public String minHeight() { return ""; }

            @Override
            public String maxHeight() { return ""; }

            @Override
            public String padding() { return "2px 8px"; }

            @Override
            public String paddingLeft() { return ""; }

            @Override
            public String paddingRight() { return ""; }

            @Override
            public String paddingTop() { return ""; }

            @Override
            public String paddingBottom() { return ""; }

            @Override
            public String margin() { return ""; }

            @Override
            public String marginLeft() { return ""; }

            @Override
            public String marginRight() { return ""; }

            @Override
            public String marginTop() { return ""; }

            @Override
            public String marginBottom() { return ""; }

            @Override
            public net.ooder.annotation.ui.css.enums.CSOverflow overflow() { return net.ooder.annotation.ui.css.enums.CSOverflow.UNSET; }

            @Override
            public net.ooder.annotation.ui.css.enums.CSOverflow overflowX() { return net.ooder.annotation.ui.css.enums.CSOverflow.UNSET; }

            @Override
            public net.ooder.annotation.ui.css.enums.CSOverflow overflowY() { return net.ooder.annotation.ui.css.enums.CSOverflow.UNSET; }

            @Override
            public String zIndex() { return ""; }

            @Override
            public String floatValue() { return ""; }

            @Override
            public String clear() { return ""; }

            @Override
            public net.ooder.annotation.ui.css.enums.CSVisibility visibility() { return net.ooder.annotation.ui.css.enums.CSVisibility.UNSET; }

            @Override
            public String opacity() { return ""; }

            @Override
            public net.ooder.annotation.ui.css.enums.CSCursor cursor() { return net.ooder.annotation.ui.css.enums.CSCursor.UNSET; }

            @Override
            public String pointerEvents() { return ""; }

            @Override
            public String userSelect() { return ""; }

            @Override
            public net.ooder.annotation.ui.css.enums.CSBoxSizing boxSizing() { return net.ooder.annotation.ui.css.enums.CSBoxSizing.UNSET; }

            @Override
            public ButtonPreset buttonPreset() { return ButtonPreset.UNSET; }

            @Override
            public InputPreset inputPreset() { return InputPreset.UNSET; }
        };
    }

    public CSBorder getBorder() {
        return new CSBorder() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() { return CSBorder.class; }

            @Override
            public String border() { return ""; }

            @Override
            public String borderWidth() { return ""; }

            @Override
            public net.ooder.annotation.ui.css.enums.CSBorderStyle borderStyle() { return net.ooder.annotation.ui.css.enums.CSBorderStyle.UNSET; }

            @Override
            public String borderColor() { return ""; }

            @Override
            public String borderRadius() { return "4px"; }

            @Override
            public String borderTop() { return ""; }

            @Override
            public String borderRight() { return ""; }

            @Override
            public String borderBottom() { return ""; }

            @Override
            public String borderLeft() { return ""; }

            @Override
            public String background() { return ""; }

            @Override
            public String backgroundColor() {
                switch (TagPreset.this) {
                    case UNSET: return "";
                    case DEFAULT: return "#f0f0f0";
                    case PRIMARY: return "#1890ff";
                    case SUCCESS: return "#52c41a";
                    case WARNING: return "#faad14";
                    case DANGER: return "#ff4d4f";
                    default: return "";
                }
            }

            @Override
            public String backgroundImage() { return ""; }

            @Override
            public String backgroundSize() { return ""; }

            @Override
            public String backgroundPosition() { return ""; }

            @Override
            public String backgroundRepeat() { return ""; }

            @Override
            public String boxShadow() { return ""; }

            @Override
            public String outline() { return ""; }

            @Override
            public String outlineColor() { return ""; }

            @Override
            public String outlineWidth() { return ""; }

            @Override
            public net.ooder.annotation.ui.css.enums.CSBorderStyle outlineStyle() { return net.ooder.annotation.ui.css.enums.CSBorderStyle.UNSET; }

            @Override
            public ButtonPreset buttonPreset() { return ButtonPreset.UNSET; }

            @Override
            public InputPreset inputPreset() { return InputPreset.UNSET; }
        };
    }
}
