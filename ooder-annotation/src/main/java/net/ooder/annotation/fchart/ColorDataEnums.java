package net.ooder.annotation.fchart;


import net.ooder.annotation.Enums;

public enum ColorDataEnums implements Enums, IColor {

    line("Average", 200,500, "00CC00");
    String id;
    Integer minvalue;
    Integer maxvalue;
    String code;
    String label;
    Integer alpha;
    String bordercolor;
    Integer borderalpha;

    ColorDataEnums(String label, Integer minvalue, Integer maxvalue,String code) {
        this.id = name();
        this.minvalue = minvalue;
        this.maxvalue = maxvalue;
        this.label = label;
        this.code=code;
    }


    @Override
    public Integer getMinvalue() {
        return minvalue;
    }

    @Override
    public void setMinvalue(Integer minvalue) {
        this.minvalue = minvalue;
    }

    @Override
    public Integer getMaxvalue() {
        return maxvalue;
    }

    @Override
    public void setMaxvalue(Integer maxvalue) {
        this.maxvalue = maxvalue;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public Integer getAlpha() {
        return alpha;
    }

    @Override
    public void setAlpha(Integer alpha) {
        this.alpha = alpha;
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
    public Integer getBorderalpha() {
        return borderalpha;
    }

    @Override
    public void setBorderalpha(Integer borderalpha) {
        this.borderalpha = borderalpha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
