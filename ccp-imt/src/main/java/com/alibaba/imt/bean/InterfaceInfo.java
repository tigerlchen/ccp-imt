/**
 * Project: imt
 * 
 * File Created at 2012-9-17
 * $Id: InterfaceInfo.java 471736 2013-03-06 08:55:11Z admin.for.perth $
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
package com.alibaba.imt.bean;


public class InterfaceInfo {

    private String key;

    private String className;
    
    private String internalClassName;
    
    private Class<?> clazz;
    
    private String methodName;
    
    private String methodDesc;

    private Class<?> returnClass;

    private Class<?>[] argumentClasses;
    
    private String[] datas;
    
    private Object[] additionalDatas;

    private ImtInfo imtInfo;
    
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getInternalClassName() {
        return internalClassName;
    }

    public void setInternalClassName(String internalClassName) {
        this.internalClassName = internalClassName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodDesc() {
        return methodDesc;
    }

    public void setMethodDesc(String methodDesc) {
        this.methodDesc = methodDesc;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getReturnClass() {
        return returnClass;
    }

    public void setReturnClass(Class<?> returnClass) {
        this.returnClass = returnClass;
    }

    public Class<?>[] getArgumentClasses() {
        return argumentClasses;
    }

    public void setArgumentClasses(Class<?>... argumentClasses) {
        this.argumentClasses = argumentClasses;
    }

	public String[] getDatas() {
        return datas;
    }

    public void setDatas(String... datas) {
        this.datas = datas;
    }

    public Object[] getAdditionalDatas() {
        return additionalDatas;
    }

    public void setAdditionalDatas(Object... additionalDatas) {
        this.additionalDatas = additionalDatas;
    }

	public ImtInfo getImtInfo() {
		return imtInfo;
	}

	public void setImtInfo(ImtInfo imtInfo) {
		this.imtInfo = imtInfo;
	}

}
