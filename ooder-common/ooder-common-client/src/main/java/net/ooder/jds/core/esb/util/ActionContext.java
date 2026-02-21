
/**
 * $RCSfile: ActionContext.java,v $
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
package net.ooder.jds.core.esb.util;


import net.ooder.esb.config.manager.JDSExpressionParserManager;
import org.mvel2.ParserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ActionContext extends ParserContext implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(ActionContext.class);

    static ThreadLocal actionContext = new ActionContextThreadLocal();


    /**
     * Constant that indicates the action is running under a "development mode".
     * This mode provides more feedback that is useful for developers but probably
     * too verbose/error prone for production.
     */
    //public static final String DEV_MODE = "__devMode";

    /**
     * Constant for the name of the action being executed.
     */
    public static final String ACTION_NAME = "net.ooder.jds.core.esb.ActionContext.name";

    /**
     * Constant for the {@link net.ooder.jds.core.esb.util.ValueStack OGNL value stack}.
     */
    public static final String VALUE_STACK = OgnlValueStack.VALUE_STACK;

    /**
     * Constant for the action's session.
     */
    public static final String SESSION = "net.ooder.jds.core.esb.ActionContext.session";

    /**
     * Constant for the action's session.
     */
    public static final String REQUEST = "net.ooder.jds.core.esb.ActionContext.request";

    /**
     * Constant for the action's application context.
     */
    public static final String APPLICATION = "net.ooder.jds.core.esb.ActionContext.application";

    /**
     * Constant for the action's parameters.
     */
    public static final String PARAMETERS = "net.ooder.jds.core.esb.ActionContext.parameters";

    /**
     * Constant for the action's locale.
     */
    public static final String LOCALE = "net.ooder.jds.core.esb.ActionContext.locale";

    /**
     * Constant for the action's type converter.
     */
    public static final String TYPE_CONVERTER = "net.ooder.jds.core.esb.ActionContext.typeConverter";


    public static final String ACTION_INVOCATION = "net.ooder.jds.core.esb.ActionContext.actionInvocation";

    /**
     * Constant for the map of type conversion errors.
     */
    public static final String CONVERSION_ERRORS = "net.ooder.jds.core.esb.ActionContext.conversionErrors";


    Map context;


    /**
     * Creates a new ActionContext initialized with another context.
     *
     * @param context a context map.
     */
    public ActionContext(Map context) {
        //super(context);
        this.context = context;
    }


    /**
     * Sets the action's application context.
     *
     * @param application the action's application context.
     */
    public void setApplication(Map application) {
        put(APPLICATION, application);
    }

    /**
     * Returns a Map of the ServletContext when in a servlet environment or a generic application level Map otherwise.
     *
     * @return a Map of ServletContext or generic application level Map
     */
    public Map getApplication() {
        return (Map) get(APPLICATION);
    }

    /**
     * Sets the action context for the current thread.
     *
     * @param context the action context.
     */
    public static void setContext(ActionContext context) {
        actionContext.set(context);
    }

    /**
     * Returns the ActionContext specific to the current thread.
     *
     * @return the ActionContext for the current thread, is never <tt>null</tt>.
     */
    public static ActionContext getContext() {
        ActionContext context = (ActionContext) actionContext.get();
//        ParserContext parserContext = new ParserContext();
//        parserContext.setImports(JDSExpressionParserManager.getExpressionTypeMap());
        ValueStack vs = ValueStackFactory.getFactory().createValueStack();
        if (context == null) {
            logger.warn("contextInfoIsNull");
            context = new ActionContext(vs.getContext());
            setContext(context);
        }
        //context.setContextMap(JDSActionContext.getActionContext().getContext());
        context.setValueStack(vs);

        return context;
    }

    /**
     * Sets the action's context map.
     *
     * @param contextMap the context map.
     */
    public void setContextMap(Map contextMap) {
        getContext().context = contextMap;
    }

    /**
     * Gets the context map.
     *
     * @return the context map.
     */
    public Map getContextMap() {
        return context;
    }

    /**
     * Sets conversion errors which occurred when executing the action.
     *
     * @param conversionErrors a Map of errors which occurred when executing the action.
     */
    public void setConversionErrors(Map conversionErrors) {
        put(CONVERSION_ERRORS, conversionErrors);
    }

    /**
     * Gets the map of conversion errors which occurred when executing the action.
     *
     * @return the map of conversion errors which occurred when executing the action or an empty map if
     * there were no errors.
     */
    public Map getConversionErrors() {
        Map errors = (Map) get(CONVERSION_ERRORS);

        if (errors == null) {
            errors = new HashMap();
            setConversionErrors(errors);
        }

        return errors;
    }

    public void remove() {
        actionContext.remove();
    }

    /**
     * Sets the Locale for the current action.
     *
     * @param locale the Locale for the current action.
     */
    public void setLocale(Locale locale) {
        put(LOCALE, locale);
    }

    /**
     * Gets the Locale of the current action. If no locale was ever specified the platform's
     * {@link Locale#getDefault() default locale} is used.
     *
     * @return the Locale of the current action.
     */
    public Locale getLocale() {
        Locale locale = (Locale) get(LOCALE);

        if (locale == null) {
            locale = Locale.getDefault();
            setLocale(locale);
        }

        return locale;
    }

    /**
     * Sets the name of the current Action in the ActionContext.
     *
     * @param name the name of the current action.
     */
    public void setName(String name) {
        put(ACTION_NAME, name);
    }

    /**
     * Gets the name of the current Action.
     *
     * @return the name of the current action.
     */
    public String getName() {
        return (String) get(ACTION_NAME);
    }

    /**
     * Sets the action parameters.
     *
     * @param parameters the parameters for the current action.
     */
    public void setParameters(Map parameters) {
        put(PARAMETERS, parameters);
    }

    /**
     * Returns a Map of the HttpServletRequest parameters when in a servlet environment or a generic Map of
     * parameters otherwise.
     *
     * @return a Map of HttpServletRequest parameters or a multipart map when in a servlet environment, or a
     * generic Map of parameters otherwise.
     */
    public Map getParameters() {
        return (Map) get(PARAMETERS);
    }

    /**
     * Sets a map of action session values.
     *
     * @param session the session values.
     */
    public void setSession(Map session) {
        put(SESSION, session);
    }

    /**
     * Gets the Map of HttpSession values when in a servlet environment or a generic session map otherwise.
     *
     * @return the Map of HttpSession values when in a servlet environment or a generic session map otherwise.
     */
    public Map getSession() {
        return (Map) get(SESSION);
    }

    /**
     * Sets the OGNL value stack.
     *
     * @param stack the OGNL value stack.
     */
    public void setValueStack(ValueStack stack) {
        put(VALUE_STACK, stack);
    }

    /**
     * Gets the OGNL value stack.
     *
     * @return the OGNL value stack.
     */
    public ValueStack getValueStack() {
        return (ValueStack) get(VALUE_STACK);
    }

    /**
     * Returns a value that is stored in the current ActionContext by doing a lookup using the value's key.
     *
     * @param key the key used to find the value.
     * @return the value that was found using the key or <tt>null</tt> if the key was not found.
     */
    public Object get(Object key) {
        return context.get(key);
    }

    /**
     * Stores a value in the current ActionContext. The value can be looked up using the key.
     *
     * @param key   the key of the value.
     * @param value the value to be stored.
     */
    public void put(Object key, Object value) {
        context.put(key, value);
    }


    private static class ActionContextThreadLocal extends ThreadLocal {
        protected Object initialValue() {
            ValueStack vs = ValueStackFactory.getFactory().createValueStack();
            ActionContext context = new ActionContext(vs.getContext());
            context.setImports(JDSExpressionParserManager.getExpressionTypeMap());
            return context;
        }
    }
}
