/**
 * $RCSfile: CustomMacro.java,v $
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
package net.ooder.esb.expression;

import org.mvel2.Macro;

public class CustomMacro implements Macro {

    public String key;

    public CustomMacro(String key) {
        this.key = key;
    }

    @Override
    public String doMacro() {
        return "$Custom['" + key + "']";
    }
}