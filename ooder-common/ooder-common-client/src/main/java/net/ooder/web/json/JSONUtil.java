/**
 * $RCSfile: JSONUtil.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.web.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.ooder.annotation.EsbBeanAnnotation;


@EsbBeanAnnotation(id = "JSON")
public class JSONUtil {

    public String toString(Object object) {
        return JSON.toJSONString(object, true).toString();
    }

    public JSONObject parseObject(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        return jsonObject;
    }

    public JSONArray parseArray(String json) {
        JSONArray jsonObject = JSON.parseArray(json);
        return jsonObject;
    }


}
