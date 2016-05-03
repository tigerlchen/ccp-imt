/**
 * Project: imt
 * 
 * File Created at 2012-11-6
 * $Id: SpringBeanAdapter.java 21 2012-11-07 07:29:03Z yourxiaobai@gmail.com $
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
package com.alibaba.imt.support.spring.adapter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.alibaba.imt.adapter.BeanAdapter;

/**
 * @author yansong.baiys
 *
 */
public class SpringBeanAdapter implements BeanAdapter, ApplicationContextAware{
	private ApplicationContext context;
    private Map<String,Object> beanMap = new HashMap<String,Object>();
    @Override
    public Object getObject(Class<?> clazz, Object[] beanDatas) {
        
        return beanMap.get((String)beanDatas[0]);
    }

    @Override
    public Object[] getBeanDatas(Class<?> clazz) {
        String[] bdns = context.getBeanDefinitionNames();
        Map<String,Object> tempBeanMap = new HashMap<String,Object>();
        for(String bdn : bdns){
            try{
                Object bean = context.getBean(bdn);
                if(clazz.equals(bean.getClass())){
                    tempBeanMap.put(bdn, bean);
                }else if(AopUtils.isAopProxy(bean)){
                    Object target = getTarget(bean);
                    if(target.getClass().equals(clazz)){
                        tempBeanMap.put(bdn,target);
                        tempBeanMap.put(bdn + "_proxy",bean);
                    }
                }
            }catch(Exception ignore){
                //There are abstract classes when getBean will throw exception, so we ignore them.
            }
        }
        beanMap.putAll(tempBeanMap);
        
        return tempBeanMap.keySet().toArray();
    }

    /**
     * @param bean
     * @return
     * @throws Exception 
     */
    private Object getTarget(Object proxy) throws Exception{
        Field h = null;
        if(AopUtils.isJdkDynamicProxy(proxy)){
            h = proxy.getClass().getSuperclass().getDeclaredField("h");  
        }else{
            h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        }
        h.setAccessible(true);  
        AopProxy aopProxy = (AopProxy) h.get(proxy);  
          
        Field advised = aopProxy.getClass().getDeclaredField("advised");  
        advised.setAccessible(true);  
          
        Object target = ((AdvisedSupport)advised.get(aopProxy)).getTargetSource().getTarget();  
          
        return target;  
    }

	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		this.context = context;
	}
}
