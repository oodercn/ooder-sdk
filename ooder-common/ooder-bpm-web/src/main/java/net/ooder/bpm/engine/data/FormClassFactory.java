package net.ooder.bpm.engine.data;

import net.ooder.bpm.client.data.FormClassBean;
import net.ooder.bpm.client.data.FormClassManager;
import net.ooder.common.ContextType;
import net.ooder.common.JDSBusException;
import net.ooder.common.JDSException;
import net.ooder.common.util.ClassUtility;
import net.ooder.common.util.CnToSpell;
import net.ooder.common.util.FileUtility;
import net.ooder.common.util.StringUtility;
import net.ooder.config.JDSConfig;
import net.ooder.context.JDSActionContext;
import net.ooder.context.JDSContext;
import net.ooder.esb.config.manager.EsbBean;
import net.ooder.esb.config.manager.EsbBeanConfig;
import net.ooder.esb.config.manager.EsbBeanFactory;

import net.ooder.esb.config.manager.ExpressionTempBean;
import net.ooder.org.conf.OrgConstants;
import net.ooder.vfs.FileInfo;
import net.ooder.vfs.Folder;
import net.ooder.vfs.ct.CtVfsFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author wenzhangli
 */
public class FormClassFactory {
    public List<FormClassBean> formClassList = new ArrayList<FormClassBean>();
    private EsbBeanFactory factory;

    public static FormClassFactory getInstance() throws InstantiationException, IllegalAccessException, ClassNotFoundException, JDSBusException {
        return new FormClassFactory();
    }


