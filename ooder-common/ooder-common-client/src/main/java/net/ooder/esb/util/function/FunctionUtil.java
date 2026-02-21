/**
 * $RCSfile: FunctionUtil.java,v $
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
package net.ooder.esb.util.function;

import com.alibaba.fastjson.util.TypeUtils;

import java.util.List;

public class FunctionUtil {
    public static Long sum(List<Object> numbers) {
        long sumk = 0;
        for (Object number : numbers) {
            sumk = sumk + TypeUtils.castToLong(number);
        }
        return sumk;
    }
}
