package net.ooder.annotation;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.annotation.IconEnumstype;


public interface TreeItem extends IconEnumstype {

    //兼容已有数据绑定转换
   // @JSONField(deserializeUsing = BindClassArrDeserializer.class)
    public Class[] getBindClass();

    public boolean isInitFold();

    public boolean isDynDestory();

    public boolean isLazyLoad();

}
