/**
 * $RCSfile: EnumsUtil.java,v $
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
package net.ooder.util;

import net.ooder.annotation.AttributeName;
import net.ooder.annotation.Enums;
import net.ooder.annotation.EnumsAttribute;
import net.ooder.annotation.Enumstype;
import net.ooder.config.MenuCfg;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class EnumsUtil {

    public final String methodName = "values";

    public static EnumsAttribute getAttribute(AttributeName enums) {
        EnumsAttribute attribute = new EnumsAttribute();
        if (enums.getClazz() != null) {
            attribute.setAttributes(values(enums.getClazz()));
        }
        attribute.setDisplayName(enums.getDisplayName());
        attribute.setName(enums.getName());
        attribute.setCode(enums.getDisplayName());
        return attribute;
    }

    public static <T extends Enumstype> T getEnums(Class<T> enmuClass, String type) {
        Method method;
        try {
            method = enmuClass.getDeclaredMethod("values", null);
            T[] enums = (T[]) method.invoke(null, null);
            for (T enuminstance : enums) {
                if (type.equals(enuminstance.getType())) {
                    return (T) enuminstance;
                }
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }


    public static <T extends Enums> T[] getEnums(Class<T> enmuClass) {
        Method method;
        T[] enums = null;
        try {
            method = enmuClass.getDeclaredMethod("values", null);
            enums = (T[]) method.invoke(null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return enums;

    }

    public static MenuCfg getMenuCfg(Class<? extends Enums> enmuClass) {
        MenuCfg menu = new MenuCfg();

        menu.setMenu(getChildMenu(enmuClass));
        return menu;
    }


    private static List<MenuCfg> getChildMenu(Class enmuClass) {

        List<MenuCfg> meuns = new ArrayList<MenuCfg>();

        try {
            Method method = enmuClass.getDeclaredMethod("values", null);
            Enumstype[] enums = (Enumstype[]) method.invoke(null, null);

            for (Enumstype enuminstance : enums) {
                MenuCfg menu = new MenuCfg();
                menu.setText(enuminstance.getName());
                menu.setId(enuminstance.getType());
                meuns.add(menu);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return meuns;

    }

    private static Object[] values(Class enmuClass) {

        List<String[]> attributes = new ArrayList<String[]>();

        try {

            Method method = enmuClass.getDeclaredMethod("values", null);
            Enumstype[] enums = (Enumstype[]) method.invoke(null, null);
            for (Enumstype enuminstance : enums) {
                String[] atttibute = new String[2];
                atttibute[0] = enuminstance.getName();
                atttibute[1] = enuminstance.getType();
                attributes.add(atttibute);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return attributes.toArray(new Object[]{attributes.size()});

    }

}
