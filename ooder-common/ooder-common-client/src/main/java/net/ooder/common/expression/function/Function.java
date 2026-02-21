/**
 * $RCSfile: Function.java,v $
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
package net.ooder.common.expression.function;
import java.util.*;

import net.ooder.common.expression.*;

/**
 * Function classes extend this class. It is an implementation of the
 * FunctionI interface.
 * <p>
 * It includes a numberOfParameters member, that is checked when parsing the
 * expression. This member should be initialized to an appropriate value for
 * all classes extending this class. If an arbitrary number of parameters
 * should be allowed, initialize this member to -1.
 */
public abstract class Function implements FunctionI
{
	/**
	 * Number of parameters a the function requires. Initialize this value to
	 * -1 if any number of parameters should be allowed.
	 */
	protected int numberOfParameters;

	/**
	 * Number of parameters to be used for the next run() invocation. Applies
	 * only if the required umber of parameters is variable
	 * (numberOfParameters = -1).
	 */
	protected int curNumberOfParameters;

	/**
	 * Creates a new PostfixMathCommand class.
	 */
	public Function() {
		numberOfParameters = 0;
		curNumberOfParameters = 0;
	}

	/**
	 * Check whether the stack is not null, throw a ParseException if it is.
	 */
	protected void checkStack(Stack inStack) throws ParseException {
		/* Check if stack is null */
		if (null == inStack) {
			throw new ParseException("Stack argument null");
		}
	}

	/**
	 * Return the required number of parameters.
	 */
	public int getNumberOfParameters() {
		return numberOfParameters;
	}

	/**
	 * Sets the number of current number of parameters used in the next call
	 * of run(). This method is only called when the reqNumberOfParameters is
	 * -1.
	 */
	public void setCurNumberOfParameters(int n) {
		curNumberOfParameters = n;
	}

	/**
	 * Throws an exception because this method should never be called under
	 * normal circumstances. Each function should use it's own run() method
	 * for evaluating the function. This includes popping off the parameters
	 * from the stack, and pushing the result back on the stack.
	 */
	public abstract void run(Stack s) throws ParseException;
}
