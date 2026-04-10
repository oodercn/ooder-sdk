package net.ooder.annotation.fchart;

import net.ooder.annotation.Enums;

public enum RawDataEnums implements Enums, IRawData {
    January("January", "17400"),
    test2("test2", "12400", "FF0000", true),
    test3("test3", "17400", "FF5904"),
    February("February", "19800"),
    March("March", "21800"),
    April("April", "23800"),
    May("May", "29600"),
    June("June", "27600");
    public String id;
    public Object value;
    public String label;
    public String displayvalue;
    public String color;
    public String link;
    public String tooltext;
    public Boolean showlabel;
    public Boolean showvalue;
    public Boolean dashed;
    public Integer alpha;
    public String labelfont;
    public String labelfontcolor;
    public Integer labelfontsize;
    public Boolean labelfontbold;
    public String labelfontitalic;
    public String labelbgcolor;
    public String labelbordercolor;
    public Integer labelalpha;
    public Integer labelbgalpha;
    public Integer labelborderalpha;
    public Integer labelborderpadding;
    public Integer labelborderradius;
    public Integer labelborderthickness;
    public Boolean labelborderdashed;
    public Integer labelborderdashlen;
    public Integer labelborderdashgap;
    public String labellink;
    public String euClassName;
    public String bindClassName;

    RawDataEnums(String label, String value) {
        this.id = name();
        this.label = label;
        this.value = value;
    }

    RawDataEnums(String label, String value, String color) {
        this.id = name();
        this.label = label;
        this.value = value;
        this.color = color;
    }

    RawDataEnums(String label, String value, String color, boolean dashed) {
        this.id = name();
        this.label = label;
        this.value = value;
        this.color = color;
        this.dashed = dashed;
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

    public String getBindClassName() {
        return bindClassName;
    }

    public void setBindClassName(String bindClassName) {
        this.bindClassName = bindClassName;
    }

    public String getUiClassName() {
        return euClassName;
    }

    public void setUiClassName(String euClassName) {
        this.euClassName = euClassName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


    public String getDisplayvalue() {
        return displayvalue;
    }

    public void setDisplayvalue(String displayvalue) {
        this.displayvalue = displayvalue;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTooltext() {
        return tooltext;
    }

    public void setTooltext(String tooltext) {
        this.tooltext = tooltext;
    }


    public Boolean getShowlabel() {
        return showlabel;
    }

    public void setShowlabel(Boolean showlabel) {
        this.showlabel = showlabel;
    }

    public Boolean getShowvalue() {
        return showvalue;
    }

    public void setShowvalue(Boolean showvalue) {
        this.showvalue = showvalue;
    }

    public String getLabelfont() {
        return labelfont;
    }

    public void setLabelfont(String labelfont) {
        this.labelfont = labelfont;
    }

    public String getLabelfontcolor() {
        return labelfontcolor;
    }

    public void setLabelfontcolor(String labelfontcolor) {
        this.labelfontcolor = labelfontcolor;
    }


    public String getLabelfontitalic() {
        return labelfontitalic;
    }

    public void setLabelfontitalic(String labelfontitalic) {
        this.labelfontitalic = labelfontitalic;
    }

    public String getLabelbgcolor() {
        return labelbgcolor;
    }

    public void setLabelbgcolor(String labelbgcolor) {
        this.labelbgcolor = labelbgcolor;
    }

    public String getLabelbordercolor() {
        return labelbordercolor;
    }

    public void setLabelbordercolor(String labelbordercolor) {
        this.labelbordercolor = labelbordercolor;
    }

    public Boolean getDashed() {
        return dashed;
    }

    public void setDashed(Boolean dashed) {
        this.dashed = dashed;
    }

    public Integer getAlpha() {
        return alpha;
    }

    public void setAlpha(Integer alpha) {
        this.alpha = alpha;
    }

    public Integer getLabelfontsize() {
        return labelfontsize;
    }

    public void setLabelfontsize(Integer labelfontsize) {
        this.labelfontsize = labelfontsize;
    }

    public Boolean getLabelfontbold() {
        return labelfontbold;
    }

    public void setLabelfontbold(Boolean labelfontbold) {
        this.labelfontbold = labelfontbold;
    }

    public Integer getLabelalpha() {
        return labelalpha;
    }

    public void setLabelalpha(Integer labelalpha) {
        this.labelalpha = labelalpha;
    }

    public Integer getLabelbgalpha() {
        return labelbgalpha;
    }

    public void setLabelbgalpha(Integer labelbgalpha) {
        this.labelbgalpha = labelbgalpha;
    }

    public Integer getLabelborderalpha() {
        return labelborderalpha;
    }

    public void setLabelborderalpha(Integer labelborderalpha) {
        this.labelborderalpha = labelborderalpha;
    }

    public Integer getLabelborderpadding() {
        return labelborderpadding;
    }

    public void setLabelborderpadding(Integer labelborderpadding) {
        this.labelborderpadding = labelborderpadding;
    }

    public Integer getLabelborderradius() {
        return labelborderradius;
    }

    public void setLabelborderradius(Integer labelborderradius) {
        this.labelborderradius = labelborderradius;
    }

    public Integer getLabelborderthickness() {
        return labelborderthickness;
    }

    public void setLabelborderthickness(Integer labelborderthickness) {
        this.labelborderthickness = labelborderthickness;
    }

    public Boolean getLabelborderdashed() {
        return labelborderdashed;
    }

    public void setLabelborderdashed(Boolean labelborderdashed) {
        this.labelborderdashed = labelborderdashed;
    }

    public Integer getLabelborderdashlen() {
        return labelborderdashlen;
    }

    public void setLabelborderdashlen(Integer labelborderdashlen) {
        this.labelborderdashlen = labelborderdashlen;
    }

    public Integer getLabelborderdashgap() {
        return labelborderdashgap;
    }

    public void setLabelborderdashgap(Integer labelborderdashgap) {
        this.labelborderdashgap = labelborderdashgap;
    }

    public String getLabellink() {
        return labellink;
    }

    public void setLabellink(String labellink) {
        this.labellink = labellink;
    }


}
