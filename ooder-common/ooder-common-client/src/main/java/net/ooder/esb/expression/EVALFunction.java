/**
 * $RCSfile: EVALFunction.java,v $
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

import net.ooder.common.expression.function.AbstractFunction;
import net.ooder.esb.util.EsbFactory;
import net.ooder.jds.core.esb.EsbUtil;

public  class EVALFunction extends AbstractFunction {

	public String expressionStr;
	public EVALFunction(String expressionStr){
		super();
		this.expressionStr=expressionStr;

	}
	public Object perform() {
//		ExpressionParser parser = WorkflowExpressionParserManager.getExpressionParser(null);
//        parser.parseExpression(expressionStr);
//		Object obj=parser.getValueAsObject();

		Object obj = EsbUtil.parExpression(expressionStr);
		return obj;

	}
}