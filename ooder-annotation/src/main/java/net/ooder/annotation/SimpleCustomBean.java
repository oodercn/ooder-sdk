package net.ooder.annotation;

import com.alibaba.fastjson.annotation.JSONField;

import java.lang.annotation.Annotation;

public class SimpleCustomBean implements CustomBean {

    @JSONField(serialize = false)
    private final Class annotationClass;

    public SimpleCustomBean(Class annotationClass) {
        this.annotationClass = annotationClass;
    }

    @Override
    public String toAnnotationStr() {
        StringBuffer buffer = new StringBuffer("@");
        buffer.append(annotationClass.getSimpleName());
        return buffer.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SimpleCustomBean){
            SimpleCustomBean custombean= (SimpleCustomBean)obj;
            return custombean.getAnnotationClass().equals(this.getAnnotationClass());
        }
        return super.equals(obj);
    }

    public Class<Annotation> getAnnotationClass() {
        return annotationClass;
    }
}
