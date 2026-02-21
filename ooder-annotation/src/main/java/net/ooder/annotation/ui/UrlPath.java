package net.ooder.annotation.ui;


import net.ooder.annotation.Enumstype;

public interface UrlPath<T extends Enumstype> {

    public T getType();

    public void setType(T type);

    public String getName();

    public void setName(String name);

    public String getPath();

    public void setPath(String path);
}
