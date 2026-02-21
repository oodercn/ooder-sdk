/**
 * $RCSfile: UMinus.java,v $
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
package net.ooder.common.expression.function.math;

import net.ooder.common.expression.*;
import net.ooder.common.expression.function.*;
import java.util.*;

public class UMinus extends Function
{
	public UMinus() {
		numberOfParameters = 1;
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);// check the stack

		Object param = inStack.pop();

		inStack.push(umin(param));
		return;
	}

	protected Object umin(Object param) throws ParseException {
        if (param == null){
            return null;
        }

		if (param instanceof Number) {
			return new Double(-((Number)param).doubleValue());
		}

        //List
        if (param instanceof List)
        {
            List list = (List) param;
            int n = list.size();
            List result = new ArrayList(n);
            for (int i = 0; i < n; i++)
            {
                result.add(umin(list.get(i)));
            }
            return result;
        }

		throw new ParseException("Invalid parameter type");
	}
}
