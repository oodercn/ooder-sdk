package net.ooder.config;

import java.util.HashMap;
import java.util.Map;

public class DSMResultModel<T> extends ResultModel<T> {


    public Map<String, String> funs = new HashMap<>();

    public Map<String, String> getFuns() {
        return funs;
    }

    public void setFuns(Map<String, String> funs) {
        this.funs = funs;
    }


}
