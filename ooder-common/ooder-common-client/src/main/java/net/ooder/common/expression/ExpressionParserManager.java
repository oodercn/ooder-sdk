/**
 * $RCSfile: ExpressionParserManager.java,v $
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
package net.ooder.common.expression;



public class ExpressionParserManager
{
    public ExpressionParserManager()
    {
    }

    public static ExpressionParser getExpressionParser()
    {
        return new JEP();
    }
}