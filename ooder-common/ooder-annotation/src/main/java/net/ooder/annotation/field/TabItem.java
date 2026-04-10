package net.ooder.annotation.field;


import net.ooder.annotation.IconEnumstype;

public interface TabItem extends IconEnumstype {

    public Class[] getBindClass();

    public boolean isInitFold();

    public boolean isDynDestory();

    public String getTips();

    public String getCaption();

    public boolean isDynLoad();
}
