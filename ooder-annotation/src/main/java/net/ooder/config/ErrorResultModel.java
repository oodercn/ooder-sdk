package net.ooder.config;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.common.JDSException;


public class ErrorResultModel<T> extends ResultModel<T> {


    public int errcode;
    public String errdes;


    @JSONField(serialize = false)
    public T getData() {
        return null;
    }

    public ErrorResultModel() {
        super();
        this.errdes = "server error!";
        this.errcode = 1000;
        this.requestStatus = -1;

    }

    public ErrorResultModel(String errdes) {
        super();
        this.errdes = (errdes == null ? "" : errdes);
        this.errcode = 1000;
        this.requestStatus = -1;

    }


    @Override
    @JSONField(serialize = false)
    public T get() throws JDSException {
        throw new JDSException(errdes, errcode);
    }

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrdes() {
        return errdes;
    }

    public void setErrdes(String errdes) {
        this.errdes = errdes;
    }

}
