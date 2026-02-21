/**
 * $RCSfile: EsbFactory.java,v $
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
package net.ooder.esb.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.ooder.annotation.EsbObj;
import net.ooder.annotation.EsbRequest;
import net.ooder.client.JDSSessionFactory;
import net.ooder.common.*;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.expression.ParseException;
import net.ooder.common.expression.function.Function;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.ClassUtility;
import net.ooder.config.JDSConfig;
import net.ooder.context.JDSActionContext;
import net.ooder.context.JDSContext;
import net.ooder.esb.config.invocation.DebugInterceptor;
import net.ooder.esb.config.manager.EsbBeanFactory;
import net.ooder.esb.config.manager.ExpressionTempBean;
import net.ooder.esb.config.manager.JDSExpressionParserManager;
import net.ooder.esb.config.manager.ServiceBean;
import net.ooder.esb.expression.*;
import net.ooder.jds.core.esb.util.ActionContext;
import net.ooder.server.JDSClientService;
import net.sf.cglib.core.Signature;
import net.sf.cglib.core.TypeUtils;
import net.sf.cglib.proxy.*;
import org.mvel2.MVEL;
import org.mvel2.Macro;
import org.mvel2.MacroProcessor;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExpressionCompiler;
import org.mvel2.integration.Interceptor;
import org.mvel2.util.ArrayTools;
import org.mvel2.util.MethodStub;
import org.mvel2.util.ParseTools;
import org.objectweb.asm.Type;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;


public class EsbFactory {
    private static final Log logger = LogFactory.getLog(ESBConstants.CONFIG_KEY, EsbFactory.class);
    private final static Map signatureMap = new HashMap();
    private final static Map<String, ExpressionCompiler> compilerMap = new HashMap<String, ExpressionCompiler>();

    private static Class[] JSONClass = new Class[]{JSONObject.class, JSONArray.class};

    private static Class contextClass;
    private static Object esbContext;
    private final static String EsbKey = "$";
    private final static String ESBMVELSOURCE = "ESBMVELSOURCE";


    private static boolean init = false;

    public synchronized static void initBus() {
        try {
            if (!init) {
                loadStaticAllData(true);
                EsbFactory.getEsbContext();
                init = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public synchronized static void reload() {
        creatStaticBean(true);
    }

    /***
     * get the context root
     * @param load whether reload the context.if true reload it
     * @return the context map
     */
    public static Map<String, Object> getContextRoot(boolean load) {
        Map<String, Object> contextRoot = new HashMap<String, Object>();

        EsbBeanFactory factory = EsbBeanFactory.getInstance();
        List expressionList = factory.loadAllServiceBean();
        for (int i = 0; i < expressionList.size(); i++) {
            if (!(expressionList.get(i) instanceof ExpressionTempBean)) {
                continue;
            }
            ExpressionTempBean bean = (ExpressionTempBean) expressionList.get(i);
            if (load || (bean.getDataType() != null && bean.getDataType().equals(ContextType.Action))) {
                String id = bean.getId();
                contextRoot.put(EsbKey + id, null);
                contextRoot.put(id, null);
            }
            // contextRoot.put(bean.getId(), null);
        }

        return contextRoot;
    }


    static Object eval(String expression) {
        return MVEL.eval(expression, creatStaticBean(false));
    }

    public static Object par(String expression) {
        return par(expression, Object.class);
    }


    public static Method getESBMethod(Class clazz) {
        ExpressionTempBean bean = EsbBeanFactory.getInstance().getDefaultServiceBean(clazz);
        if (bean != null) {
            return getESBMethod(bean.getId());
        }
        return null;
    }

    public static Method getESBMethod(String expression) {
        if (expression != null) {
            if (!expression.startsWith(EsbKey)) {
                expression = EsbKey + expression;
            }
            Method[] methods = getEsbContext().getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals("get" + expression)) {
                    return method;
                }
            }
        }
        return null;
    }


    public static <T> T par(Class<T> clazz) {

        T object = null;
        ExpressionTempBean bean = EsbBeanFactory.getInstance().getDefaultServiceBean(clazz);
        if (bean != null) {
            String expression = EsbBeanFactory.getInstance().getDefaultServiceBean(clazz).getId();
            object = par(EsbKey + expression, clazz);
        }

        return object;
    }

    public static class EMacro implements Macro {
        public ServiceBean bean;

        public EMacro(ServiceBean bean) {
            this.bean = bean;
        }

        @Override
        public String doMacro() {
            return bean.getExpression();
        }
    }

    public static <T> T par(String expression, Class<T> clazz) {
        T obj = null;
        if (!init) {
            initBus();
        }

        if (expression != null && expression.startsWith(EsbKey)) {
            ExpressionTempBean bean = (ExpressionTempBean) EsbBeanFactory.getInstance().getEsbBeanById(expression);
            if (bean != null && bean.getDataType() != null) {
                obj = getContextRootById(expression, clazz);
            } else {
                obj = par(expression, ActionContext.getContext().getContextMap(), clazz);
            }

        } else {
            obj = par(expression, ActionContext.getContext().getContextMap(), clazz);
        }
        return obj;
    }

    public static <T> T par(String expression, Map context, Class<T> clazz) {
        if (!init) {
            initBus();
        }
        T o = par(expression, context, getContext(), clazz);
        return o;

    }

    private static Object getContextRootById(String objId) {
        return getContextRootById(objId, Object.class);
    }


    static JDSClientService getCurrJDSClient() {
        JDSContext context = JDSActionContext.getActionContext();
        JDSSessionFactory factory = new JDSSessionFactory(context);
        ConfigCode configCode = JDSActionContext.getActionContext().getConfigCode();
        String sessionId = JDSActionContext.getActionContext().getSessionId();
        JDSClientService client = null;
        try {
            client = factory.getClientService(configCode);
        } catch (Exception e) {

        }

        if (client == null && sessionId != null) {
            try {
                client = factory.getJDSClientBySessionId(sessionId, configCode);
            } catch (JDSException e) {
            }
        }
        return client;
    }

    private static Object runExpression(ExpressionTempBean bean) {
        Class clazz = null;
        try {
            clazz = ClassUtility.loadClass(bean.getClazz());
        } catch (ClassNotFoundException e) {
            //  e.printStackTrace();
        }

        if (clazz == null) {
            clazz = (Class) JDSExpressionParserManager.getExpressionTypeMap().get(bean.getId());
            if (clazz != null) {
                try {
                    clazz = ClassUtility.loadClass(clazz.getName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JDSExpressionParserManager.getExpressionTypeMap().put(bean.getId(), clazz);
        }


        Function function = (Function) JDSExpressionParserManager.getName2functionMap().get(bean.getReturntype());
        Object returnObject = null;
        String expression = bean.getExpressionArr();
        List<char[]> params = null;
        if (expression != null) {
            params = ParseTools.parseMethodOrConstructor(expression.toCharArray());
        }

        if (function != null && clazz != null) {
            try {
                Stack stack = new Stack();
                for (int k = 0; k < params.size(); k++) {
                    String param = new String(params.get(k));
                    stack.push(par(param));
                }
                function.run(stack);
                returnObject = stack.pop();
            } catch (Throwable e) {
                logger.error("ParseExpressionException:", e);
                e.printStackTrace();
            }

        }
        return returnObject;
    }

    private static <T> T getContextRootById(String objId, Class<T> clazz) {
        ExpressionTempBean bean = (ExpressionTempBean) EsbBeanFactory.getInstance().getEsbBeanById(objId);
        T o = null;
        if (bean != null && bean.getDataType() != null) {
            switch (bean.getDataType()) {
                case Action:
                    o = (T) runExpression(bean);
                    if (o == null) {
                        o = par(bean.getExpressionArr(), clazz);
                    }
                    break;
                case Context:
                    if (ActionContext.getContext().get(bean.getId()) == null) {
                        o = (T) runExpression(bean);
                        if (o == null) {
                            o = par(bean.getExpressionArr(), clazz);
                        }

                        ActionContext.getContext().put(bean.getId(), o);
                    }
                    o = (T) ActionContext.getContext().get(bean.getId());
                    break;
                case Static:
                    Map root = (Map) EsbFactory.getDefauleRoot();
                    if (!root.containsKey(EsbKey + bean.getId()) || root.get(EsbKey + bean.getId()) == null) {
                        o = (T) runExpression(bean);
                        if (o == null) {
                            o = par(bean.getExpressionArr(), clazz);
                        }

                        if (o != null) {
                            root.put(EsbKey + bean.getId(), o);
                        }
                    }
                    o = (T) root.get(EsbKey + bean.getId());
                    break;
                case Session:
                    if (ActionContext.getContext().getSession().get(bean.getId()) == null) {
                        o = (T) runExpression(bean);
                        if (o == null) {
                            o = par(bean.getExpressionArr(), clazz);
                        }

                        ActionContext.getContext().getSession().put(bean.getId(), o);
                    }
                    o = (T) ActionContext.getContext().getSession().get(bean.getId());
                    break;
                case Server:
                    String serverUrl = bean.getServerUrl();
                    if (serverUrl == null || serverUrl.equals("")) {
                        serverUrl = JDSConfig.getValue(bean.getId() + "." + "serverUrl");
                    }
                    if (serverUrl == null || serverUrl.equals("")) {
                        serverUrl = JDSConfig.getValue("serverUrl");
                    }
                    if (serverUrl != null && !serverUrl.equals("")) {
                        try {
                            switch (bean.getTokenType()) {
                                case user:
                                    RemoteClientFunction function = new RemoteClientFunction(getCurrJDSClient(), bean.getClazz(), serverUrl);
                                    o = (T) function.perform();
                                    break;
                                case admin:
                                    RemoteAdminFunction adminFunction = new RemoteAdminFunction(bean.getClazz(), serverUrl);
                                    o = (T) adminFunction.perform();
                                    break;
                                case guest:
                                    RemoteFunction gustfunction = new RemoteFunction(bean.getClazz(), serverUrl);
                                    o = (T) gustfunction.perform();
                                    break;
                                default:
                                    RemoteFunction defaultFunction = new RemoteFunction(bean.getClazz(), serverUrl);
                                    o = (T) defaultFunction.perform();
                                    break;
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        o = (T) runExpression(bean);
                        if (o == null) {
                            o = par(bean.getExpressionArr(), clazz);
                        }

                    }
                    break;
                default:
                    try {
                        throw new JDSException("service key :" + objId + " not found! place check esbConfig.", JDSException.ESBERROR);
                    } catch (JDSException e) {
                        e.printStackTrace();
                    }
                    break;

            }


        } else {
            try {
                throw new JDSException("service key :" + objId + " not found! place check esbConfig.", JDSException.ESBERROR);
            } catch (JDSException e) {
                e.printStackTrace();
            }
        }
        return o;

    }

    public static Class guessType(String expression) {
        return guessType(expression, null, null);
    }

    public static Class guessType(String expression, Map context, Object obj) {
        Class type = findClass(expression);
        if (type == null) {
            if (obj == null) {
                obj = creatStaticBean(true);
            } else {
                if (!(obj instanceof StaticBean)) {
                    obj = Mixin.createBean(new Object[]{obj, creatStaticBean(true)});
                }
            }
            ExpressionCompiler compiler = new ExpressionCompiler(expression, getParserContext());
            type = compiler.getReturnType();
        }


        return type;
    }

    public static ParserContext getParserContext() {
        ParserContext context = (ParserContext) JDSActionContext.getActionContext().getContext().get("ParserContext");
        if (context == null) {
            context = new ParserContext();
            List<? extends ServiceBean> serviceBeans = EsbBeanFactory.getInstance().getServiceBeanByFlowType(EsbFlowType.function);
            for (ServiceBean serviceBean : serviceBeans) {
                try {
                    Class clazz = ClassUtility.loadClass(serviceBean.getClazz());
                    Method[] methods = clazz.getDeclaredMethods();
                    for (Method method : methods) {
                        if (Modifier.isStatic(method.getModifiers())) {
                            context.addImport(method.getName().toLowerCase(), new MethodStub(method));
                        }

                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            for (Class clazz : JSONClass) {
                context.addImport(clazz.getSimpleName(), clazz);
            }
            context.addImport("Math", Math.class);
            Map<String, Interceptor> interceptors = new HashMap<String, Interceptor>();
            interceptors.put("Debug", new DebugInterceptor());
            context.setInterceptors(interceptors);
            JDSActionContext.getActionContext().getContext().put("ParserContext", context);
        }

        return context;
    }

    public static String macrosExpression(String expression, Set<String> contextKeySet) {
        if (contextKeySet == null) {
            contextKeySet = new HashSet<>();
        }
        Map<String, Macro> myMacros = new HashMap<String, Macro>();
        List<? extends ServiceBean> beans = EsbBeanFactory.getInstance().getServiceBeanByFlowType(EsbFlowType.localAction);
        for (ServiceBean serviceBean : beans) {
            myMacros.put("$" + serviceBean.getId(), new EMacro(serviceBean));
        }
        Set<String> actionKeySet = JDSActionContext.getActionContext().getContext().keySet();
        for (String contextKey : actionKeySet) {
            myMacros.put(contextKey, new CTXMacro(contextKey));
        }

        for (String contextKey : contextKeySet) {
            myMacros.put(contextKey, new CustomMacro(contextKey));
        }
        MacroProcessor macroProcessor = new MacroProcessor();
        macroProcessor.setMacros(myMacros);
        String parsedExpression = macroProcessor.parse(expression);
        return parsedExpression;
    }

    /**
     * 动态生成 serviceBean
     *
     * @param expression 表达式，Mvel 表达式。如 GetClientService("net.ooder.iot.api.AdminAPI","http://service.tujiasmart.com:82")
     * @param context    上下文
     * @param source     未知.
     * @return
     */
    public static <T> T par(String expression, Map context, Object source, Class<T> clazz) {
        T o = null;
        StaticBean root = creatStaticBean(false);
        root.setSource(source);
        if (context == null || context.isEmpty()) {
            context = new HashMap();
        }
        String parsedExpression = macrosExpression(expression, null);
        final ParserContext ctx = getParserContext();
        ExpressionCompiler compiler = new ExpressionCompiler(parsedExpression, ctx);
        Serializable s = compiler.compile();
        if (clazz == null) {
            clazz = (Class<T>) Object.class;
        }

        try {
            o = MVEL.executeExpression(s, root, context, clazz);
        } catch (Throwable thrown) {
            throw thrown;
        }

        if (o != null) {
            try {
                Field[] fields = o.getClass().getDeclaredFields();
                for (Field field : fields) {
                    EsbObj param = field.getAnnotation(EsbObj.class);
                    if (param != null) {
                        String expressionStr = param.id();
                        if (expressionStr == null || expressionStr.equals("")) {
                            if (EsbBeanFactory.getInstance().getDefaultServiceBean(field.getType()) != null) {
                                expressionStr = EsbBeanFactory.getInstance().getDefaultServiceBean(field.getType()).getId();
                            }
                        }
                        Object object = par(EsbKey + expressionStr, field.getType());
                        field.set(o, object);
                    }

                    Autowired autowired = field.getAnnotation(Autowired.class);
                    if (param != null) {
                        String expressionStr = param.id();
                        if (expressionStr == null || expressionStr.equals("")) {
                            if (EsbBeanFactory.getInstance().getDefaultServiceBean(field.getType()) != null) {
                                expressionStr = EsbBeanFactory.getInstance().getDefaultServiceBean(field.getType()).getId();
                            }
                        }
                        Object object = par(EsbKey + expressionStr, field.getType());
                        field.set(o, object);
                    }

                    EsbRequest request = field.getAnnotation(EsbRequest.class);
                    if (request != null && field.getType().equals(String.class)) {
                        String expressionStr = request.expressionArr();
                        if (expressionStr != null && !expressionStr.equals("")) {
                            Object object = par(EsbKey + expressionStr, field.getType());
                            field.set(o, object);
                        } else {
                            Object object = JDSActionContext.getActionContext().getParams(field.getName());
                            field.set(o, object);
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }

        return o;

    }


    static void loadStaticAllData(boolean reload) throws
            InstantiationException, IllegalAccessException, ClassNotFoundException {
        JDSExpressionParserManager.loadStaticAllData(reload);

    }


    @SuppressWarnings("unchecked")
    public synchronized static StaticBean creatStaticBean(boolean reload) {

        if (esbContext == null || reload) {
            Class iface = EsbFactory.getEsbContext();
            MethodInterceptor staticMethodInterceptor = new MethodInterceptor() {
                public Object intercept(Object obj, /*proxyObject*/
                                        Method method, /*proxyMethod*/
                                        Object[] objects, /*proxyMethodParams*/
                                        MethodProxy proxy /*somethingOther?*/) {

                    String name = method.getName();
                    Object returnObject = null;
                    final Map<String, Function> nameMap = JDSExpressionParserManager.getName2functionMap();

                    if (name.equals("setSource")) {
                        if (objects != null && objects.length == 1) {
                            JDSActionContext.getActionContext().getContext().put(ESBMVELSOURCE, objects[0]);
                        }
                    } else if (name.equals("getSource")) {
                        returnObject = JDSActionContext.getActionContext().getContext().get(ESBMVELSOURCE);
                    } else if (nameMap.containsKey(name)) {
                        if ((objects == null || objects.length == 0) && name.startsWith("$")) {
                            returnObject = getContextRootById(name);
                        } else {
                            Function function = nameMap.get(name);
                            Stack stack = new Stack();
                            // put the proxy method parameter to stack
                            for (int k = 0; k < objects.length; k++) {
                                stack.push(objects[k]);
                            }
                            try {
                                function.run(stack);
                                returnObject = stack.pop();
                            } catch (Throwable e) {
                                logger.error("ParseExpressionException:", e);
                                e.printStackTrace();
                            }

                        }
                    } else if (name.equals("ctx")) {
                        return JDSActionContext.getActionContext().getContext();
                    }

                    return returnObject;
                }
            };

            esbContext = Enhancer.create(
                    Object.class/*superClass*/,
                    new Class[]{iface, StaticBean.class} /* interface to implement */,
                    staticMethodInterceptor/*callbackMethod to proxy real call*/
            );
        }
        return (StaticBean) esbContext;
    }

    private static synchronized Class findClass(String expression) {
        if (expression.startsWith(EsbKey)) {
            expression = expression.substring(1);
        }
        Class clazz = (Class) JDSExpressionParserManager.getExpressionTypeMap().get(expression);
        if (clazz == null) {
            Object obj = null;
            try {
                obj = MVEL.eval(expression);
                clazz = obj.getClass();
            } catch (Exception e) {
                clazz = Object.class;
            }
        }

        return clazz;
    }


    private static synchronized void addSignature(InterfaceMaker im, ExpressionTempBean bean) {
        Class clazz = (Class) JDSExpressionParserManager.getExpressionTypeMap().get(bean.getId());

        Function function = (Function) JDSExpressionParserManager.getName2functionMap().get(bean.getReturntype());

        if (clazz != null && function != null) {

            Signature sig = null;
            List<char[]> params = null;
            String expression = bean.getExpressionArr();
            if (expression != null) {
                params = ParseTools.parseMethodOrConstructor(expression.toCharArray());
            }

            Class[] classs = new Class[function.getNumberOfParameters()];
            if (params == null || function.getNumberOfParameters() < params.size()) {
                logger.warn("[" + bean.getId() + "=" + bean.getExpressionArr() + "] in " + function.getClass() + " NumberOfParameters=" + function.getNumberOfParameters());
                return;
            }

            Type retuntype = TypeUtils.getType(clazz.getName());
            try {
                for (int k = 0; k < params.size(); k++) {
                    String param = new String(params.get(k));
                    if (param.indexOf("(") > -1) {
                        classs[k] = findClass(new String(ParseTools.subset(param.toCharArray(), 0, ArrayTools.findFirst('(', 0, param.length(), param.toCharArray()))));
                    } else {
                        classs[k] = findClass(param);
                    }
                }
            } catch (Exception e) {
                logger.warn("[" + bean.getId() + "=" + bean.getExpressionArr() + "]" + e.getMessage());
            }

            if (function.getNumberOfParameters() > params.size()) {
                logger.warn("[" + bean.getId() + "=" + bean.getExpressionArr() + "]" + " NumberOfParameters=" + function.getNumberOfParameters());
                for (int k = params.size(); k < function.getNumberOfParameters(); k++) {
                    classs[k] = Object.class;
                }
            }

            Type[] argumentTypes = TypeUtils.getTypes(classs);
            if (retuntype != null) {

                if (bean.getReturntype() == null || bean.getReturntype().equals("")) {
                    bean.setReturntype(bean.getId());
                }
                sig = new Signature(bean.getReturntype().trim(), retuntype, argumentTypes);

                if (!signatureMap.containsValue(sig)) {
                    im.add(sig, null);
                    signatureMap.put(bean.getReturntype(), sig);
                }
                if (!bean.getId().contains(".")) {
                    Type[] nullargumentTypes = new Type[0];

                    Signature getSig = new Signature("get" + EsbKey + bean.getId(), retuntype, nullargumentTypes);
                    if (!signatureMap.containsValue(getSig)) {
                        im.add(getSig, null);
                        signatureMap.put("get" + EsbKey + bean.getId(), getSig);
                    }

                    Signature $sig = new Signature(EsbKey + bean.getId(), retuntype, nullargumentTypes);
                    if (!signatureMap.containsValue($sig)) {
                        im.add($sig, null);
                        signatureMap.put(EsbKey + bean.getId(), getSig);
                    }
                }
            }
        }
    }


    public static synchronized Class getEsbContext() {

        if (contextClass == null) {
            ContextType[] skipType = new ContextType[]{ContextType.Server, ContextType.Command, ContextType.Function};
            InterfaceMaker im = new InterfaceMaker();
            EsbBeanFactory esbfactory = EsbBeanFactory.getInstance();
            List<? extends ServiceBean> expressionList = esbfactory.loadAllServiceBean();
            for (int i = 0; i < expressionList.size(); i++) {
                if ((expressionList.get(i) instanceof ExpressionTempBean)) {
                    ExpressionTempBean bean = (ExpressionTempBean) expressionList.get(i);
                    if (bean != null && !Arrays.asList(skipType).contains(bean.getDataType())) {
                        addSignature(im, bean);
                    }
                }
            }
            im.setClassLoader(Thread.currentThread().getContextClassLoader());
            contextClass = im.create();
        }

        return contextClass;
    }

    static synchronized Object getContext() {
        ActionContext ctx = ActionContext.getContext();
        Object esbContext = ctx.get("EsbContext");
        if (esbContext == null) {
            esbContext = EsbFactory.creatStaticBean(false);
            ctx.put("EsbContext", esbContext);
        }
        return esbContext;

    }

    /****
     * @return
     */
    public static Object getDefauleRoot() {
        return CacheManagerFactory.createCache(JDSConstants.ORGCONFIG_KEY, ESBConstants.staticName);
    }

}
