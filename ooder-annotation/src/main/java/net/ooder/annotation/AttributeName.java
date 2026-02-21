package net.ooder.annotation;



public interface AttributeName<T> extends Enumstype {

    public Class<? extends T> getClazz();

    public String getDisplayName();

}
