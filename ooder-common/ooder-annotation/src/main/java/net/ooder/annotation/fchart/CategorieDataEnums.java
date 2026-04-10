package net.ooder.annotation.fchart;


import net.ooder.annotation.Enums;

public enum CategorieDataEnums implements Enums, ICategorie {

    line("测试", true, "", "测试");
    String label;

    String id;

    Boolean showlabel;

    String tooltext;

    String font;

    String fontcolor;

    Boolean fontbold;

    Boolean fontitalic;

    String bgcolor;

    String bordercolor;

    Integer alpha;

    Integer bgalpha;

    Integer borderalpha;

    Integer borderpadding;

    Integer borderradius;

    Integer borderthickness;

    Boolean borderdashed;

    Integer borderdashLen;

    Integer borderdashgap;

    String link;


    CategorieDataEnums(String label, Boolean showlabel, String link, String tooltext) {
        this.id = name();
        this.label = label;
        this.showlabel = showlabel;
        this.link = link;
        this.tooltext = tooltext;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean getShowlabel() {
        return showlabel;
    }

    public void setShowlabel(Boolean showlabel) {
        this.showlabel = showlabel;
    }

    public String getTooltext() {
        return tooltext;
    }

    public void setTooltext(String tooltext) {
        this.tooltext = tooltext;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public String getFontcolor() {
        return fontcolor;
    }

    public void setFontcolor(String fontcolor) {
        this.fontcolor = fontcolor;
    }

    public Boolean getFontbold() {
        return fontbold;
    }

    public void setFontbold(Boolean fontbold) {
        this.fontbold = fontbold;
    }

    public Boolean getFontitalic() {
        return fontitalic;
    }

    public void setFontitalic(Boolean fontitalic) {
        this.fontitalic = fontitalic;
    }

    public String getBgcolor() {
        return bgcolor;
    }

    public void setBgcolor(String bgcolor) {
        this.bgcolor = bgcolor;
    }

    @Override
    public String getBordercolor() {
        return bordercolor;
    }

    @Override
    public void setBordercolor(String bordercolor) {
        this.bordercolor = bordercolor;
    }

    @Override
    public Integer getAlpha() {
        return alpha;
    }

    @Override
    public void setAlpha(Integer alpha) {
        this.alpha = alpha;
    }

    public Integer getBgalpha() {
        return bgalpha;
    }

    public void setBgalpha(Integer bgalpha) {
        this.bgalpha = bgalpha;
    }

    public Integer getBorderalpha() {
        return borderalpha;
    }

    public void setBorderalpha(Integer borderalpha) {
        this.borderalpha = borderalpha;
    }

    public Integer getBorderpadding() {
        return borderpadding;
    }

    public void setBorderpadding(Integer borderpadding) {
        this.borderpadding = borderpadding;
    }

    public Integer getBorderradius() {
        return borderradius;
    }

    public void setBorderradius(Integer borderradius) {
        this.borderradius = borderradius;
    }

    public Integer getBorderthickness() {
        return borderthickness;
    }

    public void setBorderthickness(Integer borderthickness) {
        this.borderthickness = borderthickness;
    }

    public Boolean getBorderdashed() {
        return borderdashed;
    }

    public void setBorderdashed(Boolean borderdashed) {
        this.borderdashed = borderdashed;
    }

    public Integer getBorderdashLen() {
        return borderdashLen;
    }

    public void setBorderdashLen(Integer borderdashLen) {
        this.borderdashLen = borderdashLen;
    }

    public Integer getBorderdashgap() {
        return borderdashgap;
    }

    public void setBorderdashgap(Integer borderdashgap) {
        this.borderdashgap = borderdashgap;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
