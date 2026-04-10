package net.ooder.annotation.fchart;

import net.ooder.annotation.Enums;
import net.ooder.annotation.ui.HAlignType;
import net.ooder.annotation.ui.VAlignType;

public enum LineDataEnums implements Enums, ILineList {

    line("Average", 22000, "00CC00");

    public String id;
    public Object value;
    public String caption;
    public String euClassName;
    public String bindClassName;

    public Integer startvalue;
    public String color;
    public String displayvalue;
    public Boolean showontop;

    public ValuePosition parentyaxis;
    public Integer endvalue;
    public Boolean istrendzone;
    public Integer thickness;
    public Integer alpha;
    public Boolean dashed;
    public Integer dashlen;
    public Integer dashgap;
    public Boolean valueonright;
    public String tooltext;
    //VLine
    public Integer lineposition;
    public Boolean showlabelborder;
    public String label;
    public Integer labelposition;
    public HAlignType labelhalign;
    public VAlignType labelvalign;
    public Integer startindex;
    public Integer endindex;
    public Boolean displayalways;
    public Integer displaywhencount;
    public Boolean valueontop;

    LineDataEnums(String label, String value) {
        this.id = name();
        this.label = label;
        this.value = value;
    }

    LineDataEnums(String displayvalue, Integer startvalue, String color) {
        this.id = name();
        this.displayvalue = displayvalue;
        this.startvalue = startvalue;
        this.color = color;
    }

    @Override
    public Integer getStartvalue() {
        return startvalue;
    }

    @Override
    public void setStartvalue(Integer startvalue) {
        this.startvalue = startvalue;
    }

    @Override
    public String getColor() {
        return color;
    }

    @Override
    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String getDisplayvalue() {
        return displayvalue;
    }

    public void setDisplayvalue(String displayvalue) {
        this.displayvalue = displayvalue;
    }

    @Override
    public Boolean getShowontop() {
        return showontop;
    }

    @Override
    public void setShowontop(Boolean showontop) {
        this.showontop = showontop;
    }

    @Override
    public ValuePosition getParentyaxis() {
        return parentyaxis;
    }

    @Override
    public void setParentyaxis(ValuePosition parentyaxis) {
        this.parentyaxis = parentyaxis;
    }

    @Override
    public Integer getEndvalue() {
        return endvalue;
    }

    @Override
    public void setEndvalue(Integer endvalue) {
        this.endvalue = endvalue;
    }

    @Override
    public Boolean getIstrendzone() {
        return istrendzone;
    }

    @Override
    public void setIstrendzone(Boolean istrendzone) {
        this.istrendzone = istrendzone;
    }

    @Override
    public Integer getThickness() {
        return thickness;
    }

    @Override
    public void setThickness(Integer thickness) {
        this.thickness = thickness;
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
    public Boolean getDashed() {
        return dashed;
    }

    @Override
    public void setDashed(Boolean dashed) {
        this.dashed = dashed;
    }

    @Override
    public Integer getDashlen() {
        return dashlen;
    }

    @Override
    public void setDashlen(Integer dashlen) {
        this.dashlen = dashlen;
    }

    @Override
    public Integer getDashgap() {
        return dashgap;
    }

    @Override
    public void setDashgap(Integer dashgap) {
        this.dashgap = dashgap;
    }

    @Override
    public Boolean getValueonright() {
        return valueonright;
    }

    @Override
    public void setValueonright(Boolean valueonright) {
        this.valueonright = valueonright;
    }

    @Override
    public String getTooltext() {
        return tooltext;
    }

    @Override
    public void setTooltext(String tooltext) {
        this.tooltext = tooltext;
    }

    @Override
    public Integer getLineposition() {
        return lineposition;
    }

    @Override
    public void setLineposition(Integer lineposition) {
        this.lineposition = lineposition;
    }

    @Override
    public Boolean getShowlabelborder() {
        return showlabelborder;
    }

    @Override
    public void setShowlabelborder(Boolean showlabelborder) {
        this.showlabelborder = showlabelborder;
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
    public Integer getLabelposition() {
        return labelposition;
    }

    @Override
    public void setLabelposition(Integer labelposition) {
        this.labelposition = labelposition;
    }

    @Override
    public HAlignType getLabelhalign() {
        return labelhalign;
    }

    @Override
    public void setLabelhalign(HAlignType labelhalign) {
        this.labelhalign = labelhalign;
    }

    @Override
    public VAlignType getLabelvalign() {
        return labelvalign;
    }

    @Override
    public void setLabelvalign(VAlignType labelvalign) {
        this.labelvalign = labelvalign;
    }

    @Override
    public Integer getStartindex() {
        return startindex;
    }

    @Override
    public void setStartindex(Integer startindex) {
        this.startindex = startindex;
    }

    @Override
    public Integer getEndindex() {
        return endindex;
    }

    @Override
    public void setEndindex(Integer endindex) {
        this.endindex = endindex;
    }

    @Override
    public Boolean getDisplayalways() {
        return displayalways;
    }

    @Override
    public void setDisplayalways(Boolean displayalways) {
        this.displayalways = displayalways;
    }

    @Override
    public Integer getDisplaywhencount() {
        return displaywhencount;
    }

    @Override
    public void setDisplaywhencount(Integer displaywhencount) {
        this.displaywhencount = displaywhencount;
    }

    @Override
    public Boolean getValueontop() {
        return valueontop;
    }

    @Override
    public void setValueontop(Boolean valueontop) {
        this.valueontop = valueontop;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;

    }

    @Override
    public String getCaption() {
        return caption;
    }

    @Override
    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Override
    public String getUiClassName() {
        return euClassName;
    }

    @Override
    public void setUiClassName(String euClassName) {
        this.euClassName = euClassName;
    }

    @Override
    public String getBindClassName() {
        return bindClassName;
    }

    @Override
    public void setBindClassName(String bindClassName) {
        this.bindClassName = bindClassName;
    }
}
