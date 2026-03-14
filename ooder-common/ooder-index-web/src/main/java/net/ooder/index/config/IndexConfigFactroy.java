/**
 * $RCSfile: IndexConfigFactroy.java,v $
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
package net.ooder.index.config;

import com.alibaba.fastjson.JSONObject;
import net.ooder.annotation.JLuceneIndex;
import net.ooder.config.JDSConfig;
import net.ooder.annotation.ClassMappingAnnotation;
import net.ooder.index.config.bean.*;
import net.ooder.index.config.type.JDocumentType;
import net.ooder.index.config.type.JFieldType;
import net.ooder.index.config.type.JPathType;
import net.sf.cglib.beans.BeanMap;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class IndexConfigFactroy {
    static String vfsRootPath = "cdiskroot/";
    public static String jsonkey = "JSONVALUE#";
    public static String createtime = "createtime";
    public static String updatetime = "updatetime";
    public static String uuid = "uuid";
    public static String indexRootPathStr = "index";
    public static String tempRootPathStr = "temp";
//    static Path indexRootPath = Paths.get(JDSConfig.Config.dataPath().getAbsolutePath() + File.separator + indexRootPathStr);
//    static Path tempRootPath = Paths.get(JDSConfig.Config.dataPath().getAbsolutePath() + File.separator + tempRootStr);

    private final static String THREAD_LOCK = "Thread Lock";

    private static IndexConfigFactroy instance;

    public static IndexConfigFactroy getInstance() {
        if (instance == null) {
            synchronized (THREAD_LOCK) {
                if (instance == null) {
                    instance = new IndexConfigFactroy();
                }
            }
        }
        return instance;
    }

    IndexConfigFactroy() {

        initPath(Paths.get(JDSConfig.Config.dataPath().getAbsolutePath() + File.separator + indexRootPathStr));
        initPath(Paths.get(JDSConfig.Config.dataPath().getAbsolutePath() + File.separator + tempRootPathStr));

    }

    /**
     * 获取本地索引地址
     *
     * @param
     * @return
     */
    private void initPath(Path path) {
        if (Files.notExists(path)) {
            try {
                path = Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public Path getIndexRootPath() {
        return Paths.get(JDSConfig.Config.dataPath().getAbsolutePath() + File.separator + indexRootPathStr);
    }

    public Path getTempRootPath() {
        return Paths.get(JDSConfig.Config.dataPath().getAbsolutePath() + File.separator + tempRootPathStr);
    }


    /**
     * 获取LUCENE 注解配置
     *
     * @param
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public JLucene getJLuceneConfig(JLuceneIndex luceneIndex, Map valueMap)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {

        JLucene luceneConfig = new JLuceneBean();
        Class clazz = luceneIndex.getClass();
        Annotation annotation = clazz.getAnnotation(JDocumentType.class);
        JDocument document = null;
        try {
            document = (JDocument) this.getIndexBean(annotation);
            luceneConfig.setVfsValid(document.isVfsValid());
            luceneConfig.setIndexValid(document.isIndexValid());
            String json = JSONObject.toJSONString(luceneIndex);

            luceneConfig.setJson(json);

            JFSDirectory fsDirectory = document.getFsDirectory();
            String path = luceneIndex.getPath() == null ? luceneConfig.getVfsJson().getPath() : luceneIndex.getPath();
            fsDirectory.setPath(indexRootPathStr + File.separator + path);
            fsDirectory.setTempPath(tempRootPathStr + File.separator + path);


            fsDirectory.setVfsPath(vfsRootPath + path);
            luceneConfig.setFsDirectory(fsDirectory);

            VFSJson vfsjson = document.getVfsJson();
            vfsjson.setPath(indexRootPathStr + File.separator + vfsjson.getPath());
            vfsjson.setVfsPath(vfsRootPath + vfsjson.getVfsPath() + path);
            luceneConfig.setVfsJson(vfsjson);

            luceneConfig.setIndexWriter(document.getIndexWriter());

            luceneConfig.setName(document.getName());


            List<JField> jfields = new ArrayList<JField>();
            BeanMap beanMap = BeanMap.create(luceneIndex);
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                JField jfield = (JField) getFieldBean(field);
                if (jfield != null) {
                    jfield.setValue(beanMap.get(field.getName()));
                    jfields.add(jfield);
                    if (jfield.getHighlighter()) {
                        if (!luceneConfig.getHighFields().contains(jfield.getName())) {
                            luceneConfig.getHighFields().add(jfield.getName());
                        }
                    }
                }
            }


            if (valueMap != null) {

                Iterator<String> it = valueMap.keySet().iterator();

                while (it.hasNext()) {
                    String key = it.next();
                    JFieldBean indexBean = new JFieldBean();
                    indexBean.setClazz(valueMap.get(key).getClass());
                    indexBean.setId(key);
                    indexBean.setName(key);
                    indexBean.setValue(valueMap.get(key));
                    indexBean.setStore(org.apache.lucene.document.Field.Store.YES);
                    jfields.add(indexBean);
                }
            }

            luceneConfig.setUuid((String) valueMap.get(IndexConfigFactroy.uuid));


            luceneConfig.setFields(jfields);

        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }

        return luceneConfig;

    }


    /**
     * 获取LUCENE 注解配置
     *
     * @param
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public JLucene getJLuceneConfig(JLuceneIndex luceneIndex)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return getJLuceneConfig(luceneIndex, new HashMap());

    }

    /**
     * 获取注解对应实体对象[支持递归注入]
     *
     * @param annotation
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    private JIndexBean getIndexBean(Annotation annotation)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class enumType = annotation.annotationType();

        Map<String, Object> valueMap = new HashMap<String, Object>();

        ClassMappingAnnotation classMappingAnnotation = annotation.annotationType().getAnnotation(ClassMappingAnnotation.class);
        if (classMappingAnnotation != null) {
            for (int k = 0; k < enumType.getDeclaredMethods().length; k++) {
                Method method = enumType.getDeclaredMethods()[k];
                if (method.getReturnType().isAnnotation() && method.getReturnType().getAnnotation(ClassMappingAnnotation.class) != null) {
                    Annotation childAnnotation = (Annotation) method.invoke(annotation, null);
                    valueMap.put(method.getName(), getIndexBean(childAnnotation));
                } else if (method.getReturnType().isArray()) {
                    Object[] objs = (Object[]) method.invoke(annotation, null);
                    Object[] beans = new Object[objs.length];
                    if (objs.length > 0) {
                        for (int f = 0; f < objs.length; f++) {
                            if (objs[f] instanceof Annotation) {
                                Annotation obj = (Annotation) objs[f];
                                if (obj.annotationType().getAnnotation(ClassMappingAnnotation.class) != null) {
                                    Object panel = getIndexBean(obj);
                                    beans[f] = panel;
                                } else {
                                    beans[f] = obj;
                                }
                            } else {
                                beans[f] = objs[f];
                            }
                        }
                        valueMap.put(method.getName(), beans);
                    }

                } else {
                    valueMap.put(method.getName(), method.invoke(annotation, null));
                }
            }
        }
        JIndexBean indexBean = (JIndexBean) classMappingAnnotation.clazz().newInstance();

        BeanMap beanMap = BeanMap.create(indexBean);
        beanMap.putAll(valueMap);
        return indexBean;
    }

    private JIndexBean getFieldBean(Field field)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Annotation annotation = field.getAnnotation(JFieldType.class);
        JField fieldBean = null;
        if (annotation != null) {
            fieldBean = (JField) getIndexBean(annotation);
            fieldBean.setId(field.getName());
            fieldBean.setClazz(field.getType());
            if (fieldBean.getName() == null || fieldBean.getName().equals("")) {
                fieldBean.setName(field.getName());
            }

        }

        return fieldBean;
    }

    private JPath getMethodBean(Method method)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {

        Annotation annotation = method.getAnnotation(JPathType.class);
        JPath pathBean = null;
        if (annotation != null) {
            pathBean = (JPath) getIndexBean(annotation);
            pathBean.setId(method.getName());
            pathBean.setClazz(method.getReturnType());

        }
        return pathBean;

    }

    public static void main(String[] args) {
    }

}


