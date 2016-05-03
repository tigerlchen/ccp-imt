/**
 * Project: imt
 * 
 * File Created at 2012-9-17
 * $Id: Util.java 472718 2013-03-25 03:21:42Z admin.for.perth $
 * 
 * Copyright 1999-2100 Alibaba.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package com.alibaba.imt.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.imt.asm.Type;


/**
 * 
 * @author yansong.baiys
 */
public class Util {
    

    public static Map<String, Object> beanToMap(Object bean) {
        if (bean == null) return null;
        Map<String, Object> ret = new HashMap<String, Object>();
        try{
            Class<?> cls = bean.getClass();
            BeanInfo bi = Introspector.getBeanInfo(cls);
            for (PropertyDescriptor pd : bi.getPropertyDescriptors()) {
                if ("class".equals(pd.getName())) continue;
                Method readMethod = pd.getReadMethod();
                if (readMethod == null) {
                    continue;
                }
                ret.put(pd.getName(), readMethod.invoke(bean));
            }
        }catch(Exception e){
            throw new RuntimeException("Method beanToMap has cause a exception", e);
        }
        return ret;
    }


    public static <T> T mapToBean(Map<String, Object> map, Class<T> cls) {
        if (map == null || cls == null) return (T) null;
        T ret = null;
        try{
            ret = cls.newInstance();
            Map<String, Method> nameWriterMapping = resolveNameWriterMapping(cls);
            for (String key : map.keySet()) {
                Method writer = nameWriterMapping.get(key);
                if (writer == null) {
                    continue;
                }
                writer.invoke(ret, map.get(key));
            }
        }catch(Exception e){
            throw new RuntimeException("Method mapToBean has cause a exception", e);
        }
        return ret;
    }

    private static Map<String, Method> resolveNameWriterMapping(Class<?> cls) throws IntrospectionException {
        Map<String, Method> ret = new HashMap<String, Method>();
        BeanInfo bi = Introspector.getBeanInfo(cls);
        for (PropertyDescriptor pd : bi.getPropertyDescriptors()) {
            if ("class".equals(pd.getName())) continue;
            ret.put(pd.getName(), pd.getWriteMethod());
        }
        return ret;
    }
    
    public static Class<?> getClass(Type type){
        switch (type.getSort()) {
            case Type.VOID:
                return void.class;
            case Type.BOOLEAN:
                return boolean.class;
            case Type.CHAR:
                return char.class;
            case Type.BYTE:
                return byte.class;
            case Type.SHORT:
                return short.class;
            case Type.INT:
                return int.class;
            case Type.FLOAT:
                return float.class;
            case Type.LONG:
                return long.class;
            case Type.DOUBLE:
                return double.class;
            case Type.ARRAY:
                try {
                    return Class.forName(type.getInternalName().replace('/', '.'));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            case Type.OBJECT:
                try {
                    return Class.forName(type.getClassName());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            default:
                return null;
        } 
    }


    /**
     * @param className
     * @param methodName
     * @param methodDesc
     * @return
     */
    public static String generateKey(String className, String methodName, String methodDesc) {
        String key = className + "#" + methodName + "#" + methodDesc;
        return key;
    }


    /**
     * @param description
     * @return
     */
    public static String getMethodNameFromMethodExternalDesc(String description) {
        description = description.replaceAll("//s+", "");
        int lPrtIndex = description.indexOf("(");
        String methodName = description.substring(0, lPrtIndex);
        
        return methodName;
    }


    /**
     * @param description
     * @return
     */
    public static Class<?>[] getParameterTypesFromMethodExternalDesc(String description) {
        description = description.replaceAll("//s+", "");
        int lPrtIndex = description.indexOf("(");
        int rPrtIndex = description.indexOf(")");
        String argString = description.substring(lPrtIndex + 1, rPrtIndex);
        String[] argClassNames = argString.split(",");
        Class<?>[] argClasses = new Class<?>[argClassNames.length];
        for(int i = 0; i < argClassNames.length; i++){
            try {
                if(byte.class.getName().equals(argClassNames[i])){
                    argClasses[i] = byte.class;
                }else if(short.class.getName().equals(argClassNames[i])){
                    argClasses[i] = short.class;
                }else if(int.class.getName().equals(argClassNames[i])){
                    argClasses[i] = int.class;
                }else if(long.class.getName().equals(argClassNames[i])){
                    argClasses[i] = long.class;
                }else if(float.class.getName().equals(argClassNames[i])){
                    argClasses[i] = float.class;
                }else if(double.class.getName().equals(argClassNames[i])){
                    argClasses[i] = double.class;
                }else if(char.class.getName().equals(argClassNames[i])){
                    argClasses[i] = char.class;
                }else if(boolean.class.getName().equals(argClassNames[i])){
                    argClasses[i] = boolean.class;
                }else{
                    argClasses[i] = Class.forName(argClassNames[i]);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return argClasses;
    }

    
}
