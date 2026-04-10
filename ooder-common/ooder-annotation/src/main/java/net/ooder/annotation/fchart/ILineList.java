package net.ooder.annotation.fchart;


import net.ooder.annotation.ui.HAlignType;
import net.ooder.annotation.ui.VAlignType;

public interface ILineList {


    public String getId();

    public void setId(String id);

    public Object getValue();

    public void setValue(Object value);

    public Integer getLineposition();

    public void setLineposition(Integer lineposition);

    public Boolean getShowlabelborder();

    public void setShowlabelborder(Boolean showlabelborder);

    public String getLabel();

    public void setLabel(String label);

    public Integer getLabelposition();

    public void setLabelposition(Integer labelposition);

    public HAlignType getLabelhalign();

    public void setLabelhalign(HAlignType labelhalign);

    public VAlignType getLabelvalign();

    public void setLabelvalign(VAlignType labelvalign);

    public Integer getStartindex();

    public void setStartindex(Integer startindex);

    public Integer getEndindex();

    public void setEndindex(Integer endindex);

    public Boolean getDisplayalways();

    public void setDisplayalways(Boolean displayalways);

    public Integer getDisplaywhencount();

    public void setDisplaywhencount(Integer displaywhencount);

    public Boolean getValueontop();

    public void setValueontop(Boolean valueontop);

    public ValuePosition getParentyaxis();

    public void setParentyaxis(ValuePosition parentyaxis);

    public Integer getEndvalue();

    public void setEndvalue(Integer endvalue);

    public Boolean getIstrendzone();

    public void setIstrendzone(Boolean istrendzone);

    public Integer getThickness();

    public void setThickness(Integer thickness);

    public Integer getAlpha();

    public void setAlpha(Integer alpha);

    public Boolean getDashed();

    public void setDashed(Boolean dashed);

    public Integer getDashlen();

    public void setDashlen(Integer dashlen);

    public Integer getDashgap();

    public void setDashgap(Integer dashgap);

    public Boolean getValueonright();

    public void setValueonright(Boolean valueonright);

    public String getTooltext();

    public void setTooltext(String tooltext);

    public Integer getStartvalue();

    public void setStartvalue(Integer startvalue);

    public String getColor();

    public void setColor(String color);

    public String getDisplayvalue();

    public void setDisplayvalue(String displayvalue);

    public Boolean getShowontop();

    public void setShowontop(Boolean showontop);

    public String getCaption();

    public void setCaption(String caption);

    public String getUiClassName();

    public void setUiClassName(String euClassName);

    public String getBindClassName();

    public void setBindClassName(String bindClassName);
}
