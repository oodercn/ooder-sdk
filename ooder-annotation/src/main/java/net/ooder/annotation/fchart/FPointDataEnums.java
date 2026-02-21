package net.ooder.annotation.fchart;


import net.ooder.annotation.Enums;

public enum FPointDataEnums implements Enums, IFPoint {

    line("Average", 22000, "00CC00");
    String id;
    Integer startvalue;
    Integer endvalue;
    String displayvalue;
    Boolean valueinside;
    String color;
    Integer alpha;
    Integer thickness;
    Boolean showborder;
    String bordercolor;
    Integer radius;
    Integer innerradius;
    Boolean dashed;
    Integer dashlen;
    Integer dashgap;
    Boolean usemarker;
    String markercolor;
    String markerbordercolor;
    Integer markerradius;
    String markertooltext;


    FPointDataEnums(String displayvalue, Integer startvalue, String color) {
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
    public Integer getEndvalue() {
        return endvalue;
    }

    @Override
    public void setEndvalue(Integer endvalue) {
        this.endvalue = endvalue;
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

    public Boolean getValueinside() {
        return valueinside;
    }

    public void setValueinside(Boolean valueinside) {
        this.valueinside = valueinside;
    }

    public Boolean getShowborder() {
        return showborder;
    }

    public void setShowborder(Boolean showborder) {
        this.showborder = showborder;
    }

    public String getBordercolor() {
        return bordercolor;
    }

    public void setBordercolor(String bordercolor) {
        this.bordercolor = bordercolor;
    }

    public Integer getRadius() {
        return radius;
    }

    public void setRadius(Integer radius) {
        this.radius = radius;
    }

    public Integer getInnerradius() {
        return innerradius;
    }

    public void setInnerradius(Integer innerradius) {
        this.innerradius = innerradius;
    }

    public Boolean getUsemarker() {
        return usemarker;
    }

    public void setUsemarker(Boolean usemarker) {
        this.usemarker = usemarker;
    }

    public String getMarkercolor() {
        return markercolor;
    }

    public void setMarkercolor(String markercolor) {
        this.markercolor = markercolor;
    }

    public String getMarkerbordercolor() {
        return markerbordercolor;
    }

    public void setMarkerbordercolor(String markerbordercolor) {
        this.markerbordercolor = markerbordercolor;
    }

    public Integer getMarkerradius() {
        return markerradius;
    }

    public void setMarkerradius(Integer markerradius) {
        this.markerradius = markerradius;
    }

    public String getMarkertooltext() {
        return markertooltext;
    }

    public void setMarkertooltext(String markertooltext) {
        this.markertooltext = markertooltext;
    }

    ;

    public String getId() {
        return id;
    }

    ;

    public void setId(String id) {
        this.id = id;
    }
}