    private List<FormClassBean> getVfsFormClassBeanListByPath(String path, List<FormClassBean> formList) {


        try {
            Folder folder =CtVfsFactory.getCtVfsService().getFolderByPath(path);

            if (folder != null) {
                List<FileInfo> files = folder.getFileList();
                for (int k = 0; k < files.size(); k++) {
                    FileInfo fileInfo = files.get(k);
                    FormClassBean formClassBean = null;
                    try {
                        formClassBean = FormClassFactory.getInstance().vfsfile2FormClassbean(fileInfo.getPath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (formClassBean != null) {
                        formList.add(formClassBean);
                    }

                }

            }
        } catch (JDSException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return formList;
    }

    public FormClassFactory() throws InstantiationException, IllegalAccessException, ClassNotFoundException, JDSBusException {

        this.factory = EsbBeanFactory.getInstance();
        EsbBeanConfig esbBeanListBean = factory.getEsbBeanConfig();
        Map<String, EsbBean> esbBeanMap = esbBeanListBean.getEsbBeanMap();
        Iterator<String> it = esbBeanMap.keySet().iterator();
        for (; it.hasNext(); ) {
            String key = it.next();
            EsbBean esbBean = esbBeanMap.get(key);
            if (esbBean.getType().equals(ContextType.Context)) {
                String className = esbBean.getFormClassManager();
                FormClassManager formClassManager = null;
                try {
                    formClassManager = (FormClassManager) Class.forName(className).newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                formClassList.addAll(formClassManager.getAllFormClassList(key));
            }
        }
    }

    public FormClassBean getFormClassBeanInst(String id) {
        ExpressionTempBean expressionTempBean = (ExpressionTempBean) factory.getIdMap().get(id);
        FormClassBean formClassBean = null;
        if (expressionTempBean != null) {
            formClassBean = tempBean2FormClassbean(expressionTempBean);
        }
        return formClassBean;

    }

    public FormClassBean getFormClassByName(String name) {

        ExpressionTempBean expressionTempBean = (ExpressionTempBean) factory.getServiceBeanByName(name).get(0);
        return tempBean2FormClassbean(expressionTempBean);
    }

    public List getAllFormClassList() {

        for (int k = 0; k < formClassList.size(); k++) {

        }
        return this.formClassList;
    }

    /**
     * @param expressionTempBean
     * @return
     */
    private FormClassBean tempBean2FormClassbean(ExpressionTempBean expressionTempBean) {

        FormClassBean formClassBean = new FormClassBean();
        formClassBean.setId(expressionTempBean.getId().trim());
        formClassBean.setDesc(expressionTempBean.getDesc().trim());
        formClassBean.setName(expressionTempBean.getName().trim());

        formClassBean.setJspUrl(formatFilePath(expressionTempBean.getJspUrl().trim()));
        if (expressionTempBean.getJspUrl() != null && expressionTempBean.getJspUrl().trim().length() > 0) {
            String absolutePath = JDSConfig.getJDSHomeAbsolutePath(expressionTempBean.getJspUrl().trim().substring(1, expressionTempBean.getJspUrl().trim().length()));
            formClassBean.setPath(absolutePath.substring(1, absolutePath.length()));
        }

        return formClassBean;
    }


    /**
     * @param vfsPath
     * @return FormClassBean
     * @throws JDSException
     */
    public FormClassBean vfsfile2FormClassbean(String vfsPath) throws JDSException {
        JDSContext context = JDSActionContext.getActionContext();
        FormClassBean formClassBean =null;
        FileInfo fileInfo = CtVfsFactory.getCtVfsService().getFileByPath(vfsPath);
        if (fileInfo!=null){
            String fileName = fileInfo.getName();
            if (fileName.indexOf(".") > -1) {
                fileName = fileName.substring(0, fileInfo.getName().indexOf("."));
            }

            // String fileName=fileInfo.getName().substring(0, fileInfo.getName().indexOf("."));
             formClassBean = new FormClassBean();
            formClassBean.setId(CnToSpell.getFullSpell(fileName));
            formClassBean.setDesc(fileName);
            formClassBean.setPath(fileInfo.getPath());
            formClassBean.setName(fileInfo.getDescription() == null ? fileInfo.getName() : fileInfo.getDescription());
            //formClassBean.setJspUrl(StringUtility.replace(fileInfo.getPath(), OrgConstants.WORKFLOWBASEPATH,""));
        }



        return formClassBean;
    }

    /**
     * @param filePath
     * @return
     */
    public FormClassBean fileBean2FormClassbean(String filePath) {
        File file = new File(filePath);

        String fileName = file.getName().substring(0, file.getName().indexOf("."));
        FormClassBean formClassBean = new FormClassBean();
        formClassBean.setId(CnToSpell.getFullSpell(fileName));
        formClassBean.setDesc(fileName);
        formClassBean.setPath(filePath);
        formClassBean.setName(fileName);
        // formClassBean.setJspUrl(formatFilePath(getFormRunTimePath(file.getAbsolutePath())));

        return formClassBean;
    }

    private String formatFilePath(String path) {
        path = StringUtility.replace(path, "\\", "/");
        return path;
    }

    private static String getJspRunTimePath(String srcTplPath) {
        String path = null;
        try {
            path = null;
            File file = new File(srcTplPath);
            if (file.exists() && file.isFile()) {
                path = file.getCanonicalPath().replace(file.getName(), "") + CnToSpell.getFullSpell(file.getName()) + ".jsp";
                String runtimePath = new File(JDSConfig.getJDSHomeAbsolutePath("")).getCanonicalPath();
                String serverHome = new File(JDSConfig.getServerHome()).getCanonicalPath();
                String shome = toSeparatorChar(serverHome);
                path = StringUtility.replace(path, shome, runtimePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return toSeparatorChar(path);
    }

    public static String getFormRunTimePath(String srcTplPath) {
        String path = null;
        try {
            path = null;
            File file = new File(srcTplPath);
            if (file.exists() && file.isFile()) {

                path = getJspRunTimePath(srcTplPath);

                try {
                    File jspfile = new File(path);
                    if (!jspfile.exists()) {

                        // Persistor persistor = new Persistor(srcTplPath);
                        // persistor.genPid("component");

                        Class persistorclazz = ClassUtility.loadClass("net.ooder.fdt.define.designer.Persistor");
                        Constructor con = persistorclazz.getConstructor(new Class[]{String.class});
                        Object persistor = con.newInstance(new Object[]{srcTplPath});
                        Method genPidmethod = persistorclazz.getMethod("genPid", new Class[]{String.class});
                        genPidmethod.invoke(persistor, new Object[]{"component"});

                        // Form form = new Form();
                        // form.build(srcTplPath);
                        Class formclazz = ClassUtility.loadClass("net.ooder.fdt.define.designer.Form");
                        Constructor formcon = formclazz.getConstructor();
                        Object form = formcon.newInstance();
                        Method buildmethod = formclazz.getMethod("build", new Class[]{String.class});
                        buildmethod.invoke(form, new Object[]{srcTplPath});

                        // persistor.flushAndGenjsp(form);
                        Method flushAndGenjsp = persistorclazz.getMethod("flushAndGenjsp", new Class[]{formclazz});
                        flushAndGenjsp.invoke(persistor, new Object[]{form});
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String runtimePath = new File(JDSConfig.getJDSHomeAbsolutePath("")).getCanonicalPath();
                path = StringUtility.replace(path, runtimePath, "");
                path = StringUtility.replace(path, File.separator, "/");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    private static String getVfsJspRunTimePath(String srcTplPath) {
        String path = srcTplPath + ".jsp";
        try {
            String runtimePath = new File(JDSConfig.getJDSHomeAbsolutePath("")).getCanonicalPath();

            runtimePath = runtimePath + "/" + path;

            runtimePath = toSeparatorChar(runtimePath);

            File jspfile = new File(runtimePath);
            // if (!jspfile.exists()) {
            // Class persistorclazz;
            // persistorclazz = ClassUtility
            // .loadClass("net.ooder.fdt.define.designer.Persistor");
            // Constructor con = persistorclazz
            // .getConstructor(new Class[] { String.class });
            // Object persistor = con.newInstance(new Object[] { srcTplPath });
            // Method genPidmethod = persistorclazz.getMethod("genPid",
            // new Class[] { String.class });
            // genPidmethod.invoke(persistor, new Object[] { "component" });
            // Class formclazz = ClassUtility
            // .loadClass("net.ooder.fdt.define.designer.Form");
            // Constructor formcon = formclazz.getConstructor();
            // Object form = formcon.newInstance();
            // Method buildmethod = formclazz.getMethod("build",
            // new Class[] { String.class });
            // buildmethod.invoke(form, new Object[] { srcTplPath });
            // Method flushAndGenjsp = persistorclazz.getMethod(
            // "flushAndGenjsp", new Class[] { formclazz });
            // flushAndGenjsp.invoke(persistor, new Object[] { form });
            //
            // }
            //

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (path != null && !path.startsWith("/")) {
            path = "/" + path;
        }
        path = StringUtility.replace(path, "\\", "/");
        return path;
    }


//    public VFSClientService getVfsClient() {
//        VFSClientService vfsClient = (VFSClientService) EsbUtil.parExpression("$VFSClientService");
//        return vfsClient;
//    }

    private static String getRunTimePath(String fn) {
        String dir = FileUtility.getPath(fn, '\\');
        String fileName = fn.substring(dir.length() + 1, fn.length());
        String runTimePath = dir + "/" + fileName.substring(0, fileName.indexOf(".")) + "" + fileName.substring(fileName.indexOf("."), fileName.length());
        return toSeparatorChar(runTimePath);
    }

    private static String toSeparatorChar(String url) {
        String l = url.replace('/', File.separatorChar);
        l = l.replace(File.separatorChar + File.separator, File.separator);
        return l;

    }
}
